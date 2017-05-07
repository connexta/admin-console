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

import static org.codice.ddf.admin.sources.commons.SourceActionCommons.getAllSourceReferences;
import static org.codice.ddf.admin.sources.commons.SourceMessages.duplicateSourceNameError;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.codice.ddf.admin.common.Report;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;
import org.codice.ddf.admin.common.fields.common.PidField;
import org.codice.ddf.admin.configurator.ConfigReader;
import org.codice.ddf.admin.configurator.ConfiguratorFactory;

import ddf.catalog.service.ConfiguredService;
import ddf.catalog.source.Source;

public class SourceValidationUtils {

    /**
     * Validates the {@code sourceName} against the existing source names in the system. An empty {@link Report} will be returned
     * if there are no existing source names with with name {@code sourceName}, or a {@link Report} with error messages.
     *
     * @param sourceName          source name to validate
     * @param configuratorFactory configurator factory for reading FederatedSource service references
     * @param servicePid          if provided, signifies this is an update on the configuration identified by the pid and if the sourceName
     *                            matches the servicePid's configuration's sourceName, it is a valid sourceName
     * @return a {@link Report} containing a {@link org.codice.ddf.admin.sources.commons.SourceMessages#DUPLICATE_SOURCE_NAME} error, or a Report with
     * no messages on success.
     */
    // TODO: 4/24/17 phuffer -  adding a duplicate name should be valid as long as the Active Binding is different
    public static Report validateSourceName(StringField sourceName,
            ConfiguratorFactory configuratorFactory, PidField servicePid) {
        ConfigReader configReader = configuratorFactory.getConfigReader();
        List<Source> sources = getAllSourceReferences(configuratorFactory);
        Report report = new Report();

        if(servicePid != null && servicePid.getValue() != null) {
            Map<String, Object> existingConfig = configReader.getConfig(servicePid.getValue());
            if (existingConfig.get("id") != null && existingConfig.get("id")
                    .equals(sourceName.getValue())) {
                return report;
            }
        }

        boolean matchFound = sources.stream()
                .map(source -> source.getId())
                .anyMatch(id -> id.equals(sourceName.getValue()));

        if (matchFound) {
            return report.argumentMessage(duplicateSourceNameError(sourceName.path()));
        }

        // Try to find the name through the config id if the service reference failed. This is a work around
        // for OpenSearch sources (but will work for any source that's a ConfiguredService) always returning
        // the default name from the getId() method.
        matchFound = sources.stream()
                .map(source -> {
                    if (source instanceof ConfiguredService) {
                        return ((ConfiguredService) source).getConfigurationPid();
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .map(configReader::getConfig)
                .map(config -> config.get("id"))
                .anyMatch(id -> id.equals(sourceName.getValue()));

        if (matchFound) {
            return report.argumentMessage(duplicateSourceNameError(sourceName.path()));
        }

        return report;
    }
}
