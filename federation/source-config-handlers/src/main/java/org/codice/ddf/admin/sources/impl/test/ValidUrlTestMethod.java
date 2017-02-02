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
package org.codice.ddf.admin.sources.impl.test;

import static org.codice.ddf.admin.api.config.sources.SourceConfiguration.PORT;
import static org.codice.ddf.admin.api.config.sources.SourceConfiguration.SOURCE_HOSTNAME;
import static org.codice.ddf.admin.api.handler.ConfigurationMessage.buildMessage;
import static org.codice.ddf.admin.api.handler.commons.SourceHandlerCommons.CANNOT_CONNECT;
import static org.codice.ddf.admin.api.handler.commons.SourceHandlerCommons.REACHED_URL;
import static org.codice.ddf.admin.api.handler.commons.SourceHandlerCommons.VALID_URL_TEST_ID;

import java.util.List;
import java.util.Map;

import org.codice.ddf.admin.api.config.sources.SourceConfiguration;
import org.codice.ddf.admin.api.handler.commons.SourceHandlerCommons;
import org.codice.ddf.admin.api.handler.method.TestMethod;
import org.codice.ddf.admin.api.handler.report.Report;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class ValidUrlTestMethod extends TestMethod<SourceConfiguration> {

    private static final String DESCRIPTION = "Attempts to connect to a given hostname and port";

    private static final List<String> REQUIRED_FIELDS = ImmutableList.of(SOURCE_HOSTNAME, PORT);

    private static final Map<String, String> SUCCESS_TYPES = ImmutableMap.of(REACHED_URL,
            "Connected to hostname and port.");

    private static final Map<String, String> FAILURE_TYPES = ImmutableMap.of(CANNOT_CONNECT,
            "Unable to connect to hostname and port.");

    public ValidUrlTestMethod() {
        super(VALID_URL_TEST_ID,
                DESCRIPTION,
                REQUIRED_FIELDS,
                null,
                SUCCESS_TYPES,
                FAILURE_TYPES,
                null);
    }

    @Override
    public Report test(SourceConfiguration configuration) {
        return new Report(buildMessage(SUCCESS_TYPES,
                FAILURE_TYPES,
                null,
                SourceHandlerCommons.endpointIsReachable(configuration.sourceHostName(),
                        configuration.sourcePort())));
    }
}
