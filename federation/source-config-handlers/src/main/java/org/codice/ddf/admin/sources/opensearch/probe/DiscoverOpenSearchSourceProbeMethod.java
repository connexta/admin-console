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
package org.codice.ddf.admin.sources.opensearch.probe;

import static org.codice.ddf.admin.api.config.sources.SourceConfiguration.PORT;
import static org.codice.ddf.admin.api.config.sources.SourceConfiguration.SOURCE_HOSTNAME;
import static org.codice.ddf.admin.api.config.sources.SourceConfiguration.SOURCE_USERNAME;
import static org.codice.ddf.admin.api.config.sources.SourceConfiguration.SOURCE_USER_PASSWORD;
import static org.codice.ddf.admin.api.handler.ConfigurationMessage.buildMessage;
import static org.codice.ddf.admin.api.handler.commons.SourceHandlerCommons.BAD_CONFIG;
import static org.codice.ddf.admin.api.handler.commons.SourceHandlerCommons.CERT_ERROR;
import static org.codice.ddf.admin.api.handler.commons.SourceHandlerCommons.CONFIG_CREATED;
import static org.codice.ddf.admin.api.handler.commons.SourceHandlerCommons.DISCOVERED_SOURCES;
import static org.codice.ddf.admin.api.handler.commons.SourceHandlerCommons.DISCOVER_SOURCES_ID;
import static org.codice.ddf.admin.api.handler.commons.SourceHandlerCommons.UNKNOWN_ENDPOINT;
import static org.codice.ddf.admin.api.handler.commons.SourceHandlerCommons.UNTRUSTED_CA;
import static org.codice.ddf.admin.api.handler.commons.SourceHandlerCommons.endpointIsReachable;
import static org.codice.ddf.admin.api.services.OpenSearchServiceProperties.OPENSEARCH_FACTORY_PID;
import static org.codice.ddf.admin.sources.opensearch.OpenSearchSourceConfigurationHandler.OPENSEARCH_SOURCE_CONFIGURATION_HANDLER_ID;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codice.ddf.admin.api.config.sources.OpenSearchSourceConfiguration;
import org.codice.ddf.admin.api.handler.ConfigurationMessage;
import org.codice.ddf.admin.api.handler.commons.UrlAvailability;
import org.codice.ddf.admin.api.handler.method.ProbeMethod;
import org.codice.ddf.admin.api.handler.report.ProbeReport;
import org.codice.ddf.admin.api.validation.SourceValidationUtils;
import org.codice.ddf.admin.sources.opensearch.OpenSearchSourceUtils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class DiscoverOpenSearchSourceProbeMethod
        extends ProbeMethod<OpenSearchSourceConfiguration> {

    public static final String OPENSEARCH_DISCOVER_SOURCES_ID = DISCOVER_SOURCES_ID;

    public static final String DESCRIPTION =
            "Attempts to discover a OpenSearch endpoint based on a hostname and port using optional authentication information.";

    public static final List<String> REQUIRED_FIELDS = ImmutableList.of(SOURCE_HOSTNAME, PORT);

    public static final List<String> OPTIONAL_FIELDS = ImmutableList.of(SOURCE_USERNAME,
            SOURCE_USER_PASSWORD);

    public static final Map<String, String> SUCCESS_TYPES = ImmutableMap.of(CONFIG_CREATED,
            "Successfully created a configuration from the OpenSearch endpoint.");

    public static final Map<String, String> FAILURE_TYPES = ImmutableMap.of(CERT_ERROR,
            "The discovered source has incorrectly configured SSL certificates and is insecure.",
            UNKNOWN_ENDPOINT,
            "No OpenSearch endpoint found.",
            BAD_CONFIG,
            "Endpoint discovered, but could not create valid configuration.");

    public static final Map<String, String> WARNING_TYPES = ImmutableMap.of(UNTRUSTED_CA,
            "The discovered URL has incorrectly configured SSL certificates and is likely insecure.");

    public static final List<String> RETURN_TYPES = ImmutableList.of(DISCOVERED_SOURCES);

    private final OpenSearchSourceUtils openSearchSourceUtils;

    private final SourceValidationUtils sourceValidationUtils;

    public DiscoverOpenSearchSourceProbeMethod() {
        super(OPENSEARCH_DISCOVER_SOURCES_ID,
                DESCRIPTION,
                REQUIRED_FIELDS,
                OPTIONAL_FIELDS,
                SUCCESS_TYPES,
                FAILURE_TYPES,
                null,
                RETURN_TYPES);

        openSearchSourceUtils = new OpenSearchSourceUtils();
        sourceValidationUtils = new SourceValidationUtils();
    }

    @Override
    public ProbeReport probe(OpenSearchSourceConfiguration configuration) {
        ProbeReport probeReport = new ProbeReport(buildMessage(SUCCESS_TYPES,
                FAILURE_TYPES,
                WARNING_TYPES,
                endpointIsReachable(configuration.endpointUrl())));
        if (probeReport.containsFailureMessages()) {
            return probeReport;
        }

        UrlAvailability availability = openSearchSourceUtils.confirmEndpointUrl(configuration);
        if (availability == null) {
            return probeReport.addMessage(buildMessage(SUCCESS_TYPES,
                    FAILURE_TYPES,
                    WARNING_TYPES,
                    UNKNOWN_ENDPOINT));
        }

        probeReport.addMessage(buildMessage(SUCCESS_TYPES,
                FAILURE_TYPES,
                WARNING_TYPES,
                availability.getAvailabilityResult()));
        if (probeReport.containsFailureMessages()) {
            return probeReport;
        }

        Map<String, Object> probeResult = new HashMap<>();
        OpenSearchSourceConfiguration openSearchConfig = new OpenSearchSourceConfiguration(
                configuration);
        openSearchConfig.endpointUrl(availability.getUrl())
                .factoryPid(OPENSEARCH_FACTORY_PID)
                .configurationHandlerId(OPENSEARCH_SOURCE_CONFIGURATION_HANDLER_ID);

        probeResult.put(DISCOVERED_SOURCES, openSearchConfig);
        return probeReport.addMessage(buildMessage(SUCCESS_TYPES,
                FAILURE_TYPES,
                WARNING_TYPES,
                CONFIG_CREATED))
                .probeResults(probeResult);
    }

    @Override
    public List<ConfigurationMessage> validateOptionalFields(
            OpenSearchSourceConfiguration configuration) {
        return sourceValidationUtils.validateOptionalUsernameAndPassword(configuration);
    }
}
