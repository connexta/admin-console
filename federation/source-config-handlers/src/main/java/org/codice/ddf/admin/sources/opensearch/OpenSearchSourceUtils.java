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
package org.codice.ddf.admin.sources.opensearch;

import static java.net.HttpURLConnection.HTTP_OK;
import static org.codice.ddf.admin.api.handler.ConfigurationMessage.createInternalErrorMsg;
import static org.codice.ddf.admin.api.services.OpenSearchServiceProperties.OPENSEARCH_FACTORY_PID;
import static org.codice.ddf.admin.commons.sources.SourceHandlerCommons.DISCOVERED_SOURCES;
import static org.codice.ddf.admin.commons.sources.SourceHandlerCommons.DISCOVERED_URL;
import static org.codice.ddf.admin.commons.sources.SourceHandlerCommons.SOURCES_NAMESPACE_CONTEXT;
import static org.codice.ddf.admin.commons.sources.SourceHandlerCommons.UNKNOWN_ENDPOINT;
import static org.codice.ddf.admin.commons.sources.SourceHandlerCommons.VERIFIED_CAPABILITIES;
import static org.codice.ddf.admin.commons.sources.SourceHandlerCommons.createCommonSourceConfigMsg;
import static org.codice.ddf.admin.commons.sources.SourceHandlerCommons.createDocument;
import static org.codice.ddf.admin.sources.opensearch.OpenSearchSourceConfigurationHandler.OPENSEARCH_SOURCE_CONFIGURATION_HANDLER_ID;

import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.codice.ddf.admin.api.config.sources.OpenSearchSourceConfiguration;
import org.codice.ddf.admin.api.handler.report.ProbeReport;
import org.codice.ddf.admin.commons.requests.RequestUtils;
import org.w3c.dom.Document;

import com.google.common.collect.ImmutableList;

public class OpenSearchSourceUtils {

    private static final List<String> OPENSEARCH_MIME_TYPES = ImmutableList.of(
            "application/atom+xml",
            "application/atom+xml; charset=UTF-8");

    private static final List<String> URL_FORMATS = ImmutableList.of(
            "https://%s:%d/services/catalog/query",
            "https://%s:%d/catalog/query",
            "http://%s:%d/services/catalog/query",
            "http://%s:%d/catalog/query");

    private static final String SIMPLE_QUERY_PARAMS = "?q=test&mr=1&src=local";

    private static final String TOTAL_RESULTS_XPATH = "//os:totalResults|//opensearch:totalResults";

    private static final RequestUtils requestUtils = new RequestUtils();

    /**
     * Confirms whether or not an endpoint has OpenSearch capabilities.
     * SUCCESS TYPES - VERIFIED_CAPABILITIES,
     * FAILURE TYPES - CANNOT_CONNECT, CERT_ERROR, UNKNOWN_ENDPOINT
     * WARNING TYPES - UNTRUSTED_CA
     * RETURN TYPES -  CONTENT_TYPE, CONTENT, STATUS_CODE
     *
     * @param url
     * @param username
     * @param password
     * @return report
     */
    public static ProbeReport getOpenSearchConfig(String url, String username, String password) {
        ProbeReport results = verifyOpenSearchCapabilities(url, username, password);
        if (results.containsFailureMessages()) {
            return results;
        }

        OpenSearchSourceConfiguration config = new OpenSearchSourceConfiguration();
        config.sourceUserName(username)
                .sourceUserPassword(password)
                .endpointUrl(url)
                .factoryPid(OPENSEARCH_FACTORY_PID)
                .configurationHandlerId(OPENSEARCH_SOURCE_CONFIGURATION_HANDLER_ID);

        results.probeResult(DISCOVERED_SOURCES, config);
        return results;
    }

    public static ProbeReport verifyOpenSearchCapabilities(String url, String username,
            String password) {
        ProbeReport requestResults = requestUtils.sendGetRequest(url + SIMPLE_QUERY_PARAMS,
                username,
                password);
        if (requestResults.containsFailureMessages()) {
            return requestResults;
        }

        int statusCode = requestResults.getProbeResult(RequestUtils.STATUS_CODE);
        String contentType = requestResults.getProbeResult(RequestUtils.CONTENT_TYPE);

        if (!(statusCode == HTTP_OK && OPENSEARCH_MIME_TYPES.contains(contentType))) {
            return requestResults.addMessage(createCommonSourceConfigMsg(UNKNOWN_ENDPOINT));
        }

        Document capabilitiesXml;
        try {
            capabilitiesXml = createDocument(requestResults.getProbeResult(RequestUtils.CONTENT));
        } catch (Exception e) {
            return requestResults.addMessage(createInternalErrorMsg(
                    "Unable to read response from endpoint."));
        }

        XPath xpath = XPathFactory.newInstance()
                .newXPath();
        xpath.setNamespaceContext(SOURCES_NAMESPACE_CONTEXT);
        try {
            if ((Boolean) xpath.compile(TOTAL_RESULTS_XPATH)
                    .evaluate(capabilitiesXml, XPathConstants.BOOLEAN)) {
                return requestResults.addMessage(createCommonSourceConfigMsg(VERIFIED_CAPABILITIES))
                        .probeResult(DISCOVERED_URL, url);
            }
        } catch (XPathExpressionException e) {
            return requestResults.addMessage(createInternalErrorMsg("Failed to compile XPath."));
        }
        return requestResults.addMessage(createCommonSourceConfigMsg(UNKNOWN_ENDPOINT));
    }

    /**
     * Attempts to discover an OpenSearch endpoint from the given hostname and port
     * SUCCESS TYPES - VERIFIED_CAPABILITIES,
     * FAILURE TYPES - UNKNOWN_ENDPOINT
     * WARNING TYPES - UNTRUSTED_CA
     * RETURN TYPES - DISCOVERED_URL
     *
     * @param hostname
     * @param port
     * @param username
     * @param password
     * @return report
     */
    public static ProbeReport discoverOpenSearchUrl(String hostname, int port, String username,
            String password) {
        return URL_FORMATS.stream()
                .map(formatUrl -> String.format(formatUrl, hostname, port))
                .map(url -> verifyOpenSearchCapabilities(url, username, password))
                .filter(report -> !report.containsFailureMessages())
                .findFirst()
                .orElse(new ProbeReport(createCommonSourceConfigMsg(UNKNOWN_ENDPOINT)));
    }

}
