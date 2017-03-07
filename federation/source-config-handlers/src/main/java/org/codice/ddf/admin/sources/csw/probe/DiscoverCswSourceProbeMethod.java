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
import static org.codice.ddf.admin.api.config.sources.SourceConfiguration.PORT;
import static org.codice.ddf.admin.api.config.sources.SourceConfiguration.SOURCE_HOSTNAME;
import static org.codice.ddf.admin.api.config.sources.SourceConfiguration.SOURCE_USERNAME;
import static org.codice.ddf.admin.api.config.sources.SourceConfiguration.SOURCE_USER_PASSWORD;
import static org.codice.ddf.admin.api.validation.SourceValidationUtils.validateForSourceDiscovery;
import static org.codice.ddf.admin.commons.requests.RequestUtils.CANNOT_CONNECT;
import static org.codice.ddf.admin.commons.requests.RequestUtils.CERT_ERROR;
import static org.codice.ddf.admin.commons.requests.RequestUtils.UNTRUSTED_CA;
import static org.codice.ddf.admin.commons.sources.SourceHandlerCommons.DISCOVERED_SOURCE;
import static org.codice.ddf.admin.commons.sources.SourceHandlerCommons.DISCOVERED_SOURCES;
import static org.codice.ddf.admin.commons.sources.SourceHandlerCommons.DISCOVERED_URL;
import static org.codice.ddf.admin.commons.sources.SourceHandlerCommons.DISCOVER_SOURCES_ID;
import static org.codice.ddf.admin.commons.sources.SourceHandlerCommons.UNKNOWN_ENDPOINT;
import static org.codice.ddf.admin.commons.sources.SourceHandlerCommons.getCommonSourceSubtypeDescriptions;

import java.util.List;
import java.util.Map;

import org.codice.ddf.admin.api.config.sources.CswSourceConfiguration;
import org.codice.ddf.admin.api.handler.ConfigurationMessage;
import org.codice.ddf.admin.api.handler.method.ProbeMethod;
import org.codice.ddf.admin.api.handler.report.ProbeReport;
import org.codice.ddf.admin.sources.csw.CswSourceUtils;

import com.google.common.collect.ImmutableList;

public class DiscoverCswSourceProbeMethod extends ProbeMethod<CswSourceConfiguration> {

    public static final String CSW_DISCOVER_SOURCES_ID = DISCOVER_SOURCES_ID;

    public static final String DESCRIPTION =
            "Attempts to discover a CSW endpoint based on a hostname and port using optional authentication information. If the \"endpointUrl\" is specified it will use this to create a configuration instead of discovering the url.";

    public static final List<String> OPTIONAL_FIELDS = ImmutableList.of(SOURCE_HOSTNAME,
            PORT,
            ENDPOINT_URL,
            SOURCE_USERNAME,
            SOURCE_USER_PASSWORD,
            ENDPOINT_URL);

    public static final Map<String, String> SUCCESS_TYPES = getCommonSourceSubtypeDescriptions(
            DISCOVERED_SOURCE);

    public static final Map<String, String> FAILURE_TYPES = getCommonSourceSubtypeDescriptions(
            CERT_ERROR,
            UNKNOWN_ENDPOINT,
            CANNOT_CONNECT);

    public static final Map<String, String> WARNING_TYPES = getCommonSourceSubtypeDescriptions(
            UNTRUSTED_CA);

    public static final List<String> RETURN_TYPES = ImmutableList.of(DISCOVERED_SOURCES);
    private final CswSourceUtils cswSourceUtils;

    public DiscoverCswSourceProbeMethod() {
        super(CSW_DISCOVER_SOURCES_ID,
                DESCRIPTION,
                null,
                OPTIONAL_FIELDS,
                SUCCESS_TYPES,
                FAILURE_TYPES,
                WARNING_TYPES,
                RETURN_TYPES);
        cswSourceUtils = new CswSourceUtils();
    }

    @Override
    public ProbeReport probe(CswSourceConfiguration configuration) {
        String un = configuration.sourceUserName();
        String pw = configuration.sourceUserPassword();
        String testUrl = configuration.endpointUrl();
        if (testUrl == null) {
            ProbeReport discoverEndpointReport = cswSourceUtils.discoverCswUrl(configuration.sourceHostName(),
                    configuration.sourcePort(),
                    un,
                    pw);
            if (discoverEndpointReport.containsFailureMessages()) {
                return discoverEndpointReport;
            }
            testUrl = discoverEndpointReport.getProbeResult(DISCOVERED_URL);
        }
        return cswSourceUtils.getPreferredCswConfig(testUrl, un, pw);
    }

    @Override
    public List<ConfigurationMessage> validateOptionalFields(CswSourceConfiguration configuration) {
        return validateForSourceDiscovery(configuration);
    }
}