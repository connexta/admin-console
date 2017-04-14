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
package org.codice.ddf.admin.sources.opensearch.test;

import static org.codice.ddf.admin.api.handler.ConfigurationMessage.buildMessage;
import static org.codice.ddf.admin.api.handler.commons.HandlerCommons.SUCCESSFUL_TEST;
import static org.codice.ddf.admin.api.services.OpenSearchServiceProperties.OPENSEARCH_FACTORY_PID;
import static org.codice.ddf.admin.commons.sources.SourceHandlerCommons.SOURCE_NAME_EXISTS_TEST_ID;
import static org.codice.ddf.admin.sources.impl.test.SourceNameExistsTestMethod.DESCRIPTION;
import static org.codice.ddf.admin.sources.impl.test.SourceNameExistsTestMethod.FAILURE_TYPES;
import static org.codice.ddf.admin.sources.impl.test.SourceNameExistsTestMethod.REQUIRED_FIELDS;
import static org.codice.ddf.admin.sources.impl.test.SourceNameExistsTestMethod.SUCCESS_TYPES;

import java.util.Collections;
import java.util.List;

import org.codice.ddf.admin.api.config.sources.SourceConfiguration;
import org.codice.ddf.admin.api.handler.ConfigurationMessage;
import org.codice.ddf.admin.api.handler.method.TestMethod;
import org.codice.ddf.admin.api.handler.report.Report;
import org.codice.ddf.admin.api.validation.SourceValidationUtils;
import org.codice.ddf.admin.configurator.ConfiguratorFactory;

public class SourceNameExistsOpenSearchTestMethod extends TestMethod<SourceConfiguration> {

    public static final String OPENSEARCH_SOURCE_EXISTS_ID = SOURCE_NAME_EXISTS_TEST_ID;

    private final ConfiguratorFactory configuratorFactory;

    private final SourceValidationUtils sourceValidationUtils;

    public SourceNameExistsOpenSearchTestMethod(ConfiguratorFactory configuratorFactory) {
        super(OPENSEARCH_SOURCE_EXISTS_ID,
                DESCRIPTION,
                REQUIRED_FIELDS,
                null,
                SUCCESS_TYPES,
                FAILURE_TYPES,
                null);

        sourceValidationUtils = new SourceValidationUtils();

        this.configuratorFactory = configuratorFactory;
    }

    @Override
    public Report test(SourceConfiguration configuration) {
        List<ConfigurationMessage> results =
                sourceValidationUtils.validateSourceName(configuration.sourceName(),
                        Collections.singletonList(OPENSEARCH_FACTORY_PID),
                        configuratorFactory.getConfigReader());

        if (results.isEmpty()) {
            return new Report(buildMessage(SUCCESS_TYPES, FAILURE_TYPES, null, SUCCESSFUL_TEST));
        }

        return new Report(results);
    }
}
