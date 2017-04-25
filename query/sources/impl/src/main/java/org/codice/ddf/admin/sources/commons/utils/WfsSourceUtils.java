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
import static org.codice.ddf.admin.sources.commons.services.WfsServiceProperties.WFS1_FACTORY_PID;
import static org.codice.ddf.admin.sources.commons.services.WfsServiceProperties.WFS2_FACTORY_PID;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.codice.ddf.admin.common.fields.common.AddressField;
import org.codice.ddf.admin.common.fields.common.CredentialsField;
import org.codice.ddf.admin.common.fields.common.UrlField;
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
     * @return A {@link DiscoveredUrl} containing containing a discovered URL on success,
     * or an {@link org.codice.ddf.admin.common.message.ErrorMessage} on failure.
     */
    public DiscoveredUrl sendWfsCapabilitiesRequest(UrlField urlField, CredentialsField creds) {
        DiscoveredUrl discoveredUrl = requestUtils.sendGetRequest(urlField,
                creds,
                GET_CAPABILITIES_PARAMS);
        if (discoveredUrl.hasErrors()) {
            return discoveredUrl;
        }

        discoveredUrl.put(DISCOVERED_URL, urlField);
        return discoveredUrl;
    }

    /**
     * Attempts to discover a WFS endpoint at a given hostname and port
     *
     * @param addressField address to probe for WFS capabilities
     * @param creds        optional username to add to Basic Auth header
     * @return A {@link DiscoveredUrl} containing containing a discovered URL on success,
     * or an {@link org.codice.ddf.admin.common.message.ErrorMessage} on failure.
     */
    public DiscoveredUrl discoverWfsUrl(AddressField addressField, CredentialsField creds) {
        return URL_FORMATS.stream()
                .map(format -> String.format(format, addressField.hostname(), addressField.port()))
                .map(url -> {
                    // TODO: 4/19/17 phuffer - override this guys path with addressField
                    UrlField urlField = new UrlField(addressField.fieldName());
                    urlField.setValue(url);
                    return urlField;
                })
                .map(urlField -> sendWfsCapabilitiesRequest(urlField, creds))
                .filter(discoveredUrl -> !discoveredUrl.hasErrors())
                .findFirst()
                .orElse(new DiscoveredUrl(Collections.singletonList(unknownEndpointError(
                        addressField.fieldName()))));
    }

    /**
     * Attempts to create a WFS configuration from the given url.
     *
     * @param urlField WFS URL to probe for a configuration
     * @param creds    optional username to add to Basic Auth header
     * @return A {@link DiscoveredUrl} containing containing a discovered URL on success,
     * or an {@link org.codice.ddf.admin.common.message.ErrorMessage} on failure.
     */
    public DiscoveredUrl getPreferredWfsConfig(UrlField urlField, CredentialsField creds) {
        DiscoveredUrl discoveredUrl = sendWfsCapabilitiesRequest(urlField, creds);

        if (discoveredUrl.hasErrors()) {
            return discoveredUrl;
        }

        DiscoveredUrl url = new DiscoveredUrl();
        String requestBody = discoveredUrl.get(RequestUtils.CONTENT);
        Document capabilitiesXml;
        try {
            capabilitiesXml = createDocument(requestBody);
        } catch (Exception e) {
            LOGGER.debug("Failed to read response from WFS endpoint.");
            url.addMessage(INTERNAL_ERROR_MESSAGE);
            return url;
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
            url.addMessage(INTERNAL_ERROR_MESSAGE);
            return url;
        }
        switch (wfsVersion) {
        case "2.0.0":
            url.put(DISCOVERED_SOURCES, preferredConfig.factoryPid(WFS2_FACTORY_PID));
            return url;
        case "1.0.0":
            url.put(DISCOVERED_SOURCES, preferredConfig.factoryPid(WFS1_FACTORY_PID));
            return url;
        default:
            LOGGER.debug("Unsupported WFS version discovered.");
            url.addMessage(unknownEndpointError(urlField.fieldName()));
            return url;
        }
    }
}
