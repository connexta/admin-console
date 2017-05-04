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
package org.codice.ddf.admin.sources.commons.utils;

import static org.codice.ddf.admin.common.message.DefaultMessages.unknownEndpointError;
import static org.codice.ddf.admin.sources.commons.SourceUtilCommons.SOURCES_NAMESPACE_CONTEXT;
import static org.codice.ddf.admin.sources.commons.SourceUtilCommons.createDocument;
import static org.codice.ddf.admin.sources.services.OpenSearchServiceProperties.OPENSEARCH_FACTORY_PID;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.collections.CollectionUtils;
import org.codice.ddf.admin.api.action.Message;
import org.codice.ddf.admin.common.Result;
import org.codice.ddf.admin.common.fields.common.AddressField;
import org.codice.ddf.admin.common.fields.common.CredentialsField;
import org.codice.ddf.admin.common.fields.common.UrlField;
import org.codice.ddf.admin.sources.fields.type.OpenSearchSourceConfigurationField;
import org.codice.ddf.admin.sources.fields.type.SourceConfigUnionField;
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

    public OpenSearchSourceUtils() {
        this(new RequestUtils());
    }

    public OpenSearchSourceUtils(RequestUtils requestUtils) {
        this.requestUtils = requestUtils;
    }

    /**
     * Attempts to create an OpenSearch configuration with the provided URL and credentials. If a configuration
     * is not found or created, the {@link Result}'s messages will have errors.
     *
     * @param urlField The URL to probe for OpenSearch capabilities
     * @param creds    optional credentials to send with Basic Auth header
     * @return @return a {@link Result} containing the {@link SourceConfigUnionField} or an {@link org.codice.ddf.admin.common.message.ErrorMessage} on failure.
     */
    public Result<SourceConfigUnionField> getOpenSearchConfig(UrlField urlField, CredentialsField creds) {
        List<Message> errors = verifyOpenSearchCapabilities(urlField, creds);
        Result<SourceConfigUnionField> configResult = new Result<>();
        if (CollectionUtils.isNotEmpty(errors)) {
            configResult.argumentMessages(errors);
            return configResult;
        }

        OpenSearchSourceConfigurationField config = new OpenSearchSourceConfigurationField();
        config.endpointUrl(urlField.getValue())
                .factoryPid(OPENSEARCH_FACTORY_PID)
                .credentials()
                .username(creds.username())
                .password(creds.password());
        return configResult.value(config);
    }

    /**
     * Verifies that an endpoint given by the URL (and credentials) has OpenSearch capabilities.
     *
     * @param urlField endpoint url to verify
     * @param creds optional credentials for authentication
     * @return empty list on successful verification, otherwise a list containing an {@link org.codice.ddf.admin.common.message.ErrorMessage}
     */
    protected List<Message> verifyOpenSearchCapabilities(UrlField urlField, CredentialsField creds) {
        Result<String> result = requestUtils.sendGetRequest(urlField, creds,
                GET_CAPABILITIES_PARAMS);
        if (result.hasErrors()) {
            return result.allMessages();
        }

        Document capabilitiesXml;
        try {
            capabilitiesXml = createDocument(result.get());
        } catch (Exception e) {
            LOGGER.debug("Failed to read response from OpenSearch endpoint.");
            return Collections.singletonList(unknownEndpointError(urlField.path()));
        }

        XPath xpath = XPathFactory.newInstance()
                .newXPath();
        xpath.setNamespaceContext(SOURCES_NAMESPACE_CONTEXT);
        try {
            if ((Boolean) xpath.compile(TOTAL_RESULTS_XPATH)
                    .evaluate(capabilitiesXml, XPathConstants.BOOLEAN)) {
                return Collections.emptyList();
            }
        } catch (XPathExpressionException e) {
            LOGGER.debug("Failed to compile OpenSearch totalResults XPath.");
            return Collections.singletonList(unknownEndpointError(urlField.path()));
        }
        return Collections.singletonList(unknownEndpointError(urlField.path()));
    }

    /**
     * Attempts to discover an OpenSearch endpoint from the given hostname and port
     *
     * @param addressField hostname and port to probe for OpenSearch capabilities
     * @param creds        optional credentials for authentication
     * @return a {@link Result} containing the discovered {@link UrlField} or an {@link org.codice.ddf.admin.common.message.ErrorMessage} on failure.
     */
    public Result<UrlField> discoverOpenSearchUrl(AddressField addressField, CredentialsField creds) {
        return URL_FORMATS.stream()
                .map(format -> String.format(format, addressField.hostname(), addressField.port()))
                .map(url -> {
                    UrlField urlField = new UrlField(addressField.fieldName());
                    urlField.updatePath(addressField.path());
                    urlField.setValue(url);
                    return urlField;
                })
                .filter(urlField -> verifyOpenSearchCapabilities(urlField, creds).isEmpty())
                .map(Result::new)
                .findFirst()
                .orElse(createDefaultResult(addressField));
    }

    private Result<UrlField> createDefaultResult(AddressField addressField) {
        Result<UrlField> defaultResult = new Result<>();
        defaultResult.argumentMessage(unknownEndpointError(addressField.path()));
        return defaultResult;
    }
}
