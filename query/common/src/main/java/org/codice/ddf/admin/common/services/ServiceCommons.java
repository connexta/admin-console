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
 **/
package org.codice.ddf.admin.common.services;

import static org.codice.ddf.admin.common.message.DefaultMessages.failedDeleteError;
import static org.codice.ddf.admin.common.message.DefaultMessages.failedPersistError;
import static org.codice.ddf.admin.common.message.DefaultMessages.failedUpdateError;
import static org.codice.ddf.admin.common.message.DefaultMessages.noExistingConfigError;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.text.StrSubstitutor;
import org.codice.ddf.admin.api.action.Message;
import org.codice.ddf.admin.common.Report;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;
import org.codice.ddf.admin.common.fields.common.ServicePid;
import org.codice.ddf.admin.configurator.Configurator;
import org.codice.ddf.admin.configurator.ConfiguratorFactory;
import org.codice.ddf.admin.configurator.OperationReport;

public class ServiceCommons {

    public static final String SERVICE_PID_KEY = "service.pid";

    public static final String FACTORY_PID_KEY = "service.factoryPid";

    public String resolveProperty(String str) {
        return StrSubstitutor.replaceSystemProperties(str);
    }

    public List<String> resolveProperties(String... list) {
        return Arrays.stream(list)
                .map(this::resolveProperty)
                .collect(Collectors.toList());
    }

    public static Report createManagedService(Map<String, Object> serviceProps, String factoryPid,
            ConfiguratorFactory configuratorFactory) {
        Report report = new Report();
        Configurator configurator = configuratorFactory.getConfigurator();

        configurator.createManagedService(factoryPid, serviceProps);
        if(configurator.commit("Service saved with details [{}]", serviceProps.toString())
                .containsFailedResults()) {
            report.argumentMessage(failedPersistError());
        }
        return report;
    }

    public static Report updateService(ServicePid servicePidField, Map<String, Object> newConfig,
            ConfiguratorFactory configuratorFactory) {
        Report report = new Report();
        String servicePid = servicePidField.getValue();
        if (!serviceConfigurationExists(servicePid, configuratorFactory)) {
            report.argumentMessage(noExistingConfigError(servicePidField.path()));
            return report;
        }

        Configurator configurator = configuratorFactory.getConfigurator();
        configurator.updateConfigFile(servicePid, newConfig, true);
        OperationReport operationReport = configurator.commit(
                "Updated config with pid [{}] and new service properties [{}]",
                servicePid,
                newConfig.toString());
        if (operationReport.containsFailedResults()) {
            return report.argumentMessage(failedUpdateError(servicePidField.path()));
        }

        return report;
    }

    public static Report deleteService(StringField servicePid, ConfiguratorFactory configuratorFactory) {
        Report report = new Report();
        Configurator configurator = configuratorFactory.getConfigurator();
        configurator.deleteManagedService(servicePid.getValue());
        if(configurator.commit("Deleted source with pid [{}].", servicePid.getValue())
                .containsFailedResults()) {
            report.argumentMessage(failedDeleteError(servicePid.path()));
        }
        return report;
    }

    public static boolean serviceConfigurationExists(String servicePid, ConfiguratorFactory configuratorFactory) {
        return !configuratorFactory.getConfigReader()
                .getConfig(servicePid)
                .isEmpty();
    }
}
