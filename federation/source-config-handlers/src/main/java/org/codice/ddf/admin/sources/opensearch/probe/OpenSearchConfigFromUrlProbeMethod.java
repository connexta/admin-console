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

import static org.codice.ddf.admin.api.config.sources.SourceConfiguration.ENDPOINT_URL;
import static org.codice.ddf.admin.api.config.sources.SourceConfiguration.SOURCE_USERNAME;
import static org.codice.ddf.admin.api.config.sources.SourceConfiguration.SOURCE_USER_PASSWORD;
import static org.codice.ddf.admin.api.handler.ConfigurationMessage.buildMessage;
import static org.codice.ddf.admin.api.handler.commons.SourceHandlerCommons.BAD_CONFIG;
import static org.codice.ddf.admin.api.handler.commons.SourceHandlerCommons.CANNOT_CONNECT;
import static org.codice.ddf.admin.api.handler.commons.SourceHandlerCommons.CERT_ERROR;
import static org.codice.ddf.admin.api.handler.commons.SourceHandlerCommons.CONFIG_CREATED;
import static org.codice.ddf.admin.api.handler.commons.SourceHandlerCommons.CONFIG_FROM_URL_ID;
import static org.codice.ddf.admin.api.handler.commons.SourceHandlerCommons.DISCOVERED_SOURCES;
import static org.codice.ddf.admin.api.handler.commons.SourceHandlerCommons.UNKNOWN_ENDPOINT;
import static org.codice.ddf.admin.api.handler.commons.SourceHandlerCommons.UNTRUSTED_CA;
import static org.codice.ddf.admin.api.handler.commons.SourceHandlerCommons.endpointIsReachable;
import static org.codice.ddf.admin.api.handler.report.ProbeReport.createProbeReport;
import static org.codice.ddf.admin.api.services.OpenSearchServiceProperties.OPENSEARCH_FACTORY_PID;
import static org.codice.ddf.admin.api.validation.SourceValidationUtils.validateOptionalUsernameAndPassword;
import static org.codice.ddf.admin.sources.opensearch.OpenSearchSourceConfigurationHandler.OPENSEARCH_SOURCE_CONFIGURATION_HANDLER_ID;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codice.ddf.admin.api.config.sources.OpenSearchSourceConfiguration;
import org.codice.ddf.admin.api.handler.ConfigurationMessage;
import org.codice.ddf.admin.api.handler.commons.UrlAvailability;
import org.codice.ddf.admin.api.handler.method.ProbeMethod;
import org.codice.ddf.admin.api.handler.report.ProbeReport;
import org.codice.ddf.admin.sources.opensearch.OpenSearchSourceUtils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class OpenSearchConfigFromUrlProbeMethod extends ProbeMethod<OpenSearchSourceConfiguration> {

    public static final String OPENSEARCH_CONFIG_FROM_URL_ID = CONFIG_FROM_URL_ID;

    public static final String DESCRIPTION =
            "Attempts to create an OpenSearch configuration from a given URL.";

    public static final List<String> OPTIONAL_FIELDS = ImmutableList.of(SOURCE_USERNAME,
            SOURCE_USER_PASSWORD);

    public static final List<String> REQUIRED_FIELDS = ImmutableList.of(ENDPOINT_URL);

    public static final Map<String, String> SUCCESS_TYPES = ImmutableMap.of(CONFIG_CREATED,
            "Created OpenSearch configuration from provided URL.");

    public static final Map<String, String> FAILURE_TYPES = ImmutableMap.of(BAD_CONFIG,
            "Failed to create a configuration from the URL.",
            CANNOT_CONNECT,
            "THe URL provided could not be reached.",
            CERT_ERROR,
            "The URL provided has improperly configured SSL certificates and is insecure.");

    public static final Map<String, String> WARNING_TYPES = ImmutableMap.of(UNTRUSTED_CA,
            "The URL's SSL certificate has been signed by an untrusted certificate authority and may be insecure.");

    public static final List<String> RETURN_TYPES = ImmutableList.of(DISCOVERED_SOURCES);

    private OpenSearchSourceUtils utils;

    public OpenSearchConfigFromUrlProbeMethod() {
        super(OPENSEARCH_CONFIG_FROM_URL_ID,
                DESCRIPTION,
                REQUIRED_FIELDS,
                OPTIONAL_FIELDS,
                SUCCESS_TYPES,
                FAILURE_TYPES,
                WARNING_TYPES,
                RETURN_TYPES);
        utils = new OpenSearchSourceUtils();
    }

    @Override
    public ProbeReport probe(OpenSearchSourceConfiguration configuration) {
        OpenSearchSourceConfiguration configCopy = new OpenSearchSourceConfiguration(configuration);
        ProbeReport report = createProbeReport(SUCCESS_TYPES,
                FAILURE_TYPES,
                WARNING_TYPES,
                endpointIsReachable(configuration.endpointUrl()));
        if (report.containsFailureMessages()) {
            return report;
        }

        UrlAvailability availability = utils.getUrlAvailability(
                configuration.endpointUrl(),
                configuration.sourceUserName(),
                configuration.sourceUserPassword());
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

        Map<String, Object> probeResult = new HashMap<>();
        probeResult.put(DISCOVERED_SOURCES,
                configCopy.endpointUrl(availability.getUrl())
                        .factoryPid(OPENSEARCH_FACTORY_PID)
                        .configurationHandlerId(OPENSEARCH_SOURCE_CONFIGURATION_HANDLER_ID));

        return report.probeResults(probeResult);
    }

    @Override
    public List<ConfigurationMessage> validateOptionalFields(
            OpenSearchSourceConfiguration configuration) {
        return validateOptionalUsernameAndPassword(configuration);
    }
}
