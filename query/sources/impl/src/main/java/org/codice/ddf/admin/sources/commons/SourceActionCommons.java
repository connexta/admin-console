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
package org.codice.ddf.admin.sources.commons;

import static org.codice.ddf.admin.common.message.DefaultMessages.failedDeleteError;
import static org.codice.ddf.admin.common.message.DefaultMessages.failedPersistError;
import static org.codice.ddf.admin.common.message.DefaultMessages.failedUpdateError;
import static org.codice.ddf.admin.sources.commons.SourceMessages.noConfigFoundError;
import static org.codice.ddf.admin.sources.commons.utils.SourceValidationUtils.validUpdateConfig;
import static org.codice.ddf.admin.sources.commons.utils.SourceValidationUtils.validateSourceName;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.codice.ddf.admin.api.action.Message;
import org.codice.ddf.admin.configurator.Configurator;
import org.codice.ddf.admin.configurator.ConfiguratorFactory;
import org.codice.ddf.admin.configurator.OperationReport;
import org.codice.ddf.admin.sources.fields.ServicePid;
import org.codice.ddf.admin.sources.fields.SourceInfoField;
import org.codice.ddf.admin.sources.fields.type.SourceConfigUnionField;

public class SourceActionCommons {

    public static SourceInfoField createSourceInfoField(String sourceHandlerName,
            boolean isAvailable, SourceConfigUnionField config) {
        SourceInfoField sourceInfoField = new SourceInfoField();
        sourceInfoField.sourceHandlerName(sourceHandlerName);
        sourceInfoField.isAvaliable(isAvailable);
        sourceInfoField.configuration(config);
        return sourceInfoField;
    }

    public static List<Message> persist(SourceConfigUnionField config, ConfiguratorFactory configuratorFactory,
            Map<String, Object> serviceProps) {
        List<Message> validationMsgs = validateSourceName(config.sourceNameField(), configuratorFactory);
        if (CollectionUtils.isNotEmpty(validationMsgs)) {
            return validationMsgs;
        }

        Configurator configurator = configuratorFactory.getConfigurator();
        configurator.createManagedService(config.factoryPid(), serviceProps);
        OperationReport report = configurator.commit("Source saved with details: {}",
                config.toString());

        if (report.containsFailedResults()) {
            validationMsgs.add(failedPersistError(config.path()));
        }

        return validationMsgs;
    }

    public static List<Message> updateConfig(ServicePid servicePid, SourceConfigUnionField config,
            ConfiguratorFactory configuratorFactory, Map<String, Object> newConfig) {
        List<Message> validationMsgs = validUpdateConfig(servicePid,
                config.sourceNameField(),
                configuratorFactory);

        if (CollectionUtils.isNotEmpty(validationMsgs)) {
            return validationMsgs;
        }

        Configurator configurator = configuratorFactory.getConfigurator();
        configurator.updateConfigFile(servicePid.getValue(), newConfig, true);
        OperationReport report = configurator.commit("Updated config with pid [{}].",
                servicePid.getValue());

        if(report.containsFailedResults()) {
            validationMsgs.add(failedUpdateError(config.path()));
        }

        return validationMsgs;
    }

    public static List<Message> deleteConfig(ServicePid servicePid,
            ConfiguratorFactory configuratorFactory, Map<String, Object> configToDelete) {
        List<Message> errors = new ArrayList<>();

        if (configToDelete.isEmpty()) {
            errors.add(noConfigFoundError(servicePid.path()));
            return errors;
        }

        Configurator configurator = configuratorFactory.getConfigurator();
        configurator.deleteManagedService(servicePid.getValue());
        OperationReport report = configurator.commit("Deleted source with pid [{}].",
                servicePid.getValue());

        if (report.containsFailedResults()) {
            errors.add(failedDeleteError(servicePid.path()));
            return errors;
        }

        return errors;
    }
}
