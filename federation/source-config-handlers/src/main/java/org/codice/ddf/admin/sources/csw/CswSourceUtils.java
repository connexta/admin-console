/**
 * Copyright (c) Codice Foundation
 * <p>
 * This is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details. A copy of the GNU Lesser General Public License
 * is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 */
package org.codice.ddf.admin.sources.csw;

import static java.net.HttpURLConnection.HTTP_OK;
import static org.codice.ddf.admin.api.handler.ConfigurationMessage.createInternalErrorMsg;
import static org.codice.ddf.admin.api.services.CswServiceProperties.CSW_GMD_FACTORY_PID;
import static org.codice.ddf.admin.api.services.CswServiceProperties.CSW_PROFILE_FACTORY_PID;
import static org.codice.ddf.admin.api.services.CswServiceProperties.CSW_SPEC_FACTORY_PID;
import static org.codice.ddf.admin.commons.sources.SourceHandlerCommons.DISCOVERED_SOURCE;
import static org.codice.ddf.admin.commons.sources.SourceHandlerCommons.DISCOVERED_SOURCES;
import static org.codice.ddf.admin.commons.sources.SourceHandlerCommons.DISCOVERED_URL;
import static org.codice.ddf.admin.commons.sources.SourceHandlerCommons.SOURCES_NAMESPACE_CONTEXT;
import static org.codice.ddf.admin.commons.sources.SourceHandlerCommons.UNKNOWN_ENDPOINT;
import static org.codice.ddf.admin.commons.sources.SourceHandlerCommons.VERIFIED_CAPABILITIES;
import static org.codice.ddf.admin.commons.sources.SourceHandlerCommons.createCommonSourceConfigMsg;
import static org.codice.ddf.admin.commons.sources.SourceHandlerCommons.createDocument;
import static org.codice.ddf.admin.sources.csw.CswSourceConfigurationHandler.CSW_SOURCE_CONFIGURATION_HANDLER_ID;

import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.codice.ddf.admin.api.config.sources.CswSourceConfiguration;
import org.codice.ddf.admin.api.handler.report.ProbeReport;
import org.codice.ddf.admin.commons.requests.RequestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.google.common.collect.ImmutableList;

