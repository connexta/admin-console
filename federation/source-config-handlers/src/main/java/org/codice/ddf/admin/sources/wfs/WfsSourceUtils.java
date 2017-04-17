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
package org.codice.ddf.admin.sources.wfs;

import static org.codice.ddf.admin.api.handler.ConfigurationMessage.createInternalErrorMsg;
import static org.codice.ddf.admin.api.services.WfsServiceProperties.WFS1_FACTORY_PID;
import static org.codice.ddf.admin.api.services.WfsServiceProperties.WFS2_FACTORY_PID;
import static org.codice.ddf.admin.commons.sources.SourceHandlerCommons.DISCOVERED_SOURCE;
import static org.codice.ddf.admin.commons.sources.SourceHandlerCommons.DISCOVERED_SOURCES;
import static org.codice.ddf.admin.commons.sources.SourceHandlerCommons.DISCOVERED_URL;
import static org.codice.ddf.admin.commons.sources.SourceHandlerCommons.SOURCES_NAMESPACE_CONTEXT;
import static org.codice.ddf.admin.commons.sources.SourceHandlerCommons.UNKNOWN_ENDPOINT;
import static org.codice.ddf.admin.commons.sources.SourceHandlerCommons.VERIFIED_CAPABILITIES;
import static org.codice.ddf.admin.commons.sources.SourceHandlerCommons.createCommonSourceConfigMsg;
import static org.codice.ddf.admin.commons.sources.SourceHandlerCommons.createDocument;
import static org.codice.ddf.admin.sources.wfs.WfsSourceConfigurationHandler.WFS_SOURCE_CONFIGURATION_HANDLER_ID;

import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.codice.ddf.admin.api.config.sources.WfsSourceConfiguration;
import org.codice.ddf.admin.api.handler.report.ProbeReport;
import org.codice.ddf.admin.commons.requests.RequestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class WfsSourceUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(WfsSourceUtils.class);

    private static final List<String> WFS_MIME_TYPES = ImmutableList.of("text/xml",
            "application/xml",
            "text/xml;charset=UTF-8",
            "application/xml;charset=UTF-8");

    private static final Map<String, String> REQUEST_PARAMS = ImmutableMap.of(
            "service", "WFS",
            "request", "GetCapabilities",
            "AcceptVersions", "2.0.0,1.0.0");

    private static final List<String> URL_FORMATS = ImmutableList.of("https://%s:%d/services/wfs",
            "https://%s:%d/wfs");

    private static final String WFS_VERSION_EXP = "/wfs:WFS_Capabilities/attribute::version";

    private final RequestUtils requestUtils;

    public WfsSourceUtils() {
        this(new RequestUtils());
    }

    public WfsSourceUtils(RequestUtils requestUtils) {
        this.requestUtils = requestUtils;
    }

    /**
     * Attempts to verify the given URL as a functional WFS endpoint
     * SUCCESS TYPES - VERIFIED_CAPABILITIES,
     * FAILURE TYPES - CANNOT_CONNECT, CERT_ERROR, UNKNOWN_ENDPOINT
     * WARNING TYPES - UNTRUSTED_CA
     * RETURN TYPES -  DISCOVERED_URL
     *
     * @param url URL to probe for WFS capabilities
     * @param username Optional username to add to Basic Auth header
     * @param password Optional password to add to Basic Auth header
     * @return report
     */
    public ProbeReport sendWfsCapabilitiesRequest(String url, String username,
            String password) {
        ProbeReport requestResults = requestUtils.sendGetRequest(url, username, password, REQUEST_PARAMS);
        return requestResults.containsFailureMessages() ? requestResults :
                requestResults.addMessage(createCommonSourceConfigMsg(VERIFIED_CAPABILITIES))
                    .probeResult(DISCOVERED_URL, url);
    }

    /**
     * Attempts to discover a WFS endpoint at a given hostname and port
     * SUCCESS TYPES - VERIFIED_CAPABILITIES,
     * FAILURE TYPES - UNKNOWN_ENDPOINT
     * WARNING TYPES - UNTRUSTED_CA
     * RETURN TYPES - DISCOVERED_URL
     *
     * @param hostname Hostname to probe for WFS capabilities
     * @param port Port over which to connect to host
     * @param username Optional username to add to Basic Auth header
     * @param password Optional username to add to Basic Auth header
     * @return report
     */
    public ProbeReport discoverWfsUrl(String hostname, int port, String username,
            String password) {
        return URL_FORMATS.stream()
                .map(formatUrl -> String.format(formatUrl, hostname, port))
                .map(url -> sendWfsCapabilitiesRequest(
                        url,
                        username,
                        password))
                .filter(report -> !report.containsFailureMessages())
                .findFirst()
                .orElse(new ProbeReport(createCommonSourceConfigMsg(UNKNOWN_ENDPOINT)));
    }

    /**
     * Attempts to create a WFS configuration from the given url.
     * SUCCESS TYPES - CONFIG_CREATED
     * FAILURE TYPES - CERT_ERROR, UNKNOWN_ENDPOINT, CANNOT_CONNECT
     * WARNING TYPES - UNTRUSTED_CA
     * RETURN TYPES - DISCOVERED_SOURCES
     *
     * @param url WFS URL to probe for a configuration
     * @param username Optional username to add to Basic Auth header
     * @param password Optional password to add to Basic Auth header
     * @return report
     */
    public ProbeReport getPreferredWfsConfig(String url, String username, String password) {
        ProbeReport requestReport = sendWfsCapabilitiesRequest(
                url, username, password);

        if (requestReport.containsFailureMessages()) {
            return requestReport;
        }

        ProbeReport results = new ProbeReport();
        String requestBody = requestReport.getProbeResult(RequestUtils.CONTENT);
        Document capabilitiesXml;
        try {
            capabilitiesXml = createDocument(requestBody);
        } catch (Exception e) {
            LOGGER.debug("Failed to read response from WFS endpoint.");
            return results.addMessage(createInternalErrorMsg(
                    "Unable to read response from endpoint."));
        }

        WfsSourceConfiguration preferredConfig = new WfsSourceConfiguration();
        preferredConfig.configurationHandlerId(WFS_SOURCE_CONFIGURATION_HANDLER_ID);
        preferredConfig.endpointUrl(url)
                .sourceUserName(username)
                .sourceUserPassword(password);

        XPath xpath = XPathFactory.newInstance()
                .newXPath();
        xpath.setNamespaceContext(SOURCES_NAMESPACE_CONTEXT);
        String wfsVersion;
        try {
            wfsVersion = xpath.compile(WFS_VERSION_EXP)
                    .evaluate(capabilitiesXml);
        } catch (XPathExpressionException e) {
            LOGGER.debug("Failed to parse XML response.");
            return results.addMessage(createInternalErrorMsg("Unable to parse XML response."));
        }
        switch (wfsVersion) {
        case "2.0.0":
            results.addMessage(createCommonSourceConfigMsg(DISCOVERED_SOURCE));
            return results.probeResult(DISCOVERED_SOURCES, preferredConfig.factoryPid(WFS2_FACTORY_PID));
        case "1.0.0":
            results.addMessage(createCommonSourceConfigMsg(DISCOVERED_SOURCE));
            return results.probeResult(DISCOVERED_SOURCES,
                    preferredConfig.factoryPid(WFS1_FACTORY_PID));
        default:
            LOGGER.debug("Unsupported WFS version discovered.");
            return results.addMessage(createCommonSourceConfigMsg(UNKNOWN_ENDPOINT));
        }
    }

}
