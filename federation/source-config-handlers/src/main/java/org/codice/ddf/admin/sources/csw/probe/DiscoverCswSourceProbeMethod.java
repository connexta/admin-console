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

import static org.codice.ddf.admin.api.config.sources.SourceConfiguration.PORT;
import static org.codice.ddf.admin.api.config.sources.SourceConfiguration.SOURCE_HOSTNAME;
import static org.codice.ddf.admin.api.config.sources.SourceConfiguration.SOURCE_USERNAME;
import static org.codice.ddf.admin.api.config.sources.SourceConfiguration.SOURCE_USER_PASSWORD;
import static org.codice.ddf.admin.api.handler.ConfigurationMessage.INTERNAL_ERROR;
import static org.codice.ddf.admin.api.handler.ConfigurationMessage.buildMessage;
import static org.codice.ddf.admin.api.handler.commons.SourceHandlerCommons.CANNOT_CONNECT;
import static org.codice.ddf.admin.api.handler.commons.SourceHandlerCommons.CERT_ERROR;
import static org.codice.ddf.admin.api.handler.commons.SourceHandlerCommons.CONFIG_CREATED;
import static org.codice.ddf.admin.api.handler.commons.SourceHandlerCommons.DISCOVERED_SOURCES;
import static org.codice.ddf.admin.api.handler.commons.SourceHandlerCommons.DISCOVER_SOURCES_ID;
import static org.codice.ddf.admin.api.handler.commons.SourceHandlerCommons.UNKNOWN_ENDPOINT;
import static org.codice.ddf.admin.api.handler.commons.SourceHandlerCommons.UNTRUSTED_CA;
import static org.codice.ddf.admin.api.handler.commons.SourceHandlerCommons.endpointIsReachable;
import static org.codice.ddf.admin.api.handler.report.ProbeReport.createProbeReport;
import static org.codice.ddf.admin.api.validation.SourceValidationUtils.validateOptionalUsernameAndPassword;
import static org.codice.ddf.admin.sources.csw.CswSourceConfigurationHandler.CSW_SOURCE_CONFIGURATION_HANDLER_ID;
import static org.codice.ddf.admin.sources.csw.CswSourceUtils.getPreferredConfig;

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

public class DiscoverCswSourceProbeMethod extends ProbeMethod<CswSourceConfiguration> {

    public static final String CSW_DISCOVER_SOURCES_ID = DISCOVER_SOURCES_ID;

    public static final String DESCRIPTION =
            "Attempts to discover a CSW endpoint based on a hostname and port using optional authentication information.";

    public static final List<String> REQUIRED_FIELDS = ImmutableList.of(SOURCE_HOSTNAME, PORT);

    public static final List<String> OPTIONAL_FIELDS = ImmutableList.of(SOURCE_USERNAME,
            SOURCE_USER_PASSWORD);

    public static final Map<String, String> SUCCESS_TYPES = ImmutableMap.of(CONFIG_CREATED,
            "Successfully created a configuration from the CSW endpoint.");

    public static final Map<String, String> FAILURE_TYPES = ImmutableMap.of(CANNOT_CONNECT,
            "The URL provided could not be reached.",
            UNKNOWN_ENDPOINT,
            "The endpoint does not appear to have CSW capabilities.",
            CERT_ERROR,
            "The discovered source has incorrectly configured SSL certificates and is insecure.",
            INTERNAL_ERROR,
            "Failed to create configuration from CSW URL.");

    public static final Map<String, String> WARNING_TYPES = ImmutableMap.of(UNTRUSTED_CA,
            "The discovered URL has incorrectly configured SSL certificates and is likely insecure.");

    public static final List<String> RETURN_TYPES = ImmutableList.of(DISCOVERED_SOURCES);

    public DiscoverCswSourceProbeMethod() {
        super(CSW_DISCOVER_SOURCES_ID,
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
        // TODO: tbatie - 2/1/17 - (Ticket) There is duplicate code across all the Discover/ConfigrFromUrl methods, this can all be eliminated
        CswSourceConfiguration config = new CswSourceConfiguration(configuration);
        ProbeReport probeReport = new ProbeReport(buildMessage(SUCCESS_TYPES,
                FAILURE_TYPES,
                WARNING_TYPES,
                endpointIsReachable(config.sourceHostName(), config.sourcePort())));
        if (probeReport.containsFailureMessages()) {
            return probeReport;
        }

        UrlAvailability availability = CswSourceUtils.confirmEndpointUrl(config);
        if (availability == null) {
            return probeReport.addMessage(buildMessage(SUCCESS_TYPES,
                    FAILURE_TYPES,
                    WARNING_TYPES,
                    UNKNOWN_ENDPOINT));
        }
        probeReport = createProbeReport(SUCCESS_TYPES,
                FAILURE_TYPES,
                WARNING_TYPES,
                availability.getAvailabilityResult());
        if (probeReport.containsFailureMessages()) {
            return probeReport;
        }

        Optional<CswSourceConfiguration> createdConfig =
                getPreferredConfig((CswSourceConfiguration) config.endpointUrl(availability.getUrl()));
        if (!createdConfig.isPresent()) {
            return probeReport.addMessage(buildMessage(SUCCESS_TYPES,
                    FAILURE_TYPES,
                    WARNING_TYPES,
                    INTERNAL_ERROR));
        }

        Map<String, Object> probeResult = new HashMap<>();
        probeResult.put(DISCOVERED_SOURCES,
                createdConfig.get()
                        .configurationHandlerId(CSW_SOURCE_CONFIGURATION_HANDLER_ID));

        return probeReport.addMessage(buildMessage(SUCCESS_TYPES,
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
