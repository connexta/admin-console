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
import static org.codice.ddf.admin.sources.services.WfsServiceProperties.resolveWfsFactoryPid;

import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.codice.ddf.admin.api.action.Message;
import org.codice.ddf.admin.common.Result;
import org.codice.ddf.admin.common.fields.common.AddressField;
import org.codice.ddf.admin.common.fields.common.CredentialsField;
import org.codice.ddf.admin.common.fields.common.UrlField;
import org.codice.ddf.admin.sources.fields.type.SourceConfigUnionField;
import org.codice.ddf.admin.sources.fields.type.WfsSourceConfigurationField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class WfsSourceUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(WfsSourceUtils.class);

    public static final Map<String, String> GET_CAPABILITIES_PARAMS = ImmutableMap.of("service",
            "WFS",
            "request",
            "GetCapabilities",
            "AcceptVersions",
            "2.0.0,1.0.0");

    private static final List<String> URL_FORMATS = ImmutableList.of("https://%s:%d/services/wfs",
            "https://%s:%d/wfs",
            "http://%s:%d/services/wfs",
            "http://%s:%d/wfs");

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
     *
     * @param urlField URL to probe for WFS capabilities
     * @param creds    optional username and password to add to Basic Auth header
     * @return a {@link Result} containing the {@link UrlField} or an {@link org.codice.ddf.admin.common.message.ErrorMessage} on failure.
     */
    public List<Message> sendWfsCapabilitiesRequest(UrlField urlField, CredentialsField creds) {
        Result<String> result = requestUtils.sendGetRequest(urlField, creds, GET_CAPABILITIES_PARAMS);
        return result.allMessages();
    }

    /**
     * Attempts to discover a WFS endpoint at a given hostname and port
     *
     * @param addressField address to probe for WFS capabilities
     * @param creds        optional username to add to Basic Auth header
     * @return @return a {@link Result} containing the {@link UrlField} or an {@link org.codice.ddf.admin.common.message.ErrorMessage} on failure.
     */
    public Result<UrlField> discoverWfsUrl(AddressField addressField, CredentialsField creds) {
        Result<UrlField> defaultResult = new Result<>();
        defaultResult.argumentMessage(unknownEndpointError(addressField.path()));

        return URL_FORMATS.stream()
                .map(format -> String.format(format, addressField.hostname(), addressField.port()))
                .map(url -> {
                    UrlField urlField = new UrlField(addressField.fieldName());
                    urlField.updatePath(addressField.path());
                    urlField.setValue(url);
                    return urlField;
                })
                .filter(urlField -> sendWfsCapabilitiesRequest(urlField, creds).isEmpty())
                .map(Result::new)
                .findFirst()
                .orElse(defaultResult);
    }

    /**
     * Attempts to create a WFS configuration from the given url.
     *
     * @param urlField WFS URL to probe for a configuration
     * @param creds    optional username to add to Basic Auth header
     * @return @return a {@link Result} containing the preferred {@link SourceConfigUnionField} or an {@link org.codice.ddf.admin.common.message.ErrorMessage} on failure.
     */
    public Result<SourceConfigUnionField> getPreferredWfsConfig(UrlField urlField, CredentialsField creds) {
        Result<String> responseBodyResult = requestUtils.sendGetRequest(urlField, creds, GET_CAPABILITIES_PARAMS);
        Result<SourceConfigUnionField> configResult = new Result<>();
        if (responseBodyResult.hasErrors()) {
            configResult.argumentMessages(responseBodyResult.argumentMessages());
            return configResult;

        }

        String requestBody = responseBodyResult.get();
        Document capabilitiesXml;
        try {
            capabilitiesXml = createDocument(requestBody);
        } catch (Exception e) {
            LOGGER.debug("Failed to read response from WFS endpoint.");
            configResult.argumentMessage(unknownEndpointError(urlField.path()));
            return configResult;
        }

        WfsSourceConfigurationField preferredConfig = new WfsSourceConfigurationField();
        preferredConfig.endpointUrl(urlField.getValue())
                .credentials()
                .username(creds.username())
                .password(creds.password());

        XPath xpath = XPathFactory.newInstance()
                .newXPath();
        xpath.setNamespaceContext(SOURCES_NAMESPACE_CONTEXT);
        String wfsVersion;
        try {
            wfsVersion = xpath.compile(WFS_VERSION_EXP)
                    .evaluate(capabilitiesXml);
        } catch (XPathExpressionException e) {
            LOGGER.debug("Failed to parse XML response.");
            configResult.argumentMessage(unknownEndpointError(urlField.path()));
            return configResult;
        }

        try {
            String factoryPid = resolveWfsFactoryPid(wfsVersion);
            preferredConfig.factoryPid(factoryPid);
            configResult.value(preferredConfig);
        } catch (IllegalArgumentException e) {
            configResult.argumentMessage(unknownEndpointError(urlField.path()));
        }
        return configResult;
    }
}
