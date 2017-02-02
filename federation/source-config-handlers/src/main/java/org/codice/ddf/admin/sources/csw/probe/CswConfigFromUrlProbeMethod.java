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
package org.codice.ddf.admin.sources.csw.probe;

import static org.codice.ddf.admin.api.config.sources.SourceConfiguration.ENDPOINT_URL;
import static org.codice.ddf.admin.api.config.sources.SourceConfiguration.SOURCE_USERNAME;
import static org.codice.ddf.admin.api.config.sources.SourceConfiguration.SOURCE_USER_PASSWORD;
import static org.codice.ddf.admin.api.handler.ConfigurationMessage.INTERNAL_ERROR;
import static org.codice.ddf.admin.api.handler.ConfigurationMessage.buildMessage;
import static org.codice.ddf.admin.api.handler.commons.SourceHandlerCommons.CANNOT_CONNECT;
import static org.codice.ddf.admin.api.handler.commons.SourceHandlerCommons.CERT_ERROR;
import static org.codice.ddf.admin.api.handler.commons.SourceHandlerCommons.CONFIG_CREATED;
import static org.codice.ddf.admin.api.handler.commons.SourceHandlerCommons.CONFIG_FROM_URL_ID;
import static org.codice.ddf.admin.api.handler.commons.SourceHandlerCommons.DISCOVERED_SOURCES;
import static org.codice.ddf.admin.api.handler.commons.SourceHandlerCommons.REACHED_URL;
import static org.codice.ddf.admin.api.handler.commons.SourceHandlerCommons.UNKNOWN_ENDPOINT;
import static org.codice.ddf.admin.api.handler.commons.SourceHandlerCommons.UNTRUSTED_CA;
import static org.codice.ddf.admin.api.handler.commons.SourceHandlerCommons.VERIFIED_URL;
import static org.codice.ddf.admin.api.handler.commons.SourceHandlerCommons.endpointIsReachable;
import static org.codice.ddf.admin.api.validation.SourceValidationUtils.validateOptionalUsernameAndPassword;
import static org.codice.ddf.admin.sources.csw.CswSourceConfigurationHandler.CSW_SOURCE_CONFIGURATION_HANDLER_ID;
import static org.codice.ddf.admin.sources.csw.CswSourceUtils.getUrlAvailability;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.codice.ddf.admin.api.config.sources.CswSourceConfiguration;
import org.codice.ddf.admin.api.handler.ConfigurationMessage;
import org.codice.ddf.admin.api.handler.commons.UrlAvailability;
import org.codice.ddf.admin.api.handler.method.ProbeMethod;
import org.codice.ddf.admin.api.handler.report.ProbeReport;
import org.codice.ddf.admin.sources.csw.CswSourceUtils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class CswConfigFromUrlProbeMethod extends ProbeMethod<CswSourceConfiguration> {

    public static final String CSW_CONFIG_FROM_URL_ID = CONFIG_FROM_URL_ID;

    public static final String DESCRIPTION =
            "Attempts to create a CSW configuration from a verified CSW URL.";

    public static final List<String> OPTIONAL_FIELDS = ImmutableList.of(SOURCE_USERNAME,
            SOURCE_USER_PASSWORD);

    public static final List<String> REQUIRED_FIELDS = ImmutableList.of(ENDPOINT_URL);

    public static final Map<String, String> SUCCESS_TYPES = ImmutableMap.of(CONFIG_CREATED,
            "Created CSW configuration from provided URL.",
            REACHED_URL,
            "Successfully connected to URL.",
            VERIFIED_URL,
            "Endpoint was successfully verified as a CSW endpoint.");

    public static final Map<String, String> FAILURE_TYPES = ImmutableMap.of(CANNOT_CONNECT,
            "The URL provided could not be reached.",
            UNKNOWN_ENDPOINT,
            "The endpoint does not appear to have CSW capabilities.",
            CERT_ERROR,
            "The URL provided has improperly configured SSL Certificates and is insecure.",
            INTERNAL_ERROR,
            "Failed to create a config from CSW URL.");

    public static final Map<String, String> WARNING_TYPES = ImmutableMap.of(UNTRUSTED_CA,
            "The URL's SSL certificate has been signed by an untrusted certificate authority, and is likely insecure.");

    public static final List<String> RETURN_TYPES = ImmutableList.of(DISCOVERED_SOURCES);

    public CswConfigFromUrlProbeMethod() {
        super(CSW_CONFIG_FROM_URL_ID,
                DESCRIPTION,
                REQUIRED_FIELDS,
                OPTIONAL_FIELDS,
                SUCCESS_TYPES,
                FAILURE_TYPES,
                WARNING_TYPES,
                RETURN_TYPES);
    }

    @Override
    public ProbeReport probe(CswSourceConfiguration configuration) {
        ProbeReport report = ProbeReport.createProbeReport(SUCCESS_TYPES,
                FAILURE_TYPES,
                WARNING_TYPES,
                endpointIsReachable(configuration.endpointUrl()));
        if (report.containsFailureMessages()) {
            return report;
        }

        UrlAvailability availability = getUrlAvailability(configuration.endpointUrl());
        if (availability == null) {
            return report.addMessage(buildMessage(SUCCESS_TYPES,
                    FAILURE_TYPES,
                    WARNING_TYPES,
                    UNKNOWN_ENDPOINT));
        }

        report.addMessage(buildMessage(SUCCESS_TYPES,
                FAILURE_TYPES,
                WARNING_TYPES,
                availability.getAvailabilityResult()));
        if (report.containsFailureMessages()) {
            return report;
        }

        Optional<CswSourceConfiguration> createdConfig = CswSourceUtils.getPreferredConfig(
                configuration);
        if (!createdConfig.isPresent()) {
            return report.addMessage(buildMessage(SUCCESS_TYPES,
                    FAILURE_TYPES,
                    WARNING_TYPES,
                    INTERNAL_ERROR));
        }

        Map<String, Object> probeResult = new HashMap<>();
        probeResult.put(DISCOVERED_SOURCES,
                createdConfig.get()
                        .configurationHandlerId(CSW_SOURCE_CONFIGURATION_HANDLER_ID));

        return report.addMessage(buildMessage(SUCCESS_TYPES,
                FAILURE_TYPES,
                WARNING_TYPES,
                CONFIG_CREATED))
                .probeResults(probeResult);
    }

    @Override
    public List<ConfigurationMessage> validateOptionalFields(CswSourceConfiguration configuration) {
        return validateOptionalUsernameAndPassword(configuration);
    }
}
