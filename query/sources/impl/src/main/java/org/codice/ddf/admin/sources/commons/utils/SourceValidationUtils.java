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

import static org.codice.ddf.admin.common.message.DefaultMessages.failedUpdateError;
import static org.codice.ddf.admin.sources.commons.SourceMessages.duplicateSourceNameError;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.codice.ddf.admin.api.action.Message;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;
import org.codice.ddf.admin.configurator.ConfigReader;
import org.codice.ddf.admin.configurator.ConfiguratorFactory;
import org.codice.ddf.admin.sources.fields.ServicePid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ddf.catalog.source.FederatedSource;

public class SourceValidationUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(SourceValidationUtils.class);

    /**
     * Gets the config from the given servicePid and checks that the config is present, and also that
     * no other configs have the same name as any existing sources.
     *
     * @param servicePid servicePid of configuration to check
     * @param sourceName source name to check against existing source names
     * @param configuratorFactory configurator factory for persisting
     * @return an empty {@code List} on success, or a {@code List} containing {@link org.codice.ddf.admin.common.message.ErrorMessage}s
     * on failure
     */
    public static List<Message> validUpdateConfig(ServicePid servicePid, StringField sourceName,
            ConfiguratorFactory configuratorFactory) {
        ConfigReader configReader = configuratorFactory.getConfigReader();
        Map<String, Object> existingConfig = configReader.getConfig(servicePid.getValue());
        List<Message> validationMsgs = new ArrayList<>();

        if (MapUtils.isNotEmpty(existingConfig)) {
            if (existingConfig.get("id") != null && !existingConfig.get("id")
                    .equals(sourceName.getValue())) {
                validationMsgs.addAll(validateSourceName(sourceName, configuratorFactory));
            }
        } else {
            validationMsgs.add(failedUpdateError(servicePid.path()));
        }

        return validationMsgs;
    }

    /**
     * Validates the {@code sourceName} against the existing source names in the system. An empty {@link List} will be returned
     * if there are no existing source names with with name {@code sourceName}, or a {@code List} containing
     * {@link Message}s if there are errors.
     *
     * @param sourceName source name to validate
     * @param configuratorFactory configurator factory for reading FederatedSource service references
     * @return a {@code List} of {@link Message}s containing a {@link org.codice.ddf.admin.sources.commons.SourceMessages#DUPLICATE_SOURCE_NAME} error, or an empty {@link List}
     * if there are no duplicate source names found
     */
    // TODO: 4/24/17 phuffer -  adding a duplicate name should be valid as long as the Active Binding is different
    public static List<Message> validateSourceName(StringField sourceName,
            ConfiguratorFactory configuratorFactory) {
        boolean matchFound = false;

        Collection<FederatedSource> sources = configuratorFactory.getConfigReader().getServices(FederatedSource.class, null);

        for (FederatedSource source : sources) {
            if (source.getId()
                    .equals(sourceName.getValue())) {
                matchFound = true;
                break;
            }
        }

        List<Message> errors = new ArrayList<>();
        if (matchFound) {
            errors.add(duplicateSourceNameError(sourceName.path()));
        }

        return errors;
    }
}
