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
package org.codice.ddf.admin.sources.utils;

import java.util.List;

import org.codice.ddf.admin.api.report.Report;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;
import org.codice.ddf.admin.common.fields.common.PidField;
import org.codice.ddf.admin.common.report.ReportImpl;
import org.codice.ddf.admin.common.services.ServiceCommons;
import org.codice.ddf.admin.configurator.ConfiguratorFactory;
import org.codice.ddf.admin.sources.SourceMessages;
import org.codice.ddf.internal.admin.configurator.actions.ManagedServiceActions;
import org.codice.ddf.internal.admin.configurator.actions.ServiceActions;
import org.codice.ddf.internal.admin.configurator.actions.ServiceReader;

import ddf.catalog.service.ConfiguredService;
import ddf.catalog.source.Source;
import ddf.catalog.util.Describable;

public class SourceValidationUtils {

    private ConfiguratorFactory configuratorFactory;

    private ManagedServiceActions managedServiceActions;

    private ServiceActions serviceActions;

    private ServiceReader serviceReader;

    private SourceUtilCommons sourceUtilCommons;

    private ServiceCommons serviceCommons;

    public SourceValidationUtils(ServiceActions serviceActions) {
        this(null, null, null, serviceActions);
    }

    public SourceValidationUtils(ServiceReader serviceReader,
            ManagedServiceActions managedServiceActions, ConfiguratorFactory configuratorFactory,
            ServiceActions serviceActions) {
        this.serviceReader = serviceReader;
        this.managedServiceActions = managedServiceActions;
        this.configuratorFactory = configuratorFactory;
        this.serviceActions = serviceActions;

        sourceUtilCommons = new SourceUtilCommons(managedServiceActions,
                serviceActions,
                serviceReader,
                configuratorFactory);

        serviceCommons = new ServiceCommons(managedServiceActions,
                serviceActions,
                serviceReader,
                configuratorFactory);
    }

    /**
     * Determines whether the service configuration identified by the service pid has the same name as sourceName.
     * If the service configuration's id equals the sourceName return {@code true}, otherwise {@code false}.
     *
     * @param servicePid service pid of the service configuration
     * @param sourceName source name to check against existing source name
     * @return {@code true} if the sourceName matches the existing configuration's id, {@code false} otherwise
     */
    public boolean hasSourceName(String servicePid, String sourceName) {
        Source source = sourceUtilCommons.getAllSourceReferences()
                .stream()
                .map(ConfiguredService.class::cast)
                .filter(configuredService -> configuredService.getConfigurationPid()
                        .equals(servicePid))
                .findFirst()
                .map(Source.class::cast)
                .orElse(null);

        return source == null || source.getId()
                .equals(sourceName);
    }

    /**
     * Validates the {@code sourceName} against the existing source names in the system. An empty {@link ReportImpl} will be returned
     * if there are no existing source names with with name {@code sourceName}, or a {@link ReportImpl} with error messages.
     *
     * @param sourceName source name to validate
     * @return a {@link ReportImpl} containing a {@link org.codice.ddf.admin.sources.SourceMessages#DUPLICATE_SOURCE_NAME}
     * error, or a Report with
     * no messages on success.
     */
    public ReportImpl sourceNameExists(StringField sourceName) {
        List<Source> sources = sourceUtilCommons.getAllSourceReferences();
        boolean matchFound = sources.stream()
                .map(Describable::getId)
                .anyMatch(id -> id.equals(sourceName.getValue()));

        ReportImpl report = new ReportImpl();
        if (matchFound) {
            report.addArgumentMessage(SourceMessages.duplicateSourceNameError(sourceName.path()));
        }
        return report;
    }

    /**
     * @param sourceName
     * @param pid
     * @return
     */
    public Report validateSourceName(StringField sourceName, PidField pid) {
        ReportImpl sourceNameReport = new ReportImpl();
        if (pid.getValue() != null) {
            sourceNameReport = serviceCommons.serviceConfigurationExists(pid);
            if (!sourceNameReport.containsErrorMsgs() && !hasSourceName(pid.getValue(),
                    sourceName.getValue())) {
                sourceNameReport.addMessages(sourceNameExists(sourceName));
            }
        } else {
            sourceNameReport.addMessages(sourceNameExists(sourceName));
        }
        return sourceNameReport;
    }

    public void setManagedServiceActions(ManagedServiceActions managedServiceActions) {
        this.managedServiceActions = managedServiceActions;
    }

    public void setConfiguratorFactory(ConfiguratorFactory configuratorFactory) {
        this.configuratorFactory = configuratorFactory;
    }

    public void setServiceActions(ServiceActions serviceActions) {
        this.serviceActions = serviceActions;
    }

    public void setServiceReader(ServiceReader serviceReader) {
        this.serviceReader = serviceReader;
    }
}
