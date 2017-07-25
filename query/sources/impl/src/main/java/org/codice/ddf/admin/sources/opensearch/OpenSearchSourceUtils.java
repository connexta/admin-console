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
import static org.codice.ddf.admin.common.report.message.DefaultMessages.unknownEndpointError;
import static org.codice.ddf.admin.common.services.ServiceCommons.FLAG_PASSWORD;
import static org.codice.ddf.admin.sources.utils.SourceUtilCommons.SOURCES_NAMESPACE_CONTEXT;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.codice.ddf.admin.api.ConfiguratorSuite;
import org.codice.ddf.admin.common.PrioritizedBatchExecutor;
import org.codice.ddf.admin.common.fields.common.CredentialsField;
import org.codice.ddf.admin.common.fields.common.HostField;
import org.codice.ddf.admin.common.fields.common.ResponseField;
import org.codice.ddf.admin.common.fields.common.UrlField;
import org.codice.ddf.admin.common.report.ReportWithResultImpl;
import org.codice.ddf.admin.sources.fields.type.OpenSearchSourceConfigurationField;
import org.codice.ddf.admin.sources.utils.RequestUtils;
import org.codice.ddf.admin.sources.utils.SourceTaskCallable;
import org.codice.ddf.admin.sources.utils.SourceTaskHandler;
import org.codice.ddf.admin.sources.utils.SourceUtilCommons;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class OpenSearchSourceUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenSearchSourceUtils.class);

    private static final List<List<String>> URL_FORMATS = ImmutableList.of(ImmutableList.of(
            "https://%s:%d/services/catalog/query",
            "https://%s:%d/catalog/query"));

    public static final Map<String, String> GET_CAPABILITIES_PARAMS = ImmutableMap.of("q",
            "test",
            "mr",
            "1",
            "src",
            "local");

    private static final String TOTAL_RESULTS_XPATH = "//os:totalResults|//opensearch:totalResults";

    private final SourceUtilCommons sourceUtilCommons;

    private RequestUtils requestUtils;

    public OpenSearchSourceUtils(ConfiguratorSuite configuratorSuite) {
        this.requestUtils = new RequestUtils();
        this.sourceUtilCommons = new SourceUtilCommons(configuratorSuite);
    }

    /**
     * Attempts to discover an OpenSearch endpoint from the given hostname and port
     * <p>
     * Possible Error Codes to be returned
     * - {@link org.codice.ddf.admin.common.report.message.DefaultMessages#UNKNOWN_ENDPOINT}
     *
     * @param hostField hostname and port to probe for OpenSearch capabilities
     * @param creds     optional credentials for authentication
     * @return a {@link ReportWithResultImpl} containing the discovered {@link OpenSearchSourceConfigurationField} on success, or containing {@link org.codice.ddf.admin.api.report.ErrorMessage}s on failure.
     */
    public ReportWithResultImpl<OpenSearchSourceConfigurationField> getOpenSearchConfigFromHost(
            HostField hostField, CredentialsField creds) {
        List<List<SourceTaskCallable<OpenSearchSourceConfigurationField>>> taskList =
                new ArrayList<>();

        for (List<String> urlFormats : URL_FORMATS) {
            List<SourceTaskCallable<OpenSearchSourceConfigurationField>> callables =
                    new ArrayList<>();
            urlFormats.forEach(url -> callables.add(new SourceTaskCallable<>(url,
                    hostField,
                    creds,
                    this::getOpenSearchConfigFromUrl)));
            taskList.add(callables);
        }

        PrioritizedBatchExecutor<ReportWithResultImpl<OpenSearchSourceConfigurationField>>
                prioritizedExecutor = new PrioritizedBatchExecutor(2,
                taskList,
                new SourceTaskHandler<OpenSearchSourceConfigurationField>());

        Optional<ReportWithResultImpl<OpenSearchSourceConfigurationField>> result =
                prioritizedExecutor.getFirst();

        if (result.isPresent()) {
            return result.get();
        } else {
            return new ReportWithResultImpl<OpenSearchSourceConfigurationField>().addArgumentMessage(
                    unknownEndpointError(hostField.path()));
        }
    }

    public ReportWithResultImpl<OpenSearchSourceConfigurationField> getOpenSearchConfigFromUrl(
            UrlField urlField, CredentialsField creds) {
        ReportWithResultImpl<ResponseField> responseResult = requestUtils.sendGetRequest(urlField,
                creds,
                GET_CAPABILITIES_PARAMS);

        if (responseResult.containsErrorMsgs()) {
            return (ReportWithResultImpl) responseResult;
        }

        return getOpenSearchConfigFromResponse(responseResult.result(), creds);
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
    private ReportWithResultImpl<OpenSearchSourceConfigurationField> getOpenSearchConfigFromResponse(
            ResponseField responseField, CredentialsField creds) {
        ReportWithResultImpl<OpenSearchSourceConfigurationField> configResult =
                new ReportWithResultImpl<>();

        String responseBody = responseField.responseBody();
        int statusCode = responseField.statusCode();

        if (statusCode != HTTP_OK || responseBody.length() < 1) {
            configResult.addArgumentMessage(unknownEndpointError(responseField.requestUrlField()
                    .path()));
            return configResult;
        }

        Document capabilitiesXml;
        try {
            capabilitiesXml = sourceUtilCommons.createDocument(responseBody);
        } catch (Exception e) {
            LOGGER.debug("Failed to read response from OpenSearch endpoint.");
            configResult.addArgumentMessage(unknownEndpointError(responseField.requestUrlField()
                    .path()));
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
                configResult.addArgumentMessage(unknownEndpointError(responseField.requestUrlField()
                        .path()));
            }
        } catch (XPathExpressionException e) {
            LOGGER.debug("Failed to compile OpenSearch totalResults XPath.");
            configResult.addArgumentMessage(unknownEndpointError(responseField.requestUrlField()
                    .path()));
        }

        return configResult;
    }

    /**
     * For testing purposes only. Groovy can access private methods
     */
    private void setRequestUtils(RequestUtils requestUtils) {
        this.requestUtils = requestUtils;
    }
}
