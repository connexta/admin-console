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
import static org.codice.ddf.admin.sources.commons.SourceUtilCommons.DISCOVERED_SOURCES;
import static org.codice.ddf.admin.sources.commons.SourceUtilCommons.DISCOVERED_URL;
import static org.codice.ddf.admin.sources.commons.SourceUtilCommons.SOURCES_NAMESPACE_CONTEXT;
import static org.codice.ddf.admin.sources.commons.SourceUtilCommons.createDocument;
import static org.codice.ddf.admin.sources.commons.services.OpenSearchServiceProperties.OPENSEARCH_FACTORY_PID;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.codice.ddf.admin.common.fields.common.AddressField;
import org.codice.ddf.admin.common.fields.common.CredentialsField;
import org.codice.ddf.admin.common.fields.common.UrlField;
import org.codice.ddf.admin.sources.fields.type.OpensearchSourceConfigurationField;
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
     * @return A {@link DiscoveredUrl} containing containing a discovered URL on success,
     * or an {@link org.codice.ddf.admin.common.message.ErrorMessage} on failure.
     */
    public DiscoveredUrl getOpenSearchConfig(UrlField urlField, CredentialsField creds) {
        DiscoveredUrl results = verifyOpenSearchCapabilities(urlField, creds);
        if (results.hasErrors()) {
            return results;
        }

        OpensearchSourceConfigurationField config = new OpensearchSourceConfigurationField();
        config.endpointUrl(urlField.getValue())
                .factoryPid(OPENSEARCH_FACTORY_PID)
                .credentials()
                .username(creds.username())
                .password(creds.password());

        results.put(DISCOVERED_SOURCES, config);
        return results;
    }

    protected DiscoveredUrl verifyOpenSearchCapabilities(UrlField urlField,
            CredentialsField creds) {
        DiscoveredUrl discoveredUrl = requestUtils.sendGetRequest(urlField,
                creds,
                GET_CAPABILITIES_PARAMS);
        if (discoveredUrl.hasErrors()) {
            return discoveredUrl;
        }

        Document capabilitiesXml;
        try {
            capabilitiesXml = createDocument(discoveredUrl.get(RequestUtils.CONTENT));
        } catch (Exception e) {
            LOGGER.debug("Failed to read response from OpenSearch endpoint.");
            discoveredUrl.addMessage(INTERNAL_ERROR_MESSAGE);
            return discoveredUrl;
        }

        XPath xpath = XPathFactory.newInstance()
                .newXPath();
        xpath.setNamespaceContext(SOURCES_NAMESPACE_CONTEXT);
        try {
            if ((Boolean) xpath.compile(TOTAL_RESULTS_XPATH)
                    .evaluate(capabilitiesXml, XPathConstants.BOOLEAN)) {
                discoveredUrl.put(DISCOVERED_URL, urlField);
                return discoveredUrl;
            }
        } catch (XPathExpressionException e) {
            LOGGER.debug("Failed to compile OpenSearch totalResults XPath.");
            discoveredUrl.addMessage(INTERNAL_ERROR_MESSAGE);
            return discoveredUrl;
        }
        discoveredUrl.addMessage(unknownEndpointError(urlField.fieldName()));
        return discoveredUrl;
    }

    /**
     * Attempts to discover an OpenSearch endpoint from the given hostname and port
     *
     * @param addressField hostname and port to probe for OpenSearch capabilities
     * @param creds        optional credentials for basic authentication
     * @return A {@link DiscoveredUrl} containing containing a discovered URL on success,
     * or an {@link org.codice.ddf.admin.common.message.ErrorMessage} on failure.
     */
    public DiscoveredUrl discoverOpenSearchUrl(AddressField addressField, CredentialsField creds) {
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
                .orElse(new DiscoveredUrl(Collections.singletonList(unknownEndpointError(
                        addressField.fieldName()))));
    }
}
