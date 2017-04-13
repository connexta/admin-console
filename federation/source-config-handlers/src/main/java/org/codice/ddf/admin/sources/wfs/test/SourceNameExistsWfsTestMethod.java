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
package org.codice.ddf.admin.sources.wfs.test;

import static org.codice.ddf.admin.api.handler.ConfigurationMessage.buildMessage;
import static org.codice.ddf.admin.api.handler.commons.HandlerCommons.SUCCESSFUL_TEST;
import static org.codice.ddf.admin.api.services.WfsServiceProperties.WFS_FACTORY_PIDS;
import static org.codice.ddf.admin.commons.sources.SourceHandlerCommons.SOURCE_NAME_EXISTS_TEST_ID;
import static org.codice.ddf.admin.sources.impl.test.SourceNameExistsTestMethod.DESCRIPTION;
import static org.codice.ddf.admin.sources.impl.test.SourceNameExistsTestMethod.FAILURE_TYPES;
import static org.codice.ddf.admin.sources.impl.test.SourceNameExistsTestMethod.REQUIRED_FIELDS;
import static org.codice.ddf.admin.sources.impl.test.SourceNameExistsTestMethod.SUCCESS_TYPES;

import java.util.List;

import org.codice.ddf.admin.api.config.sources.SourceConfiguration;
import org.codice.ddf.admin.api.handler.ConfigurationMessage;
import org.codice.ddf.admin.api.handler.method.TestMethod;
import org.codice.ddf.admin.api.handler.report.Report;
import org.codice.ddf.admin.api.validation.SourceValidationUtils;
import org.codice.ddf.admin.configurator.ConfiguratorFactory;

public class SourceNameExistsWfsTestMethod extends TestMethod<SourceConfiguration> {

    public static final String WFS_SOURCE_EXISTS_ID = SOURCE_NAME_EXISTS_TEST_ID;

    private final ConfiguratorFactory configuratorFactory;

    private final SourceValidationUtils sourceValidationUtils;

    public SourceNameExistsWfsTestMethod(ConfiguratorFactory configuratorFactory) {
        super(WFS_SOURCE_EXISTS_ID,
                DESCRIPTION,
                REQUIRED_FIELDS,
                null,
                SUCCESS_TYPES,
                FAILURE_TYPES,
                null);

        this.sourceValidationUtils = new SourceValidationUtils();

        this.configuratorFactory = configuratorFactory;
    }

    @Override
    public Report test(SourceConfiguration configuration) {
        List<ConfigurationMessage> results =
                sourceValidationUtils.validateSourceName(configuration.sourceName(),
                        WFS_FACTORY_PIDS,
                        configuratorFactory.getConfigReader());

        if (results.isEmpty()) {
            return new Report(buildMessage(SUCCESS_TYPES, FAILURE_TYPES, null, SUCCESSFUL_TEST));
        }

        return new Report(results);
    }
}
