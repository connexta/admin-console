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
package org.codice.ddf.admin.sources.commons.utils;

import static org.codice.ddf.admin.sources.commons.SourceMessages.duplicateSourceNameError;
import static org.codice.ddf.admin.sources.commons.utils.SourceUtilCommons.getAllSourceReferences;

import java.util.List;

import org.codice.ddf.admin.common.fields.base.scalar.StringField;
import org.codice.ddf.admin.common.report.ReportImpl;
import org.codice.ddf.admin.configurator.ConfiguratorFactory;

import ddf.catalog.service.ConfiguredService;
import ddf.catalog.source.Source;

public class SourceValidationUtils {

    /**
     * Determines whether the service configuration identified by the service pid has the same name as sourceName.
     * If the service configuration's id equals the sourceName return {@code true}, otherwise {@code false}.
     *
     * @param servicePid service pid of the service configuration
     * @param sourceName source name to check against existing source name
     * @param configuratorFactory configurator factory to get services
     * @return {@code true} if the sourceName matches the existing configuration's id, {@code false} otherwise
     */
    public static boolean hasSourceName(String servicePid, String sourceName, ConfiguratorFactory configuratorFactory) {
        Source source = getAllSourceReferences(configuratorFactory).stream()
                .map(ConfiguredService.class::cast)
                .filter(configuredService -> configuredService.getConfigurationPid()
                        .equals(servicePid))
                .findFirst()
                .map(Source.class::cast)
                .orElseGet(() -> null);

        return source == null || source.getId().equals(sourceName);
    }

    /**
     * Validates the {@code sourceName} against the existing source names in the system. An empty {@link ReportImpl} will be returned
     * if there are no existing source names with with name {@code sourceName}, or a {@link ReportImpl} with error messages.
     *
     * @param sourceName          source name to validate
     * @param configuratorFactory configurator factory for reading FederatedSource service references
     * @return a {@link ReportImpl} containing a {@link org.codice.ddf.admin.sources.commons.SourceMessages#DUPLICATE_SOURCE_NAME} error, or a Report with
     * no messages on success.
     */
    public static ReportImpl validateSourceName(StringField sourceName,
            ConfiguratorFactory configuratorFactory) {
        List<Source> sources = getAllSourceReferences(configuratorFactory);
        boolean matchFound = sources.stream()
                .map(source -> source.getId())
                .anyMatch(id -> id.equals(sourceName.getValue()));

        ReportImpl report = new ReportImpl();
        if (matchFound) {
            report.addArgumentMessage(duplicateSourceNameError(sourceName.path()));
        }
        return report;
    }
}
