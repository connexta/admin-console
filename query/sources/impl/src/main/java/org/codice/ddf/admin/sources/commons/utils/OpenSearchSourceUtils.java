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

import static org.codice.ddf.admin.common.message.DefaultMessages.INTERNAL_ERROR_MESSAGE;
import static org.codice.ddf.admin.common.message.DefaultMessages.unknownEndpointError;
import static org.codice.ddf.admin.sources.commons.SourceUtilCommons.SOURCES_NAMESPACE_CONTEXT;
import static org.codice.ddf.admin.sources.commons.SourceUtilCommons.createDocument;
import static org.codice.ddf.admin.sources.commons.services.OpenSearchServiceProperties.OPENSEARCH_FACTORY_PID;

import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.codice.ddf.admin.common.Result;
import org.codice.ddf.admin.common.fields.common.AddressField;
import org.codice.ddf.admin.common.fields.common.CredentialsField;
import org.codice.ddf.admin.common.fields.common.UrlField;
import org.codice.ddf.admin.sources.fields.type.OpensearchSourceConfigurationField;
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
     * Confirms whether or not an endpoint has OpenSearch capabilities.
     *
     * @param urlField The URL to probe for OpenSearch capabilities
     * @param creds    optional credentials to send with Basic Auth header
     * @return @return a {@link Result} containing the {@link SourceConfigUnionField} or an {@link org.codice.ddf.admin.common.message.ErrorMessage} on failure.
     */
    public Result<SourceConfigUnionField> getOpenSearchConfig(UrlField urlField, CredentialsField creds) {
        Result<UrlField> result = verifyOpenSearchCapabilities(urlField, creds);
        if (result.hasErrors()) {
            return new Result<SourceConfigUnionField>().argumentMessages(result.argumentMessages());
        }

        OpensearchSourceConfigurationField config = new OpensearchSourceConfigurationField();
        config.endpointUrl(urlField.getValue())
                .factoryPid(OPENSEARCH_FACTORY_PID)
                .credentials()
                .username(creds.username())
                .password(creds.password());

        return new Result<>(config);
    }

    protected Result<UrlField> verifyOpenSearchCapabilities(UrlField urlField, CredentialsField creds) {
        Result<String> getResponseBody = requestUtils.sendGetRequest(urlField,
                creds,
                GET_CAPABILITIES_PARAMS);
        if (getResponseBody.hasErrors()) {
            return new Result<UrlField>().argumentMessages(getResponseBody.argumentMessages());
        }

        Result<UrlField> url = new Result<>();
        Document capabilitiesXml;
        try {
            capabilitiesXml = createDocument(getResponseBody.get());
        } catch (Exception e) {
            LOGGER.debug("Failed to read response from OpenSearch endpoint.");
            return url.argumentMessage(INTERNAL_ERROR_MESSAGE);
        }

        XPath xpath = XPathFactory.newInstance()
                .newXPath();
        xpath.setNamespaceContext(SOURCES_NAMESPACE_CONTEXT);
        try {
            if ((Boolean) xpath.compile(TOTAL_RESULTS_XPATH)
                    .evaluate(capabilitiesXml, XPathConstants.BOOLEAN)) {
                return url.value(urlField);
            }
        } catch (XPathExpressionException e) {
            LOGGER.debug("Failed to compile OpenSearch totalResults XPath.");
            return url.argumentMessage(INTERNAL_ERROR_MESSAGE);
        }
        return url.argumentMessage(unknownEndpointError(urlField.fieldName()));
    }

    /**
     * Attempts to discover an OpenSearch endpoint from the given hostname and port
     *
     * @param addressField hostname and port to probe for OpenSearch capabilities
     * @param creds        optional credentials for basic authentication
     * @return a {@link Result} containing the {@link UrlField} or an {@link org.codice.ddf.admin.common.message.ErrorMessage} on failure.
     */
    public Result<UrlField> discoverOpenSearchUrl(AddressField addressField, CredentialsField creds) {
        return URL_FORMATS.stream()
                .map(format -> String.format(format, addressField.hostname(), addressField.port()))
                .map(url -> {
                    // TODO: 4/19/17 phuffer - override this guys path with addressField
                    UrlField urlField = new UrlField(addressField.fieldName());
                    urlField.setValue(url);
                    return urlField;
                })
                .map(urlField -> verifyOpenSearchCapabilities(urlField, creds))
                .filter(discoveredUrl -> !discoveredUrl.hasErrors())
                .findFirst()
                .orElse(new Result<UrlField>().argumentMessage(unknownEndpointError(addressField.fieldName())));
    }
}