public class CswSourceUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(CswSourceUtils.class);

    public static final String GET_CAPABILITIES_PARAMS = "?service=CSW&request=GetCapabilities";

    private static final List<String> URL_FORMATS = ImmutableList.of("https://%s:%d/services/csw",
            "https://%s:%d/csw",
            "http://%s:%d/services/csw",
            "http://%s:%d/csw");

    private static final List<String> CSW_MIME_TYPES = ImmutableList.of("text/xml",
            "application/xml",
            "application/xml; charset=UTF-8",
            "text/xml; charset=UTF-8");

    protected static final String GMD_OUTPUT_SCHEMA = "http://www.isotc211.org/2005/gmd";

    private static final String HAS_CATALOG_METACARD_EXP =
            "//ows:OperationsMetadata//ows:Operation[@name='GetRecords']/ows:Parameter[@name='OutputSchema' or @name='outputSchema']/ows:Value/text()='urn:catalog:metacard'";

    private static final String HAS_GMD_ISO_EXP =
            "//ows:OperationsMetadata/ows:Operation[@name='GetRecords']/ows:Parameter[@name='OutputSchema' or @name='outputSchema']/ows:Value/text()='http://www.isotc211.org/2005/gmd'";

    private static final String GET_FIRST_OUTPUT_SCHEMA =
            "//ows:OperationsMetadata/ows:Operation[@name='GetRecords']/ows:Parameter[@name='OutputSchema' or @name='outputSchema']/ows:Value[1]/text()";

    private final RequestUtils requestUtils;

    public CswSourceUtils() {
        this(new RequestUtils());
    }

    public CswSourceUtils(RequestUtils requestUtils) {
        this.requestUtils = requestUtils;
    }

    /**
     * Confirms whether or not an endpoint has CSW capabilities.
     * SUCCESS TYPES - VERIFIED_CAPABILITIES
     * FAILURE TYPES - CANNOT_CONNECT, CERT_ERROR, UNKNOWN_ENDPOINT
     * WARNING TYPES - UNTRUSTED_CA
     * RETURN TYPES -  CONTENT_TYPE, CONTENT, STATUS_CODE
     *
     * @param url URL to probe for CSW capabilities
     * @param username Optional username for Basic Auth header
     * @param password Optional Password for Basic Auth header
     * @return report
     */
    public ProbeReport sendCswCapabilitiesRequest(String url, String username,
            String password) {
        ProbeReport requestResults = requestUtils.sendGetRequest(url + GET_CAPABILITIES_PARAMS,
                username,
                password);
        if (requestResults.containsFailureMessages()) {
            return requestResults;
        }

        int statusCode = requestResults.getProbeResult(RequestUtils.STATUS_CODE);
        String contentType = requestResults.getProbeResult(RequestUtils.CONTENT_TYPE);

        if (statusCode == HTTP_OK && CSW_MIME_TYPES.contains(contentType)) {
            return requestResults.addMessage(createCommonSourceConfigMsg(VERIFIED_CAPABILITIES))
                    .probeResult(DISCOVERED_URL, url);
        }

        return requestResults.addMessage(createCommonSourceConfigMsg(UNKNOWN_ENDPOINT));
    }

    /**
     * Attempts to discover the source from the given hostname and port
     * SUCCESS TYPES - VERIFIED_CAPABILITIES
     * FAILURE TYPES - UNKNOWN_ENDPOINT
     * WARNING TYPES - UNTRUSTED_CA
     * RETURN TYPES - DISCOVERED_URL
     *
     * @param hostname Host to probe for CSW capabilities
     * @param port Port used for communication with host
     * @param username Optional username for Basic Auth header
     * @param password Optional password for Basic Auth header
     * @return report
     */
    public ProbeReport discoverCswUrl(String hostname, int port, String username,
            String password) {
        return URL_FORMATS.stream()
                .map(format -> String.format(format, hostname, port))
                .map(url -> sendCswCapabilitiesRequest(url, username, password))
                .filter(report -> !report.containsFailureMessages())
                .findFirst()
                .orElse(new ProbeReport(createCommonSourceConfigMsg(UNKNOWN_ENDPOINT)));
    }

    /**
     * Attempts to create a CSW configuration from the given url.
     * SUCCESS TYPES - DISCOVERED_SOURCE
     * FAILURE TYPES - CERT_ERROR, UNKNOWN_ENDPOINT, CANNOT_CONNECT
     * WARNING TYPES - UNTRUSTED_CA
     * RETURN TYPES - DISCOVERED_SOURCES
     *
     * @param url A URL of an endpoint with CSW capabilities
     * @param username Optional username to send with Basic Auth header
     * @param password Optional password to send with Basic Auth header
     * @return report
     */
    public ProbeReport getPreferredCswConfig(String url, String username, String password) {
        ProbeReport requestReport = sendCswCapabilitiesRequest(url, username, password);
        if (requestReport.containsFailureMessages()) {
            return requestReport;
        }

        ProbeReport results = new ProbeReport();
        String requestBody = requestReport.getProbeResult(RequestUtils.CONTENT);
        Document capabilitiesXml;
        try {
            capabilitiesXml = createDocument(requestBody);
        } catch (Exception e) {
            LOGGER.debug("Failed to create XML document from response.");
            return results.addMessage(createInternalErrorMsg(
                    "Unable to read response from endpoint."));
        }

        CswSourceConfiguration preferred = new CswSourceConfiguration();
        preferred.endpointUrl(url);
        preferred.sourceUserName(username);
        preferred.sourceUserPassword(password);
        preferred.configurationHandlerId(CSW_SOURCE_CONFIGURATION_HANDLER_ID);

        XPath xpath = XPathFactory.newInstance()
                .newXPath();
        xpath.setNamespaceContext(SOURCES_NAMESPACE_CONTEXT);

        try {
            if ((Boolean) xpath.compile(HAS_CATALOG_METACARD_EXP)
                    .evaluate(capabilitiesXml, XPathConstants.BOOLEAN)) {
                results.addMessage(createCommonSourceConfigMsg(DISCOVERED_SOURCE));
                return results.probeResult(DISCOVERED_SOURCES,
                        preferred.factoryPid(CSW_PROFILE_FACTORY_PID));
            }
        } catch (Exception e) {
            LOGGER.debug("Failed to compile DDF Profile CSW discovery XPath expression.");
        }

        try {
            if ((Boolean) xpath.compile(HAS_GMD_ISO_EXP)
                    .evaluate(capabilitiesXml, XPathConstants.BOOLEAN)) {
                results.addMessage(createCommonSourceConfigMsg(DISCOVERED_SOURCE));
                return results.probeResult(DISCOVERED_SOURCES,
                        preferred.outputSchema(GMD_OUTPUT_SCHEMA)
                                .factoryPid(CSW_GMD_FACTORY_PID));
            }
        } catch (Exception e) {
            LOGGER.debug("Failed to compile GMD CSW discovery XPath expression.");
        }

        try {
            String outputSchema = xpath.compile(GET_FIRST_OUTPUT_SCHEMA)
                    .evaluate(capabilitiesXml);
            results.addMessage(createCommonSourceConfigMsg(DISCOVERED_SOURCE));
            return results.probeResult(DISCOVERED_SOURCES,
                    preferred.outputSchema(outputSchema)
                            .factoryPid(CSW_SPEC_FACTORY_PID));
        } catch (Exception e) {
            LOGGER.debug("Failed to compile generic CSW specification discovery XPath expression.");
        }

        LOGGER.debug("URL [{}] responded to GetCapabilities request, but response was not readable", url);
        return results.addMessage(createInternalErrorMsg(
                "Failed to create a CSW source configuration from the URL."));
    }
}
