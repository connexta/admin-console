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

import static java.net.HttpURLConnection.HTTP_OK;
import static org.codice.ddf.admin.common.report.message.DefaultMessages.unknownEndpointError;
import static org.codice.ddf.admin.common.services.ServiceCommons.FLAG_PASSWORD;
import static org.codice.ddf.admin.sources.utils.SourceUtilCommons.SOURCES_NAMESPACE_CONTEXT;

import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.codice.ddf.admin.api.ConfiguratorSuite;
import org.codice.ddf.admin.common.fields.common.CredentialsField;
import org.codice.ddf.admin.common.fields.common.HostField;
import org.codice.ddf.admin.common.fields.common.ResponseField;
import org.codice.ddf.admin.common.fields.common.UrlField;
import org.codice.ddf.admin.common.report.ReportWithResultImpl;
import org.codice.ddf.admin.sources.fields.type.WfsSourceConfigurationField;
import org.codice.ddf.admin.sources.utils.RequestUtils;
import org.codice.ddf.admin.sources.utils.SourceUtilCommons;
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

    private SourceUtilCommons sourceUtilCommons;

    private RequestUtils requestUtils;

    public WfsSourceUtils(ConfiguratorSuite configuratorSuite) {
        this.requestUtils = new RequestUtils();
        this.sourceUtilCommons = new SourceUtilCommons(configuratorSuite);
    }

    /**
     * Attempts to discover a WFS endpoint at a given hostname and port.
     * <p>
     * Possible Error Codes to be returned
     * - {@link org.codice.ddf.admin.common.report.message.DefaultMessages#UNKNOWN_ENDPOINT}
     *
     * @param hostField address to probe for WFS capabilities
     * @param creds     optional username to add to Basic Auth header
     * @return a {@link ReportWithResultImpl} containing the {@link WfsSourceConfigurationField} or an {@link org.codice.ddf.admin.api.report.ErrorMessage} on failure.
     */
    public ReportWithResultImpl<WfsSourceConfigurationField> getWfsConfigFromHost(
            HostField hostField, CredentialsField creds) {
        for (String urlFormat : URL_FORMATS) {
            UrlField clientUrl = new UrlField();
            clientUrl.setValue(String.format(urlFormat, hostField.hostname(), hostField.port()));

            ReportWithResultImpl<WfsSourceConfigurationField> configResult = getWfsConfigFromUrl(
                    clientUrl,
                    creds);

            if (!configResult.containsErrorMsgs()) {
                return configResult;
            }
        }

        return new ReportWithResultImpl<WfsSourceConfigurationField>().addArgumentMessage(
                unknownEndpointError(hostField.path()));
    }

    public ReportWithResultImpl<WfsSourceConfigurationField> getWfsConfigFromUrl(UrlField urlField,
            CredentialsField creds) {
        ReportWithResultImpl<ResponseField> responseResult = requestUtils.sendGetRequest(urlField,
                creds,
                GET_CAPABILITIES_PARAMS);

        if (responseResult.containsErrorMsgs()) {
            return (ReportWithResultImpl) responseResult;
        }

        return getWfsConfigFromResult(responseResult.result(), creds);
    }

    /**
     * Attempts to create a WFS configuration from the given WFS GetCapabilities response.
     * <p>
     * Possible Error Codes to be returned
     * - {@link org.codice.ddf.admin.common.report.message.DefaultMessages#UNKNOWN_ENDPOINT}
     *
     * @param responseField WFS URL to probe for a configuration
     * @param creds         optional username to add to Basic Auth header used in the original request
     * @return a {@link ReportWithResultImpl} containing the preferred {@link WfsSourceConfigurationField}, or containing {@link org.codice.ddf.admin.api.report.ErrorMessage}s on failure.
     */
    private ReportWithResultImpl<WfsSourceConfigurationField> getWfsConfigFromResult(
            ResponseField responseField, CredentialsField creds) {
        ReportWithResultImpl<WfsSourceConfigurationField> configResult =
                new ReportWithResultImpl<>();

        String responseBody = responseField.responseBody();
        UrlField requestUrl = responseField.requestUrlField();

        if (responseField.statusCode() != HTTP_OK || responseBody.length() < 1) {
            configResult.addArgumentMessage(unknownEndpointError(responseField.requestUrlField()
                    .path()));
            return configResult;
        }

        Document capabilitiesXml;
        try {
            capabilitiesXml = sourceUtilCommons.createDocument(responseBody);
        } catch (Exception e) {
            LOGGER.debug("Failed to read response from WFS endpoint.");
            configResult.addArgumentMessage(unknownEndpointError(responseField.requestUrlField()
                    .path()));
            return configResult;
        }

        WfsSourceConfigurationField preferredConfig = new WfsSourceConfigurationField();
        preferredConfig.endpointUrl(requestUrl.getValue())
                .credentials()
                .username(creds.username())
                .password(FLAG_PASSWORD);

        XPath xpath = XPathFactory.newInstance()
                .newXPath();
        xpath.setNamespaceContext(SOURCES_NAMESPACE_CONTEXT);
        String wfsVersion;
        try {
            wfsVersion = xpath.compile(WFS_VERSION_EXP)
                    .evaluate(capabilitiesXml);
        } catch (XPathExpressionException e) {
            LOGGER.debug("Failed to parse XML response.");
            configResult.addArgumentMessage(unknownEndpointError(responseField.requestUrlField()
                    .path()));
            return configResult;
        }

        configResult.result(preferredConfig.wfsVersion(wfsVersion));
        if (!preferredConfig.validate()
                .isEmpty()) {
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
