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
import static org.codice.ddf.admin.sources.fields.CswProfile.CSW_FEDERATION_PROFILE_SOURCE;
import static org.codice.ddf.admin.sources.fields.CswProfile.CSW_SPEC_PROFILE_FEDERATED_SOURCE;
import static org.codice.ddf.admin.sources.fields.CswProfile.GMD_CSW_ISO_FEDERATED_SOURCE;

import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.codice.ddf.admin.common.ReportWithResult;
import org.codice.ddf.admin.common.fields.common.CredentialsField;
import org.codice.ddf.admin.common.fields.common.HostField;
import org.codice.ddf.admin.common.fields.common.UrlField;
import org.codice.ddf.admin.sources.fields.type.CswSourceConfigurationField;
import org.codice.ddf.admin.sources.fields.type.SourceConfigUnionField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class CswSourceUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(CswSourceUtils.class);

    public static final Map<String, String> GET_CAPABILITIES_PARAMS = ImmutableMap.of("service",
            "CSW",
            "request",
            "GetCapabilities");

    private static final List<String> URL_FORMATS = ImmutableList.of("https://%s:%d/services/csw",
            "https://%s:%d/csw");

    protected static final String GMD_OUTPUT_SCHEMA = "http://www.isotc211.org/2005/gmd";

    protected static final String CSW_202_OUTPUT_SCHEMA = "http://www.opengis.net/cat/csw/2.0.2";

    protected static final String METACARD_OUTPUT_SCHEMA = "urn:catalog:metacard";

    private static final String HAS_CATALOG_METACARD_EXP =
            "//ows:OperationsMetadata//ows:Operation[@name='GetRecords']/ows:Parameter[@name='OutputSchema' or @name='outputSchema']/ows:Value/text()='urn:catalog:metacard'";

    private static final String HAS_GMD_ISO_EXP =
            "//ows:OperationsMetadata/ows:Operation[@name='GetRecords']/ows:Parameter[@name='OutputSchema' or @name='outputSchema']/ows:Value/text()='http://www.isotc211.org/2005/gmd'";

    private static final String GET_FIRST_OUTPUT_SCHEMA =
            "//ows:OperationsMetadata/ows:Operation[@name='GetRecords']/ows:Parameter[@name='OutputSchema' or @name='outputSchema']/ows:Value[1]/text()";

    private final RequestUtils requestUtils;

    public CswSourceUtils() {
        this(new RequestUtils());
    }

    public CswSourceUtils(RequestUtils requestUtils) {
        this.requestUtils = requestUtils;
    }

    /**
     * Attempts to discover the source from the given hostname and port with optional basic authentication.
     *
     * @param hostField address to probe for CSW capabilities
     * @param creds        optional credentials for basic authentication
     * @return a {@link ReportWithResult} containing the {@link UrlField} or an {@link org.codice.ddf.admin.common.message.ErrorMessage} on failure.
     */
    public ReportWithResult<UrlField> discoverCswUrl(HostField hostField, CredentialsField creds) {
        return requestUtils.discoverUrlFromHost(hostField, URL_FORMATS, creds,
                GET_CAPABILITIES_PARAMS);
    }

    /**
     * Attempts to create a CSW configuration from the given url.
     *
     * @param urlField A URL of an endpoint with CSW capabilities
     * @param creds    optional credentials for basic authentication
     * @return a {@link ReportWithResult} containing the {@link SourceConfigUnionField} or an {@link org.codice.ddf.admin.common.message.ErrorMessage} on failure.
     */
    public ReportWithResult<SourceConfigUnionField> getPreferredCswConfig(UrlField urlField, CredentialsField creds) {
        ReportWithResult<String> responseBodyResult = requestUtils.sendGetRequest(urlField,
                creds,
                GET_CAPABILITIES_PARAMS);

        ReportWithResult<SourceConfigUnionField> configResult = new ReportWithResult<>();
        if (responseBodyResult.containsErrorMsgs()) {
            configResult.addMessages(responseBodyResult);
            return configResult;
        }

        String requestBody = responseBodyResult.result();
        Document capabilitiesXml;
        try {
            capabilitiesXml = createDocument(requestBody);
        } catch (Exception e) {
            LOGGER.debug("Failed to create XML document from response.");
            configResult.argumentMessage(unknownEndpointError(urlField.path()));
            return configResult;
        }

        CswSourceConfigurationField preferred = new CswSourceConfigurationField();
        preferred.endpointUrl(urlField.getValue())
                .credentials()
                .username(creds.username())
                .password(creds.password());

        XPath xpath = XPathFactory.newInstance()
                .newXPath();
        xpath.setNamespaceContext(SOURCES_NAMESPACE_CONTEXT);

        try {
            if ((Boolean) xpath.compile(HAS_CATALOG_METACARD_EXP)
                    .evaluate(capabilitiesXml, XPathConstants.BOOLEAN)) {
                configResult.result(preferred.outputSchema(METACARD_OUTPUT_SCHEMA)
                        .cswProfile(CSW_FEDERATION_PROFILE_SOURCE));
                return configResult;
            }
        } catch (Exception e) {
            LOGGER.debug("Failed to compile DDF Profile CSW discovery XPath expression.");
        }

        try {
            if ((Boolean) xpath.compile(HAS_GMD_ISO_EXP)
                    .evaluate(capabilitiesXml, XPathConstants.BOOLEAN)) {
                configResult.result(preferred.outputSchema(GMD_OUTPUT_SCHEMA)
                        .cswProfile(GMD_CSW_ISO_FEDERATED_SOURCE)
                        .eventServiceAddress(urlField.getValue() + "/subscription"));
                return configResult;
            }
        } catch (Exception e) {
            LOGGER.debug("Failed to compile GMD CSW discovery XPath expression.");
        }

        try {
            xpath.compile(GET_FIRST_OUTPUT_SCHEMA)
                    .evaluate(capabilitiesXml);
            configResult.result(preferred.outputSchema(CSW_202_OUTPUT_SCHEMA)
                    .cswProfile(CSW_SPEC_PROFILE_FEDERATED_SOURCE));
            return configResult;
        } catch (Exception e) {
            LOGGER.debug("Failed to compile generic CSW specification discovery XPath expression.");
        }

        LOGGER.debug("URL [{}] responded to GetCapabilities request, but response was not readable.",
                urlField.getValue());
        configResult.argumentMessage(unknownEndpointError(urlField.path()));
        return configResult;
    }
}
