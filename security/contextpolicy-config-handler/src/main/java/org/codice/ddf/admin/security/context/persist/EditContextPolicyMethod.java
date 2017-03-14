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

package org.codice.ddf.admin.security.context.persist;

import static org.codice.ddf.admin.api.config.context.ContextPolicyConfiguration.CONTEXT_POLICY_BINS;
import static org.codice.ddf.admin.api.config.context.ContextPolicyConfiguration.WHITE_LIST_CONTEXTS;
import static org.codice.ddf.admin.api.handler.ConfigurationMessage.FAILED_PERSIST;
import static org.codice.ddf.admin.api.handler.commons.HandlerCommons.EDIT;
import static org.codice.ddf.admin.api.handler.commons.HandlerCommons.SUCCESSFUL_PERSIST;
import static org.codice.ddf.admin.api.handler.report.Report.createReport;
import static org.codice.ddf.admin.api.services.ContextPolicyServiceProperties.POLICY_MANAGER_PID;
import static org.codice.ddf.admin.api.services.ContextPolicyServiceProperties.configToPolicyManagerProps;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codice.ddf.admin.api.config.context.ContextPolicyBin;
import org.codice.ddf.admin.api.config.context.ContextPolicyConfiguration;
import org.codice.ddf.admin.api.handler.method.PersistMethod;
import org.codice.ddf.admin.api.handler.report.Report;
import org.codice.ddf.admin.configurator.Configurator;
import org.codice.ddf.admin.configurator.ConfiguratorFactory;
import org.codice.ddf.admin.configurator.OperationReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class EditContextPolicyMethod extends PersistMethod<ContextPolicyConfiguration> {
    private static final Logger LOGGER = LoggerFactory.getLogger(EditContextPolicyMethod.class);

    public static final String PERSIST_CONTEXT_POLICY_ID = EDIT;

    public static final String DESCRIPTION = "Persist changes to the Web Context Policy manager.";

    public static final List<String> REQUIRED_FIELDS = ImmutableList.of(CONTEXT_POLICY_BINS,
            WHITE_LIST_CONTEXTS);

    public static final Map<String, String> SUCCESS_TYPES = ImmutableMap.of(SUCCESSFUL_PERSIST,
            "Successfully saved Web Context Policy Manager settings");

    public static final Map<String, String> FAILURE_TYPES = ImmutableMap.of(FAILED_PERSIST,
            "Unable to persist changes");

    private static final String ROOT_CONTEXT_PATH = "/";

    private final ConfiguratorFactory configuratorFactory;

    public EditContextPolicyMethod(ConfiguratorFactory configuratorFactory) {
        super(PERSIST_CONTEXT_POLICY_ID,
                DESCRIPTION,
                REQUIRED_FIELDS,
                null,
                SUCCESS_TYPES,
                FAILURE_TYPES,
                null);
        this.configuratorFactory = configuratorFactory;
    }

    @Override
    public Report persist(ContextPolicyConfiguration config) {
        if (config.contextPolicyBins()
                .stream()
                .map(ContextPolicyBin::contextPaths)
                .flatMap(Set::stream)
                .noneMatch(ROOT_CONTEXT_PATH::equals)) {
            LOGGER.debug("Invalid attempt to delete configuration for root context path");
            return createReport(SUCCESS_TYPES, FAILURE_TYPES, null, FAILED_PERSIST);
        }


        Configurator configurator = configuratorFactory.getConfigurator();
        configurator.updateConfigFile(POLICY_MANAGER_PID,
                configToPolicyManagerProps(config),
                false);

        OperationReport configReport = configurator.commit(
                "Web Context Policy saved with details: {}",
                config.toString());

        return createReport(SUCCESS_TYPES,
                FAILURE_TYPES,
                null,
                configReport.containsFailedResults() ? FAILED_PERSIST : SUCCESSFUL_PERSIST);
    }

}
