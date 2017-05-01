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

import static org.codice.ddf.admin.common.message.DefaultMessages.failedPersistError;
import static org.codice.ddf.admin.common.services.ServiceCommons.persist;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.apache.commons.lang.StringUtils;
import org.codice.ddf.admin.api.action.Message;
import org.codice.ddf.admin.common.fields.base.ListFieldImpl;
import org.codice.ddf.admin.configurator.ConfigReader;
import org.codice.ddf.admin.configurator.ConfiguratorFactory;
import org.codice.ddf.admin.sources.fields.ServicePid;
import org.codice.ddf.admin.sources.fields.SourceInfoField;
import org.codice.ddf.admin.sources.fields.type.SourceConfigUnionField;

import ddf.catalog.source.ConnectedSource;
import ddf.catalog.source.FederatedSource;

public class SourceActionCommons {

    public static SourceInfoField createSourceInfoField(String sourceHandlerName,
            boolean isAvailable, SourceConfigUnionField config) {
        config.credentials().password("*****");
        SourceInfoField sourceInfoField = new SourceInfoField();
        sourceInfoField.sourceHandlerName(sourceHandlerName);
        sourceInfoField.isAvaliable(isAvailable);
        sourceInfoField.configuration(config);
        return sourceInfoField;
    }

    public static List<Message> persistSource(SourceConfigUnionField config,
            Map<String, Object> serviceProps, ConfiguratorFactory configuratorFactory) {
        if (!persist(serviceProps, config.factoryPid(), configuratorFactory)) {
            return Collections.singletonList(failedPersistError(config.path()));
        }
        return Collections.emptyList();
    }

    /**
     * Gets the configurations for the given factoryPids using the {@link ConfiguratorFactory}. A mapper is used
     * to transform the service properties to a {@link SourceConfigUnionField}.
     *
     * @param factoryPids factory pids to lookup configurations for
     * @param mapper a {@link Function} taking a map of string to objects and returning a {@code SourceConfigUnionField}
     * @param selector a {@code ServicePid} to select a single configuration, returns all configs when null or empty
     * @return a list of {@code SourceInfoField}s configured in the system
     */
    public static ListFieldImpl<SourceInfoField> getSourceConfigurations(List<String> factoryPids, Function<Map<String, Object>, SourceConfigUnionField> mapper,
            ServicePid selector, ConfiguratorFactory configuratorFactory, String actionHandlerId) {
        ListFieldImpl<SourceInfoField> sourceInfoListField = new ListFieldImpl<>(SourceInfoField.class);
        ConfigReader configReader = configuratorFactory.getConfigReader();

        if (StringUtils.isNotEmpty(selector.getValue())) {
            SourceConfigUnionField config = mapper.apply(configReader.getConfig(selector.getValue()));
            sourceInfoListField.add(createSourceInfoField(actionHandlerId, true, config));
            return sourceInfoListField;
        }

        factoryPids.stream()
                .flatMap(factoryPid -> configReader.getManagedServiceConfigs(factoryPid)
                        .values()
                        .stream())
                .map(mapper)
                .forEach(config -> sourceInfoListField.add(createSourceInfoField(actionHandlerId, true, config)));

        return sourceInfoListField;

    }


    public static ListFieldImpl<SourceInfoField> getAllSourceConfigurations(ConfiguratorFactory configuratorFactory) {
        ListFieldImpl<SourceInfoField> sourceInfoListField = new ListFieldImpl<>(SourceInfoField.class);
        ConfigReader configReader = configuratorFactory.getConfigReader();

        Set<FederatedSource> federatedSources = configReader.getServices(FederatedSource.class, null);
        Set<ConnectedSource> connectedSource = configReader.getServices(ConnectedSource.class, null);

        return sourceInfoListField;
    }
}
