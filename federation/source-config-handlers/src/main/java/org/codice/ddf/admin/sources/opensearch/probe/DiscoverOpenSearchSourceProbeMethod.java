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
import static org.codice.ddf.admin.api.config.sources.SourceConfiguration.PORT;
import static org.codice.ddf.admin.api.config.sources.SourceConfiguration.SOURCE_HOSTNAME;
import static org.codice.ddf.admin.api.config.sources.SourceConfiguration.SOURCE_USERNAME;
import static org.codice.ddf.admin.api.config.sources.SourceConfiguration.SOURCE_USER_PASSWORD;
import static org.codice.ddf.admin.api.validation.SourceValidationUtils.validateForSourceDiscovery;
import static org.codice.ddf.admin.commons.requests.RequestUtils.CANNOT_CONNECT;
import static org.codice.ddf.admin.commons.requests.RequestUtils.CERT_ERROR;
import static org.codice.ddf.admin.commons.requests.RequestUtils.UNTRUSTED_CA;
import static org.codice.ddf.admin.commons.sources.SourceHandlerCommons.CREATED_SOURCE;
import static org.codice.ddf.admin.commons.sources.SourceHandlerCommons.DISCOVERED_SOURCES;
import static org.codice.ddf.admin.commons.sources.SourceHandlerCommons.DISCOVERED_URL;
import static org.codice.ddf.admin.commons.sources.SourceHandlerCommons.DISCOVER_SOURCES_ID;
import static org.codice.ddf.admin.commons.sources.SourceHandlerCommons.UNKNOWN_ENDPOINT;
import static org.codice.ddf.admin.commons.sources.SourceHandlerCommons.getCommonSourceSubtypeDescriptions;
import static org.codice.ddf.admin.sources.opensearch.OpenSearchSourceUtils.discoverOpenSearchUrl;
import static org.codice.ddf.admin.sources.opensearch.OpenSearchSourceUtils.getOpenSearchConfig;

import java.util.List;
import java.util.Map;

import org.codice.ddf.admin.api.config.sources.OpenSearchSourceConfiguration;
import org.codice.ddf.admin.api.handler.ConfigurationMessage;
import org.codice.ddf.admin.api.handler.method.ProbeMethod;
import org.codice.ddf.admin.api.handler.report.ProbeReport;

import com.google.common.collect.ImmutableList;

public class DiscoverOpenSearchSourceProbeMethod extends ProbeMethod<OpenSearchSourceConfiguration> {

    public static final String OPENSEARCH_DISCOVER_SOURCES_ID = DISCOVER_SOURCES_ID;
    public static final String DESCRIPTION = "Attempts to discover an OpenSearch endpoint based on a hostname and port using optional authentication information. If the \"endpointUrl\" is specified it will use this to create a configuration instead of discovering the url.";
    public static final List<String> OPTIONAL_FIELDS = ImmutableList.of(SOURCE_HOSTNAME, PORT, ENDPOINT_URL, SOURCE_USERNAME, SOURCE_USER_PASSWORD);
    public static final Map<String, String> SUCCESS_TYPES = getCommonSourceSubtypeDescriptions(CREATED_SOURCE);
    public static final Map<String, String> FAILURE_TYPES = getCommonSourceSubtypeDescriptions(CERT_ERROR, UNKNOWN_ENDPOINT, CANNOT_CONNECT);
    public static final Map<String, String> WARNING_TYPES = getCommonSourceSubtypeDescriptions(UNTRUSTED_CA);
    public static final List<String> RETURN_TYPES = ImmutableList.of(DISCOVERED_SOURCES);

    public DiscoverOpenSearchSourceProbeMethod() {
        super(OPENSEARCH_DISCOVER_SOURCES_ID,
                DESCRIPTION,
                null,
                OPTIONAL_FIELDS,
                SUCCESS_TYPES,
                FAILURE_TYPES,
                WARNING_TYPES,
                RETURN_TYPES);
    }

    @Override
    public ProbeReport probe(OpenSearchSourceConfiguration configuration) {
        String testUrl = configuration.endpointUrl();
        String un = configuration.sourceUserName();
        String pw = configuration.sourceUserPassword();
        if (testUrl == null) {
            ProbeReport discoveryReport = discoverOpenSearchUrl(configuration.sourceHostName(),
                    configuration.sourcePort(),
                    un,
                    pw);
            if (discoveryReport.containsFailureMessages()) {
                return discoveryReport;
            }
            testUrl = discoveryReport.getProbeResult(DISCOVERED_URL);
        }
        return getOpenSearchConfig(testUrl, un, pw);
    }

    @Override
    public List<ConfigurationMessage> validateOptionalFields(OpenSearchSourceConfiguration configuration) {
        return validateForSourceDiscovery(configuration);
    }
}
