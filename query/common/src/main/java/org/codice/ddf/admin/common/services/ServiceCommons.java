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

import static org.codice.ddf.admin.common.report.message.DefaultMessages.failedPersistError;
import static org.codice.ddf.admin.common.report.message.DefaultMessages.noExistingConfigError;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.text.StrSubstitutor;
import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.common.fields.common.PidField;
import org.codice.ddf.admin.common.report.ReportImpl;
import org.codice.ddf.admin.configurator.Configurator;
import org.codice.ddf.admin.configurator.ConfiguratorFactory;
import org.codice.ddf.admin.configurator.OperationReport;
import org.codice.ddf.internal.admin.configurator.actions.ManagedServiceActions;
import org.codice.ddf.internal.admin.configurator.actions.ServiceActions;
import org.codice.ddf.internal.admin.configurator.actions.ServiceReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: 6/21/17 phuffer - Make this back into static methods
public class ServiceCommons {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceCommons.class);

    public static final String SERVICE_PID_KEY = "service.pid";

    public static final String FACTORY_PID_KEY = "service.factoryPid";

    // A flag to indicate if a service being updated has a password of "secret". If so, the
    // password will not be updated.
    public static final String FLAG_PASSWORD = "secret";

    private ManagedServiceActions managedServiceActions;

    private ServiceActions serviceActions;

    private ServiceReader serviceReader;

    private ConfiguratorFactory configuratorFactory;

    public ServiceCommons() {
    }

    public ServiceCommons(ManagedServiceActions managedServiceActions,
            ServiceActions serviceActions, ServiceReader serviceReader,
            ConfiguratorFactory configuratorFactory) {
        this.managedServiceActions = managedServiceActions;
        this.serviceActions = serviceActions;
        this.serviceReader = serviceReader;
        this.configuratorFactory = configuratorFactory;
    }

    public String resolveProperty(String str) {
        return StrSubstitutor.replaceSystemProperties(str);
    }

    public List<String> resolveProperties(String... list) {
        return Arrays.stream(list)
                .map(this::resolveProperty)
                .collect(Collectors.toList());
    }

    public ReportImpl createManagedService(Map<String, Object> serviceProps, String factoryPid) {
        ReportImpl report = new ReportImpl();

        if (configuratorFactory != null && managedServiceActions != null) {
            Configurator configurator = configuratorFactory.getConfigurator();
            configurator.add(managedServiceActions.create(factoryPid, serviceProps));

            if (configurator.commit("Service saved with details [{}]", serviceProps.toString())
                    .containsFailedResults()) {
                report.addResultMessage(failedPersistError());
            }
        } else {
            LOGGER.debug(
                    "Unable to create managed service due to missing configuratorFactory or managedServiceActions.");
            report.addResultMessage(failedPersistError());
        }

        return report;
    }

    public ReportImpl updateService(PidField servicePid, Map<String, Object> newConfig) {
        ReportImpl report = new ReportImpl();

        if (configuratorFactory == null || serviceActions == null) {
            LOGGER.debug(
                    "Unable to update service due to missing configuratorFactory or serviceActions.");
            report.addResultMessage(failedPersistError());
        } else {
            report.addMessages(serviceConfigurationExists(servicePid));
            if (report.containsErrorMsgs()) {
                return report;
            }

            String pid = servicePid.getValue();
            Configurator configurator = configuratorFactory.getConfigurator();
            configurator.add(serviceActions.build(pid, newConfig, true));
            OperationReport operationReport = configurator.commit(
                    "Updated config with pid [{}] and new service properties [{}]",
                    pid,
                    newConfig.toString());
            if (operationReport.containsFailedResults()) {
                report.addResultMessage(failedPersistError());
            }
        }

        return report;
    }

    public ReportImpl deleteService(PidField servicePid) {
        ReportImpl report = new ReportImpl();

        if (configuratorFactory != null && managedServiceActions != null) {
            Configurator configurator = configuratorFactory.getConfigurator();
            configurator.add(managedServiceActions.delete(servicePid.getValue()));
            if (configurator.commit("Deleted service with pid [{}].", servicePid.getValue())
                    .containsFailedResults()) {
                report.addResultMessage(failedPersistError());
            }
        } else {
            LOGGER.debug(
                    "Unable to delete service to missing configuratorFactory or managedServiceActions.");
            report.addResultMessage(failedPersistError());
        }
        return report;
    }

    /**
     * Determines whether the service identified by the {@code servicePid} exists.
     *
     * @param servicePid identifier of the service
     * @return
     */
    public ReportImpl serviceConfigurationExists(PidField servicePid) {
        ReportImpl report = new ReportImpl();
        if (!serviceConfigurationExists(servicePid.getValue())) {
            report.addResultMessage(noExistingConfigError());
        }
        return report;
    }

    /**
     * Checks if the given pid retrieves any properties. If no properties are found or the properties are empty then fail.
     *
     * @param servicePid
     * @return with the serviceExists or not
     */
    public boolean serviceConfigurationExists(String servicePid) {
        return serviceActions != null && !serviceActions.read(servicePid)
                .isEmpty();
    }

    public static <T> T mapValue(Map<String, Object> props, String key) {
        return props.get(key) == null ? null : (T) props.get(key);
    }

    public static class ServicePropertyBuilder {

        private Map<String, Object> serviceProperties;

        public ServicePropertyBuilder() {
            serviceProperties = new HashMap<>();
        }

        public ServicePropertyBuilder put(String key, Object object) {
            serviceProperties.put(key, object);
            return this;
        }

        public ServicePropertyBuilder putPropertyIfNotNull(String key, Field field) {
            if (field.getValue() != null) {
                put(key, field.getValue());
            }
            return this;
        }

        public Map<String, Object> build() {
            return serviceProperties;
        }

    }

    public void setManagedServiceActions(ManagedServiceActions managedServiceActions) {
        this.managedServiceActions = managedServiceActions;
    }

    public void setServiceActions(ServiceActions serviceActions) {
        this.serviceActions = serviceActions;
    }

    public void setServiceReader(ServiceReader serviceReader) {
        this.serviceReader = serviceReader;
    }

    public void setConfiguratorFactory(ConfiguratorFactory configuratorFactory) {
        this.configuratorFactory = configuratorFactory;
    }
}
