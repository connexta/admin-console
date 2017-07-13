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

import org.codice.ddf.admin.api.ConfiguratorSuite;
import org.codice.ddf.admin.api.report.Report;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;
import org.codice.ddf.admin.common.fields.common.PidField;
import org.codice.ddf.admin.common.report.ReportImpl;
import org.codice.ddf.admin.common.services.ServiceCommons;
import org.codice.ddf.admin.sources.SourceMessages;

import ddf.catalog.service.ConfiguredService;
import ddf.catalog.source.Source;
import ddf.catalog.util.Describable;

public class SourceValidationUtils {

    private SourceUtilCommons sourceUtilCommons;

    private ServiceCommons serviceCommons;

    public SourceValidationUtils(ConfiguratorSuite configuratorSuite) {
        sourceUtilCommons = new SourceUtilCommons(configuratorSuite);
        serviceCommons = new ServiceCommons(configuratorSuite);
    }

    private boolean findSourceNameMatch(String servicePid, String sourceName) {
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
     * Validates the {@code sourceName} against the existing source names in the system.
     *
     * @param sourceName source name to validate
     * @return a {@link Report} containing a {@link SourceMessages#DUPLICATE_SOURCE_NAME} on failure.
     */
    public Report duplicateSourceNameExists(StringField sourceName) {
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
     * Validates whether the existing service properties identified by the {@code pid}
     * has the {@code sourceName}.
     * <p>
     * Possible error codes
     * {@link org.codice.ddf.admin.common.report.message.DefaultMessages#NO_EXISTING_CONFIG}
     * {@link SourceMessages#DUPLICATE_SOURCE_NAME}
     *
     * @param sourceName source name to validate
     * @param pid        service pid of the service properties
     * @return a {@link Report} containing an {@link org.codice.ddf.admin.api.report.ErrorMessage}s on failure.
     */
    public Report duplicateSourceNameExists(StringField sourceName, PidField pid) {
        ReportImpl sourceNameReport = new ReportImpl();
        if (pid.getValue() != null) {
            sourceNameReport = serviceCommons.serviceConfigurationExists(pid);
            if (!sourceNameReport.containsErrorMsgs() && !findSourceNameMatch(pid.getValue(),
                    sourceName.getValue())) {
                sourceNameReport.addMessages(duplicateSourceNameExists(sourceName));
            }
        } else {
            sourceNameReport.addMessages(duplicateSourceNameExists(sourceName));
        }
        return sourceNameReport;
    }
}
