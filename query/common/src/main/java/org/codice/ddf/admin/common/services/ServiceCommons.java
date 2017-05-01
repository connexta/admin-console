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

import static org.codice.ddf.admin.common.message.DefaultMessages.failedUpdateError;
import static org.codice.ddf.admin.common.message.DefaultMessages.noExistingConfigError;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.text.StrSubstitutor;
import org.codice.ddf.admin.api.action.Message;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;
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

    public static boolean persist(Map<String, Object> serviceProps, String factoryPid,
            ConfiguratorFactory configuratorFactory) {
        Configurator configurator = configuratorFactory.getConfigurator();
        configurator.createManagedService(factoryPid, serviceProps);
        return !configurator.commit("Service saved with details [{}]", serviceProps.toString())
                .containsFailedResults();
    }

    public static List<Message> update(StringField servicePidField, Map<String, Object> newConfig,
            ConfiguratorFactory configuratorFactory) {
        String servicePid = servicePidField.getValue();
        Map<String, Object> existingConfig = configuratorFactory.getConfigReader()
                .getConfig(servicePid);

        if (existingConfig != null && existingConfig.isEmpty()) {
            return Collections.singletonList(noExistingConfigError(servicePidField.path()));
        }

        Configurator configurator = configuratorFactory.getConfigurator();
        configurator.updateConfigFile(servicePid, newConfig, true);
        OperationReport report = configurator.commit(
                "Updated config with pid [{}] and new service properties [{}]",
                servicePid,
                newConfig.toString());
        if (report.containsFailedResults()) {
            return Collections.singletonList(failedUpdateError(servicePidField.path()));
        }

        return Collections.emptyList();
    }

    public static boolean delete(String servicePid, ConfiguratorFactory configuratorFactory) {
        Configurator configurator = configuratorFactory.getConfigurator();
        configurator.deleteManagedService(servicePid);
        return !configurator.commit("Deleted source with pid [{}].", servicePid)
                .containsFailedResults();
    }

    public static boolean configExists(String servicePid, ConfiguratorFactory configuratorFactory) {
        return !configuratorFactory.getConfigReader()
                .getConfig(servicePid)
                .isEmpty();
    }
}
