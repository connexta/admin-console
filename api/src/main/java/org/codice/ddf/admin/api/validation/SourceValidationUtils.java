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
package org.codice.ddf.admin.api.validation;

import static org.codice.ddf.admin.api.config.sources.SourceConfiguration.SOURCE_NAME;
import static org.codice.ddf.admin.api.config.sources.SourceConfiguration.SOURCE_USERNAME;
import static org.codice.ddf.admin.api.config.sources.SourceConfiguration.SOURCE_USER_PASSWORD;
import static org.codice.ddf.admin.api.handler.ConfigurationMessage.buildMessage;
import static org.codice.ddf.admin.api.handler.ConfigurationMessage.createInvalidFieldMsg;
import static org.codice.ddf.admin.api.services.CswServiceProperties.CSW_FACTORY_PIDS;
import static org.codice.ddf.admin.api.services.OpenSearchServiceProperties.OPENSEARCH_FACTORY_PID;
import static org.codice.ddf.admin.api.services.WfsServiceProperties.WFS_FACTORY_PIDS;
import static org.codice.ddf.admin.api.validation.ValidationUtils.validateString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.apache.commons.lang.StringUtils;
import org.codice.ddf.admin.api.config.sources.SourceConfiguration;
import org.codice.ddf.admin.api.configurator.Configurator;
import org.codice.ddf.admin.api.handler.ConfigurationMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SourceValidationUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(SourceValidationUtils.class);

    public List<ConfigurationMessage> validateWfsFactoryPid(String factoryPid, String configId) {
        List<ConfigurationMessage> errors = validateString(factoryPid, configId);
        if (errors.isEmpty() && !WFS_FACTORY_PIDS.contains(factoryPid)) {
            errors.add(createInvalidFieldMsg("Unknown factory PID type \"" + factoryPid
                            + "\". Wfs factory pid must be one of: " + String.join(",", WFS_FACTORY_PIDS),
                    configId));
        }
        return errors;
    }

    public List<ConfigurationMessage> validateCswFactoryPid(String factoryPid, String configId) {
        List<ConfigurationMessage> errors = validateString(factoryPid, configId);
        if (errors.isEmpty() && !CSW_FACTORY_PIDS.contains(factoryPid)) {
            errors.add(createInvalidFieldMsg("Unknown factory PID type \"" + factoryPid
                            + "\". CSW factory pid must be one of: " + String.join(",", CSW_FACTORY_PIDS),
                    configId));
        }
        return errors;
    }

    public List<ConfigurationMessage> validateOpensearchFactoryPid(String factoryPid,
            String configId) {
        List<ConfigurationMessage> errors = validateString(factoryPid, configId);
        if (errors.isEmpty() && !OPENSEARCH_FACTORY_PID.equals(factoryPid)) {
            errors.add(createInvalidFieldMsg("Unknown factory PID type \"" + factoryPid
                    + "\". OpenSearch factory pid must be " + OPENSEARCH_FACTORY_PID, configId));
        }
        return errors;
    }

    public List<ConfigurationMessage> validateOptionalUsernameAndPassword(
            SourceConfiguration configuration) {
        List<ConfigurationMessage> validationResults = new ArrayList<>();
        if (configuration.sourceUserName() != null) {
            validationResults.addAll(configuration.validate(Arrays.asList(SOURCE_USERNAME,
                    SOURCE_USER_PASSWORD)));
        }
        return validationResults;
    }

    /**
     * Validates the {@param sourceName} against the existing source names of the configuration's for
     * the given {@param factoryPids} using the {@param configurator}. An empty {@link List} will be returned
     * if there are no existing source names with with name {@param sourceName}, or a {@link List} containing
     * {@link ConfigurationMessage}s if there are errors. If the {@param configurator} is {@code null}, one will
     * be created.
     *
     * @param sourceName   a non null name to validate
     * @param factoryPids  a list of non null factory pids of the configuration to validate names against
     * @param configurator configurator to fetch configurations for the given {@param factoryPids}
     * @return a {@link List} of {@link ConfigurationMessage}s containing failure messages, or empty {@link List}
     * if there are no duplicate source names found
     */
    public List<ConfigurationMessage> validateSourceName(@Nonnull String sourceName,
            @Nonnull List<String> factoryPids, Configurator configurator) {
        if (configurator == null) {
            configurator = new Configurator();
        }

        List<Map<String, Map<String, Object>>> configurations = factoryPids.stream()
                .map(configurator::getManagedServiceConfigs)
                .filter(config -> !config.isEmpty())
                .collect(Collectors.toList());

        for (Map<String, Map<String, Object>> config : configurations) {
            for (Map<String, Object> entry : config.values()) {
                Object id = entry.get("id");
                if (id instanceof String) {
                    String name = (String) id;
                    if (StringUtils.isNotEmpty(name) && sourceName.equals(name)) {
                        LOGGER.debug(
                                "Found duplicate existing source name when creating source with name \"{}\"",
                                sourceName);

                        return Collections.singletonList(createInvalidFieldMsg(String.format(
                                "A source with the name \"%s\" is already in use. Please choose another name.",
                                sourceName), SOURCE_NAME));
                    }
                } else {
                    LOGGER.debug(
                            "Unable to validate duplicity of the incoming configuration's source name \"{}\"",
                            sourceName);
                    return Collections.singletonList(buildMessage(ConfigurationMessage.MessageType.FAILURE,
                            ConfigurationMessage.INTERNAL_ERROR,
                            String.format("Error validating \"%s\" against existing source names.",
                                    sourceName)));
                }
            }
        }

        return Collections.emptyList();
    }
}
