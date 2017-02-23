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

import static org.codice.ddf.admin.api.config.sources.CswSourceConfiguration.FORCE_SPATIAL_FILTER;
import static org.codice.ddf.admin.api.config.sources.CswSourceConfiguration.OUTPUT_SCHEMA;
import static org.codice.ddf.admin.api.config.sources.SourceConfiguration.ENDPOINT_URL;
import static org.codice.ddf.admin.api.config.sources.SourceConfiguration.FACTORY_PID;
import static org.codice.ddf.admin.api.config.sources.SourceConfiguration.SOURCE_NAME;
import static org.codice.ddf.admin.api.config.sources.SourceConfiguration.SOURCE_USERNAME;
import static org.codice.ddf.admin.api.config.sources.SourceConfiguration.SOURCE_USER_PASSWORD;
import static org.codice.ddf.admin.api.handler.ConfigurationMessage.FAILED_PERSIST;
import static org.codice.ddf.admin.api.handler.commons.HandlerCommons.CREATE;
import static org.codice.ddf.admin.api.handler.commons.HandlerCommons.SUCCESSFUL_PERSIST;
import static org.codice.ddf.admin.api.handler.commons.SourceHandlerCommons.SOURCE_NAME_EXISTS_TEST_ID;
import static org.codice.ddf.admin.api.services.CswServiceProperties.cswConfigToServiceProps;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.codice.ddf.admin.api.config.sources.CswSourceConfiguration;
import org.codice.ddf.admin.api.configurator.Configurator;
import org.codice.ddf.admin.api.configurator.OperationReport;
import org.codice.ddf.admin.api.handler.ConfigurationHandler;
import org.codice.ddf.admin.api.handler.ConfigurationMessage;
import org.codice.ddf.admin.api.handler.method.PersistMethod;
import org.codice.ddf.admin.api.handler.report.Report;
import org.codice.ddf.admin.api.validation.SourceValidationUtils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

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

    private static final Map<String, String> SUCCESS_TYPES = ImmutableMap.of(SUCCESSFUL_PERSIST,
            "CSW Source successfully created.");

    private static final Map<String, String> FAILURE_TYPES = ImmutableMap.of(FAILED_PERSIST,
            "Failed to create CSW Source.");

    private final SourceValidationUtils sourceValidationUtils;

    private final ConfigurationHandler handler;

    public CreateCswSourcePersistMethod(ConfigurationHandler handler) {
        super(CREATE_CSW_SOURCE_ID,
                DESCRIPTION,
                REQUIRED_FIELDS,
                OPTIONAL_FIELDS,
                SUCCESS_TYPES,
                FAILURE_TYPES,
                null);

        sourceValidationUtils = new SourceValidationUtils();
        this.handler = handler;
    }

    @Override
    public Report persist(CswSourceConfiguration configuration) {
        Configurator configurator = new Configurator();
        configurator.createManagedService(configuration.factoryPid(),
                cswConfigToServiceProps(configuration));
        OperationReport report = configurator.commit("CSW source saved with details: {}",
                configuration.toString());
        return Report.createReport(SUCCESS_TYPES,
                FAILURE_TYPES,
                null,
                report.containsFailedResults() ? FAILED_PERSIST : SUCCESSFUL_PERSIST);
    }

    @Override
    public List<ConfigurationMessage> validateOptionalFields(CswSourceConfiguration configuration) {
        List<ConfigurationMessage> validationResults =
                sourceValidationUtils.validateOptionalUsernameAndPassword(configuration);
        if (configuration.outputSchema() != null) {
            validationResults.addAll(configuration.validate(Arrays.asList(OUTPUT_SCHEMA)));
        }
        if (configuration.forceSpatialFilter() != null) {
            validationResults.addAll(configuration.validate(Arrays.asList(FORCE_SPATIAL_FILTER)));
        }
        return validationResults;
    }

    @Override
    public List<ConfigurationMessage> validateRequiredFields(
            CswSourceConfiguration configuration) {
        Report report = handler.test(SOURCE_NAME_EXISTS_TEST_ID, configuration);
        report.addMessages(super.validateRequiredFields(configuration));
        return report.messages();
    }
}
