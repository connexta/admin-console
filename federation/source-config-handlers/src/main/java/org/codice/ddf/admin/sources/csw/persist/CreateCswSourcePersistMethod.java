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
package org.codice.ddf.admin.sources.csw.persist;

import static org.codice.ddf.admin.api.config.Configuration.FACTORY_PID;
import static org.codice.ddf.admin.api.config.sources.CswSourceConfiguration.FORCE_SPATIAL_FILTER;
import static org.codice.ddf.admin.api.config.sources.CswSourceConfiguration.OUTPUT_SCHEMA;
import static org.codice.ddf.admin.api.config.sources.SourceConfiguration.ENDPOINT_URL;
import static org.codice.ddf.admin.api.config.sources.SourceConfiguration.SOURCE_NAME;
import static org.codice.ddf.admin.api.config.sources.SourceConfiguration.SOURCE_USERNAME;
import static org.codice.ddf.admin.api.config.sources.SourceConfiguration.SOURCE_USER_PASSWORD;
import static org.codice.ddf.admin.api.handler.commons.HandlerCommons.CREATE;
import static org.codice.ddf.admin.api.handler.commons.HandlerCommons.FAILED_CREATE;
import static org.codice.ddf.admin.api.handler.commons.HandlerCommons.SUCCESSFUL_CREATE;
import static org.codice.ddf.admin.api.services.CswServiceProperties.CSW_FEATURE;
import static org.codice.ddf.admin.api.services.CswServiceProperties.cswConfigToServiceProps;
import static org.codice.ddf.admin.commons.sources.SourceHandlerCommons.SOURCE_NAME_EXISTS_TEST_ID;
import static org.codice.ddf.admin.commons.sources.SourceHandlerCommons.getCommonSourceSubtypeDescriptions;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.codice.ddf.admin.api.config.sources.CswSourceConfiguration;
import org.codice.ddf.admin.api.handler.ConfigurationHandler;
import org.codice.ddf.admin.api.handler.ConfigurationMessage;
import org.codice.ddf.admin.api.handler.method.PersistMethod;
import org.codice.ddf.admin.api.handler.report.Report;
import org.codice.ddf.admin.api.validation.SourceValidationUtils;
import org.codice.ddf.admin.configurator.Configurator;
import org.codice.ddf.admin.configurator.ConfiguratorFactory;
import org.codice.ddf.admin.configurator.OperationReport;

import com.google.common.collect.ImmutableList;

public class CreateCswSourcePersistMethod extends PersistMethod<CswSourceConfiguration> {

    public static final String CREATE_CSW_SOURCE_ID = CREATE;

    public static final String DESCRIPTION =
            "Attempts to create and persist a CSW source given a configuration.";

    public static final List<String> REQUIRED_FIELDS = ImmutableList.of(SOURCE_NAME,
            ENDPOINT_URL,
            FACTORY_PID);

    private static final List<String> OPTIONAL_FIELDS = ImmutableList.of(SOURCE_USERNAME,
            SOURCE_USER_PASSWORD,
            OUTPUT_SCHEMA,
            FORCE_SPATIAL_FILTER);

    private static final Map<String, String> SUCCESS_TYPES = getCommonSourceSubtypeDescriptions(
            SUCCESSFUL_CREATE);

    private static final Map<String, String> FAILURE_TYPES = getCommonSourceSubtypeDescriptions(
            FAILED_CREATE);

    private final SourceValidationUtils sourceValidationUtils;

    private final ConfigurationHandler handler;

    private final ConfiguratorFactory configuratorFactory;

    public CreateCswSourcePersistMethod(ConfigurationHandler handler,
            ConfiguratorFactory configuratorFactory) {
        super(CREATE_CSW_SOURCE_ID,
                DESCRIPTION,
                REQUIRED_FIELDS,
                OPTIONAL_FIELDS,
                SUCCESS_TYPES,
                FAILURE_TYPES,
                null);
        this.configuratorFactory = configuratorFactory;

        sourceValidationUtils = new SourceValidationUtils();
        this.handler = handler;
    }

    @Override
    public Report persist(CswSourceConfiguration configuration) {
        Configurator configurator = configuratorFactory.getConfigurator();
        configurator.startFeature(CSW_FEATURE);
        configurator.createManagedService(configuration.factoryPid(),
                cswConfigToServiceProps(configuration));
        OperationReport report = configurator.commit("CSW source saved with details: {}",
                configuration.toString());
        return Report.createReport(SUCCESS_TYPES,
                FAILURE_TYPES,
                null,
                report.containsFailedResults() ? FAILED_CREATE : SUCCESSFUL_CREATE);
    }

    @Override
    public List<ConfigurationMessage> validateOptionalFields(CswSourceConfiguration configuration) {
        List<ConfigurationMessage> validationResults =
                sourceValidationUtils.validateOptionalUsernameAndPassword(configuration);
        if (configuration.outputSchema() != null) {
            validationResults.addAll(configuration.validate(Collections.singletonList(OUTPUT_SCHEMA)));
        }
        if (configuration.forceSpatialFilter() != null) {
            validationResults.addAll(configuration.validate(Collections.singletonList(
                    FORCE_SPATIAL_FILTER)));
        }
        return validationResults;
    }

    @Override
    public List<ConfigurationMessage> validateRequiredFields(CswSourceConfiguration configuration) {
        Report report = handler.test(SOURCE_NAME_EXISTS_TEST_ID, configuration);
        report.addMessages(super.validateRequiredFields(configuration));
        return report.messages();
    }
}
