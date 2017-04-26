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
import static org.codice.ddf.admin.sources.commons.services.CswServiceProperties.CSW_GMD_FACTORY_PID;
import static org.codice.ddf.admin.sources.commons.services.CswServiceProperties.CSW_PROFILE_FACTORY_PID;
import static org.codice.ddf.admin.sources.commons.services.CswServiceProperties.CSW_SPEC_FACTORY_PID;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.codice.ddf.admin.common.Result;
import org.codice.ddf.admin.common.fields.common.AddressField;
import org.codice.ddf.admin.common.fields.common.CredentialsField;
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
     * Confirms whether or not an endpoint has CSW capabilities.
     *
     * @param urlField the url endpoint
     * @param creds    optional credentials for basic authentication
     * @return a {@link Result} containing the {@link UrlField} or an {@link org.codice.ddf.admin.common.message.ErrorMessage} on failure.
     */
    public Result<UrlField> sendCswCapabilitiesRequest(UrlField urlField, CredentialsField creds) {
        Result result = requestUtils.sendGetRequest(urlField, creds,
                GET_CAPABILITIES_PARAMS);
        if (result.hasErrors()) {
            return new Result<UrlField>().argumentMessages(result.argumentMessages());
        }

        return new Result<>(urlField);
    }

    /**
     * Attempts to discover the source from the given hostname and port with optional basic authentication.
     *
     * @param addressField address to probe for CSW capabilities
     * @param creds        optional credentials for basic authentication
     * @return a {@link Result} containing the {@link UrlField} or an {@link org.codice.ddf.admin.common.message.ErrorMessage} on failure.
     */
    public Result<UrlField> discoverCswUrl(AddressField addressField, CredentialsField creds) {
        return URL_FORMATS.stream()
                .map(format -> String.format(format, addressField.hostname(), addressField.port()))
                .map(url -> {
                    // TODO: 4/19/17 phuffer - override this guys path with addressField
                    UrlField urlField = new UrlField(addressField.fieldName());
                    urlField.setValue(url);
                    return urlField;
                })
                .map(urlField -> sendCswCapabilitiesRequest(urlField, creds))
                .filter(result -> !result.hasErrors())
                .findFirst()
                .orElse(new Result<UrlField>().argumentMessage(unknownEndpointError(addressField.fieldName())));
    }

    /**
     * Attempts to create a CSW configuration from the given url.
     *
     * @param urlField A URL of an endpoint with CSW capabilities
     * @param creds    optional credentials for basic authentication
     * @return a {@link Result} containing the {@link SourceConfigUnionField} or an {@link org.codice.ddf.admin.common.message.ErrorMessage} on failure.
     */
    public Result<SourceConfigUnionField> getPreferredCswConfig(UrlField urlField, CredentialsField creds) {
        Result<String> getResponseBody = requestUtils.sendGetRequest(urlField, creds,
                GET_CAPABILITIES_PARAMS);
        if (getResponseBody.hasErrors()) {
            return new Result<SourceConfigUnionField>().argumentMessages(getResponseBody.argumentMessages());
        }

        Result<SourceConfigUnionField> configResult = new Result<>();
        String requestBody = getResponseBody.get();
        Document capabilitiesXml;
        try {
            capabilitiesXml = createDocument(requestBody);
        } catch (Exception e) {
            LOGGER.debug("Failed to create XML document from response.");
            return configResult.argumentMessage(INTERNAL_ERROR_MESSAGE);
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
                return configResult.value(preferred.factoryPid(CSW_PROFILE_FACTORY_PID));
            }
        } catch (Exception e) {
            LOGGER.debug("Failed to compile DDF Profile CSW discovery XPath expression.");
        }

        try {
            if ((Boolean) xpath.compile(HAS_GMD_ISO_EXP)
                    .evaluate(capabilitiesXml, XPathConstants.BOOLEAN)) {
                return configResult.value(preferred.outputSchema(GMD_OUTPUT_SCHEMA)
                        .factoryPid(CSW_GMD_FACTORY_PID));
            }
        } catch (Exception e) {
            LOGGER.debug("Failed to compile GMD CSW discovery XPath expression.");
        }

        try {
            String outputSchema = xpath.compile(GET_FIRST_OUTPUT_SCHEMA)
                    .evaluate(capabilitiesXml);
            return configResult.value(preferred.outputSchema(outputSchema)
                    .factoryPid(CSW_SPEC_FACTORY_PID));
        } catch (Exception e) {
            LOGGER.debug("Failed to compile generic CSW specification discovery XPath expression.");
        }

        LOGGER.debug("URL [{}] responded to GetCapabilities request, but response was not readable",
                urlField.getValue());
        return configResult.argumentMessage(INTERNAL_ERROR_MESSAGE);
    }
}
