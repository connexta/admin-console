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
package org.codice.ddf.admin.sources.impl.probe;

import static org.codice.ddf.admin.api.config.sources.SourceConfiguration.PORT;
import static org.codice.ddf.admin.api.config.sources.SourceConfiguration.SOURCE_HOSTNAME;
import static org.codice.ddf.admin.api.config.sources.SourceConfiguration.SOURCE_USERNAME;
import static org.codice.ddf.admin.api.config.sources.SourceConfiguration.SOURCE_USER_PASSWORD;
import static org.codice.ddf.admin.api.handler.report.ProbeReport.createProbeReport;
import static org.codice.ddf.admin.api.validation.SourceValidationUtils.validateOptionalUsernameAndPassword;
import static org.codice.ddf.admin.commons.sources.SourceHandlerCommons.DISCOVERED_SOURCE;
import static org.codice.ddf.admin.commons.sources.SourceHandlerCommons.DISCOVERED_SOURCES;
import static org.codice.ddf.admin.commons.sources.SourceHandlerCommons.DISCOVER_SOURCES_ID;
import static org.codice.ddf.admin.commons.sources.SourceHandlerCommons.getCommonSourceSubtypeDescriptions;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.codice.ddf.admin.api.config.sources.SourceConfiguration;
import org.codice.ddf.admin.api.handler.ConfigurationMessage;
import org.codice.ddf.admin.api.handler.SourceConfigurationHandler;
import org.codice.ddf.admin.api.handler.method.ProbeMethod;
import org.codice.ddf.admin.api.handler.report.ProbeReport;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class DiscoverSourcesProbeMethod extends ProbeMethod<SourceConfiguration> {

    //Return types
    public static final String CONFIG = "config";
    public static final String MESSAGES = "messages";
    public static final String DESCRIPTION = "Retrieves possible configurations for the specified url. The results will be in a list of maps with the keys " + CONFIG + ", " + MESSAGES;

    public static final List<String> REQUIRED_FIELDS = ImmutableList.of(SOURCE_HOSTNAME, PORT);
    public static final List<String> OPTIONAL_FIELDS = ImmutableList.of(SOURCE_USERNAME, SOURCE_USER_PASSWORD);
    public static final Map<String, String> SUCCESS_TYPES =  getCommonSourceSubtypeDescriptions(DISCOVERED_SOURCE);

    //    public static final Map<String, String> FAILURE_TYPES = ImmutableMap.of(FAILED_PROBE, "No sources were discovered from the specified host.");
    public static final List<String> RETURN_TYPES = ImmutableList.of(DISCOVERED_SOURCES);
    private List<SourceConfigurationHandler> handlers;

    // TODO: tbatie - 2/1/17 - (Ticket) We can't return a failure type here because the frontend can't handle the error properly
    public DiscoverSourcesProbeMethod(List<SourceConfigurationHandler> handlers) {
        super(DISCOVER_SOURCES_ID,
                DESCRIPTION,
                REQUIRED_FIELDS,
                OPTIONAL_FIELDS,
                SUCCESS_TYPES,
                null,
                null,
                RETURN_TYPES);

        this.handlers = handlers;
    }

    @Override
    public ProbeReport probe(SourceConfiguration config) {
        List<ProbeReport> sourceProbeReports = handlers.stream()
                .map(handler -> handler.probe(DISCOVER_SOURCES_ID, config))
                .collect(Collectors.toList());

        List<Map<String, Object>> discoveredSources = sourceProbeReports.stream()
                .filter(probeReport -> !probeReport.containsFailureMessages())
                .map(probeReport -> ImmutableMap.of(CONFIG,
                        probeReport.probeResults().get(DISCOVERED_SOURCES),
                        MESSAGES,
                        probeReport.messages()))
                .collect(Collectors.toList());

        return createProbeReport(SUCCESS_TYPES, null, null, DISCOVERED_SOURCE).probeResult(
                DISCOVERED_SOURCES,
                discoveredSources);
    }

    @Override
    public List<ConfigurationMessage> validateOptionalFields(SourceConfiguration configuration) {
        return validateOptionalUsernameAndPassword(configuration);
    }
}
