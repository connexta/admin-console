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
package org.codice.ddf.admin.sources.csw.test;

import static org.codice.ddf.admin.api.handler.ConfigurationMessage.buildMessage;
import static org.codice.ddf.admin.api.services.CswServiceProperties.CSW_FACTORY_PIDS;
import static org.codice.ddf.admin.sources.impl.test.SourceNameExistsTestMethod.DESCRIPTION;
import static org.codice.ddf.admin.sources.impl.test.SourceNameExistsTestMethod.FAILURE_TYPES;
import static org.codice.ddf.admin.sources.impl.test.SourceNameExistsTestMethod.REQUIRED_FIELDS;
import static org.codice.ddf.admin.sources.impl.test.SourceNameExistsTestMethod.SOURCE_NAME_EXISTS_SUCCESS;
import static org.codice.ddf.admin.sources.impl.test.SourceNameExistsTestMethod.SOURCE_NAME_EXISTS_TEST_ID;
import static org.codice.ddf.admin.sources.impl.test.SourceNameExistsTestMethod.SUCCESS_TYPES;

import java.util.List;

import org.codice.ddf.admin.api.config.sources.SourceConfiguration;
import org.codice.ddf.admin.api.configurator.Configurator;
import org.codice.ddf.admin.api.handler.ConfigurationMessage;
import org.codice.ddf.admin.api.handler.method.TestMethod;
import org.codice.ddf.admin.api.handler.report.Report;
import org.codice.ddf.admin.api.validation.SourceValidationUtils;
import org.codice.ddf.admin.sources.opensearch.test.SourceNameExistsOpenSearchTestMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SourceNameExistsCswTestMethod extends TestMethod<SourceConfiguration> {

    private static final Logger LOGGER = LoggerFactory.getLogger(
            SourceNameExistsOpenSearchTestMethod.class);

    public static final String CSW_SOURCE_EXISTS_ID = SOURCE_NAME_EXISTS_TEST_ID;

    private final Configurator configurator;

    private final SourceValidationUtils sourceValidationUtils;

    public SourceNameExistsCswTestMethod(Configurator configurator) {
        super(CSW_SOURCE_EXISTS_ID,
                DESCRIPTION,
                REQUIRED_FIELDS,
                null,
                SUCCESS_TYPES,
                FAILURE_TYPES,
                null);

        sourceValidationUtils = new SourceValidationUtils();

        if (configurator == null) {
            this.configurator = new Configurator();
        } else {
            this.configurator = configurator;
        }
    }

    @Override
    public Report test(SourceConfiguration configuration) {
        List<ConfigurationMessage> results =
                sourceValidationUtils.validateSourceName(configuration.sourceName(),
                        CSW_FACTORY_PIDS,
                        configurator);

        if (results.isEmpty()) {
            return new Report(buildMessage(SUCCESS_TYPES,
                    FAILURE_TYPES,
                    null,
                    SOURCE_NAME_EXISTS_SUCCESS));
        }

        Report report = new Report();
        results.forEach(report::addMessage);
        return report;
    }
}
