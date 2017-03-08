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
import static org.codice.ddf.admin.commons.requests.RequestUtils.CANNOT_CONNECT;
import static org.codice.ddf.admin.commons.requests.RequestUtils.CONNECTED;

import java.util.List;
import java.util.Map;

import org.codice.ddf.admin.api.config.sources.SourceConfiguration;
import org.codice.ddf.admin.api.handler.method.TestMethod;
import org.codice.ddf.admin.api.handler.report.Report;
import org.codice.ddf.admin.commons.requests.RequestUtils;

import com.google.common.collect.ImmutableList;

// TODO: tbatie - 2/2/17 - (Ticket) This class should eventually be removed once the frontend is capable of handle errors messages from a probe report
public class ValidUrlTestMethod extends TestMethod<SourceConfiguration> {

    private static final RequestUtils REQUEST_UTILS = new RequestUtils();

    private static final String DESCRIPTION = "Attempts to connect to a given hostname and port";

    public static final String VALID_URL_TEST_ID = "valid-url";

    private static final List<String> REQUIRED_FIELDS = ImmutableList.of(SOURCE_HOSTNAME, PORT);
    private static final Map<String, String> SUCCESS_TYPES = REQUEST_UTILS.
            getRequestSubtypeDescriptions(CONNECTED);
    private static final Map<String, String> FAILURE_TYPES = REQUEST_UTILS.
            getRequestSubtypeDescriptions(CANNOT_CONNECT);

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
        return REQUEST_UTILS.endpointIsReachable(configuration.sourceHostName(), configuration.sourcePort());
    }
}
