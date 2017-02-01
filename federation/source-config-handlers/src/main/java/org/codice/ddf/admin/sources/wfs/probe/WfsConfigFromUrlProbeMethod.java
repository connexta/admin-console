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
package org.codice.ddf.admin.sources.wfs.probe;

import static org.codice.ddf.admin.api.config.sources.SourceConfiguration.ENDPOINT_URL;
import static org.codice.ddf.admin.api.config.sources.SourceConfiguration.SOURCE_USERNAME;
import static org.codice.ddf.admin.api.config.sources.SourceConfiguration.SOURCE_USER_PASSWORD;
import static org.codice.ddf.admin.api.handler.commons.SourceHandlerCommons.BAD_CONFIG;
import static org.codice.ddf.admin.api.handler.commons.SourceHandlerCommons.CANNOT_CONNECT;
import static org.codice.ddf.admin.api.handler.commons.SourceHandlerCommons.CERT_ERROR;
import static org.codice.ddf.admin.api.handler.commons.SourceHandlerCommons.CONFIG_CREATED;
import static org.codice.ddf.admin.api.handler.commons.SourceHandlerCommons.CONFIG_FROM_URL_ID;
import static org.codice.ddf.admin.api.handler.commons.SourceHandlerCommons.DISCOVERED_SOURCES;
import static org.codice.ddf.admin.api.handler.commons.SourceHandlerCommons.UNTRUSTED_CA;
import static org.codice.ddf.admin.api.handler.report.ProbeReport.createProbeReport;
import static org.codice.ddf.admin.api.validation.SourceValidationUtils.validateOptionalUsernameAndPassword;
import static org.codice.ddf.admin.sources.wfs.WfsSourceConfigurationHandler.WFS_SOURCE_CONFIGURATION_HANDLER_ID;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.codice.ddf.admin.api.config.sources.WfsSourceConfiguration;
import org.codice.ddf.admin.api.handler.ConfigurationMessage;
import org.codice.ddf.admin.api.handler.commons.UrlAvailability;
import org.codice.ddf.admin.api.handler.method.ProbeMethod;
import org.codice.ddf.admin.api.handler.report.ProbeReport;
import org.codice.ddf.admin.sources.wfs.WfsSourceUtils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class WfsConfigFromUrlProbeMethod extends ProbeMethod<WfsSourceConfiguration> {

    public static final String WFS_CONFIG_FROM_URL_ID = CONFIG_FROM_URL_ID;
    public static final String DESCRIPTION = "Attempts to create a WFS configuration from a given URL.";
    public static final List<String> REQUIRED_FIELDS = ImmutableList.of(ENDPOINT_URL);
    public static final List<String> OPTIONAL_FIELDS = ImmutableList.of(SOURCE_USERNAME,
            SOURCE_USER_PASSWORD);
    public static final Map<String, String> SUCCESS_TYPES = ImmutableMap.of(CONFIG_CREATED, "WFS configuration was successfully created.");
    public static final Map<String, String> FAILURE_TYPES = ImmutableMap.of(
            BAD_CONFIG, "Failed to create config from provided URL.",
            CANNOT_CONNECT, "Could not reach specified URL.",
            CERT_ERROR, "The URL provided has improperly configured SSL certificates and is insecure.");
    public static final Map<String, String> WARNING_TYPES = ImmutableMap.of(
            UNTRUSTED_CA, "The URL's SSL certificated has been signed by an untrusted certificate authority and may be insecure.");

    public static final List<String> RETURN_TYPES = ImmutableList.of(DISCOVERED_SOURCES);


    public WfsConfigFromUrlProbeMethod() {
        super(WFS_CONFIG_FROM_URL_ID,
                DESCRIPTION,
                REQUIRED_FIELDS,
                OPTIONAL_FIELDS,
                SUCCESS_TYPES,
                FAILURE_TYPES,
                WARNING_TYPES,
                RETURN_TYPES);
    }

    @Override
    public ProbeReport probe(WfsSourceConfiguration configuration) {
        UrlAvailability status = WfsSourceUtils.getUrlAvailability(configuration.endpointUrl());
        String result;
        Map<String, Object> probeResult = new HashMap<>();
        if (status.isAvailable()) {
            Optional<WfsSourceConfiguration> preferred = WfsSourceUtils.getPreferredConfig(configuration);
            if (preferred.isPresent()) {
                result = status.isTrustedCertAuthority() ? CONFIG_CREATED : UNTRUSTED_CA;
                probeResult.put(DISCOVERED_SOURCES, preferred.get().configurationHandlerId(WFS_SOURCE_CONFIGURATION_HANDLER_ID));
            } else {
                result = BAD_CONFIG;
            }
        } else {
            result = status.isCertError() ? CERT_ERROR : CANNOT_CONNECT;
        }
        return createProbeReport(SUCCESS_TYPES, FAILURE_TYPES, WARNING_TYPES, result).probeResults(probeResult);
    }

    @Override
    public List<ConfigurationMessage> validateOptionalFields(WfsSourceConfiguration configuration) {
        return validateOptionalUsernameAndPassword(configuration);
    }
}
