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
package org.codice.ddf.admin.sources.wfs.persist;

import static org.codice.ddf.admin.api.config.sources.SourceConfiguration.ENDPOINT_URL;
import static org.codice.ddf.admin.api.config.sources.SourceConfiguration.FACTORY_PID;
import static org.codice.ddf.admin.api.config.sources.SourceConfiguration.SOURCE_NAME;
import static org.codice.ddf.admin.api.config.sources.SourceConfiguration.SOURCE_USERNAME;
import static org.codice.ddf.admin.api.config.sources.SourceConfiguration.SOURCE_USER_PASSWORD;
import static org.codice.ddf.admin.api.handler.commons.HandlerCommons.CREATE;
import static org.codice.ddf.admin.api.handler.commons.HandlerCommons.FAILED_CREATE;
import static org.codice.ddf.admin.api.handler.commons.HandlerCommons.SUCCESSFUL_CREATE;
import static org.codice.ddf.admin.api.handler.report.Report.createReport;
import static org.codice.ddf.admin.api.services.WfsServiceProperties.wfsConfigToServiceProps;
import static org.codice.ddf.admin.commons.sources.SourceHandlerCommons.SOURCE_NAME_EXISTS_TEST_ID;
import static org.codice.ddf.admin.commons.sources.SourceHandlerCommons.getCommonSourceSubtypeDescriptions;

import java.util.List;
import java.util.Map;

import org.codice.ddf.admin.api.config.sources.WfsSourceConfiguration;
import org.codice.ddf.admin.api.handler.ConfigurationHandler;
import org.codice.ddf.admin.api.handler.ConfigurationMessage;
import org.codice.ddf.admin.api.handler.method.PersistMethod;
import org.codice.ddf.admin.api.handler.report.Report;
import org.codice.ddf.admin.api.validation.SourceValidationUtils;
import org.codice.ddf.admin.configurator.Configurator;
import org.codice.ddf.admin.configurator.ConfiguratorFactory;
import org.codice.ddf.admin.configurator.OperationReport;

import com.google.common.collect.ImmutableList;

public class CreateWfsSourcePersistMethod extends PersistMethod<WfsSourceConfiguration> {

    public static final String CREATE_WFS_SOURCE_ID = CREATE;

    public static final String DESCRIPTION =
            "Attempts to create and persist a WFS source given a configuration.";

    private static final List<String> REQUIRED_FIELDS = ImmutableList.of(SOURCE_NAME,
            ENDPOINT_URL,
            FACTORY_PID);

    private static final List<String> OPTIONAL_FIELDS = ImmutableList.of(SOURCE_USERNAME,
            SOURCE_USER_PASSWORD);

    private static final Map<String, String> SUCCESS_TYPES = getCommonSourceSubtypeDescriptions(
            SUCCESSFUL_CREATE);

    private static final Map<String, String> FAILURE_TYPES = getCommonSourceSubtypeDescriptions(
            FAILED_CREATE);

    private final SourceValidationUtils sourceValidationUtils;

    private final ConfigurationHandler handler;

    private final ConfiguratorFactory configuratorFactory;

    public CreateWfsSourcePersistMethod(ConfigurationHandler handler,
            ConfiguratorFactory configuratorFactory) {
        super(CREATE_WFS_SOURCE_ID,
                DESCRIPTION,
                REQUIRED_FIELDS,
                OPTIONAL_FIELDS,
                SUCCESS_TYPES,
                FAILURE_TYPES,
                null);

        this.handler = handler;
        this.configuratorFactory = configuratorFactory;
        sourceValidationUtils = new SourceValidationUtils();
    }

    @Override
    public Report persist(WfsSourceConfiguration configuration) {
        Configurator configurator = configuratorFactory.getConfigurator();
        configurator.createManagedService(configuration.factoryPid(),
                wfsConfigToServiceProps(configuration));
        OperationReport report = configurator.commit("WFS source saved with details: {}",
                configuration.toString());
        return createReport(SUCCESS_TYPES,
                FAILURE_TYPES,
                null,
                report.containsFailedResults() ? FAILED_CREATE : SUCCESSFUL_CREATE);
    }

    @Override
    public List<ConfigurationMessage> validateOptionalFields(WfsSourceConfiguration configuration) {
        return sourceValidationUtils.validateOptionalUsernameAndPassword(configuration);
    }

    @Override
    public List<ConfigurationMessage> validateRequiredFields(WfsSourceConfiguration configuration) {
        Report report = handler.test(SOURCE_NAME_EXISTS_TEST_ID, configuration);
        report.addMessages(super.validateRequiredFields(configuration));
        return report.messages();
    }
}
