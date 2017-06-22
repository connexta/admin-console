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
import static org.codice.ddf.admin.common.report.message.DefaultMessages.cannotConnectError;
import static org.codice.ddf.admin.common.report.message.DefaultMessages.unknownEndpointError;
import static org.codice.ddf.admin.common.services.ServiceCommons.FLAG_PASSWORD;
import static org.codice.ddf.admin.sources.utils.SourceUtilCommons.SOURCES_NAMESPACE_CONTEXT;

import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.codice.ddf.admin.common.fields.common.CredentialsField;
import org.codice.ddf.admin.common.fields.common.HostField;
import org.codice.ddf.admin.common.fields.common.ResponseField;
import org.codice.ddf.admin.common.fields.common.UrlField;
import org.codice.ddf.admin.common.report.ReportWithResultImpl;
import org.codice.ddf.admin.sources.fields.type.OpenSearchSourceConfigurationField;
import org.codice.ddf.admin.sources.utils.RequestUtils;
import org.codice.ddf.admin.sources.utils.SourceUtilCommons;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class OpenSearchSourceUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenSearchSourceUtils.class);

    private static final List<String> URL_FORMATS = ImmutableList.of(
            "https://%s:%d/services/catalog/query",
            "https://%s:%d/catalog/query");

    public static final Map<String, String> GET_CAPABILITIES_PARAMS = ImmutableMap.of("q",
            "test",
            "mr",
            "1",
            "src",
            "local");

    private static final String TOTAL_RESULTS_XPATH = "//os:totalResults|//opensearch:totalResults";

    private final RequestUtils requestUtils;

    private final SourceUtilCommons sourceUtilCommons;

    public OpenSearchSourceUtils() {
        this(new RequestUtils(), new SourceUtilCommons());
    }

    public OpenSearchSourceUtils(RequestUtils requestUtils, SourceUtilCommons sourceUtilCommons) {
        this.requestUtils = requestUtils;
        this.sourceUtilCommons = sourceUtilCommons;
    }

    /**
     * Attempts to discover an OpenSearch endpoint from the given hostname and port
     * <p>
     * Possible Error Codes to be returned
     * - {@link org.codice.ddf.admin.common.report.message.DefaultMessages#CANNOT_CONNECT}
     *
     * @param hostField hostname and port to probe for OpenSearch capabilities
     * @param creds     optional credentials for authentication
     * @return a {@link ReportWithResultImpl} containing the discovered {@link ResponseField} on success, or containing {@link org.codice.ddf.admin.api.report.ErrorMessage}s on failure.
     */
    public ReportWithResultImpl<ResponseField> discoverOpenSearchUrl(HostField hostField,
            CredentialsField creds) {
        ReportWithResultImpl<ResponseField> responseResult = requestUtils.discoverUrlFromHost(hostField,
                URL_FORMATS,
                creds,
                GET_CAPABILITIES_PARAMS);
        if(responseResult.containsErrorMsgs()) {
            return new ReportWithResultImpl<ResponseField>().addArgumentMessage(cannotConnectError(hostField.path()));
        }
        return responseResult;
    }

    public ReportWithResultImpl<ResponseField> sendRequest(UrlField urlField,
            CredentialsField creds) {
        return requestUtils.sendGetRequest(urlField, creds, GET_CAPABILITIES_PARAMS);
    }

    /**
     * Attempts to create an OpenSearch configuration with and OpenSearch capabilities response
     * <p>
     * Possible Error Codes to be returned
     * - {@link org.codice.ddf.admin.common.report.message.DefaultMessages#UNKNOWN_ENDPOINT}
     *
     * @param responseField The URL to probe for OpenSearch capabilities
     * @param creds         optional credentials used in the original capabilities request
     * @return a {@link ReportWithResultImpl} containing the {@link OpenSearchSourceConfigurationField} or containing {@link org.codice.ddf.admin.api.report.ErrorMessage}s on failure.
     */
    public ReportWithResultImpl<OpenSearchSourceConfigurationField> getOpenSearchConfig(
            ResponseField responseField, CredentialsField creds) {
        ReportWithResultImpl<OpenSearchSourceConfigurationField> configResult =
                new ReportWithResultImpl<>();

        String responseBody = responseField.responseBody();
        int statusCode = responseField.statusCode();

        if (statusCode != HTTP_OK || responseBody.length() < 1) {
            configResult.addResultMessage(unknownEndpointError());
            return configResult;
        }

        Document capabilitiesXml;
        try {
            capabilitiesXml = sourceUtilCommons.createDocument(responseBody);
        } catch (Exception e) {
            LOGGER.debug("Failed to read response from OpenSearch endpoint.");
            configResult.addResultMessage(unknownEndpointError());
            return configResult;
        }

        XPath xpath = XPathFactory.newInstance()
                .newXPath();
        xpath.setNamespaceContext(SOURCES_NAMESPACE_CONTEXT);

        try {
            if ((Boolean) xpath.compile(TOTAL_RESULTS_XPATH)
                    .evaluate(capabilitiesXml, XPathConstants.BOOLEAN)) {
                OpenSearchSourceConfigurationField config =
                        new OpenSearchSourceConfigurationField();
                config.endpointUrl(responseField.requestUrl())
                        .credentials()
                        .username(creds.username())
                        .password(FLAG_PASSWORD);

                configResult.result(config);
            } else {
                configResult.addResultMessage(unknownEndpointError());
            }
        } catch (XPathExpressionException e) {
            LOGGER.debug("Failed to compile OpenSearch totalResults XPath.");
            configResult.addResultMessage(unknownEndpointError());
        }

        return configResult;
    }
}
