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
package org.codice.ddf.admin.sources.opensearch.persist;

import static org.codice.ddf.admin.sources.commons.SourceActionCommons.createSourceInfoField;
import static org.codice.ddf.admin.sources.commons.SourceActionCommons.deleteConfig;
import static org.codice.ddf.admin.sources.commons.services.OpenSearchServiceProperties.servicePropsToOpenSearchConfig;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.codice.ddf.admin.api.action.Message;
import org.codice.ddf.admin.api.fields.Field;
import org.codice.ddf.admin.common.actions.BaseAction;
import org.codice.ddf.admin.configurator.ConfigReader;
import org.codice.ddf.admin.configurator.ConfiguratorFactory;
import org.codice.ddf.admin.sources.fields.ServicePid;
import org.codice.ddf.admin.sources.fields.SourceInfoField;

import com.google.common.collect.ImmutableList;

public class DeleteOpenSearchConfiguration extends BaseAction<SourceInfoField> {
    public static final String ID = "deleteOpenSearchSource";

    public static final String DESCRIPTION =
            "Deletes a OpenSearch source configuration and returns the deleted configuration.";

    private ServicePid servicePid;

    private ConfiguratorFactory configuratorFactory;

    public DeleteOpenSearchConfiguration(ConfiguratorFactory configuratorFactory) {
        super(ID, DESCRIPTION, new SourceInfoField());
        this.configuratorFactory = configuratorFactory;
        servicePid = new ServicePid();
        servicePid.isRequired(true);
    }

    @Override
    public SourceInfoField performAction() {
        ConfigReader configReader = configuratorFactory.getConfigReader();
        Map<String, Object> configToDelete = configReader.getConfig(servicePid.getValue());

        List<Message> results = deleteConfig(servicePid, configuratorFactory, configToDelete);
        if (CollectionUtils.isNotEmpty(results)) {
            results.forEach(this::addArgumentMessage);
            return null;
        }

        return createSourceInfoField(ID, false, servicePropsToOpenSearchConfig(configToDelete));
    }

    @Override
    public List<Field> getArguments() {
        return ImmutableList.of(servicePid);
    }
}
