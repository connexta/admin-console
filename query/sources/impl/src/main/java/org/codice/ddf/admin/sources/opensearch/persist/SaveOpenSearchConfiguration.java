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
import static org.codice.ddf.admin.sources.commons.SourceActionCommons.persist;
import static org.codice.ddf.admin.sources.commons.SourceActionCommons.updateConfig;
import static org.codice.ddf.admin.sources.commons.services.OpenSearchServiceProperties.openSearchConfigToServiceProps;
import static org.codice.ddf.admin.sources.fields.type.SourceConfigUnionField.ENDPOINT_URL_FIELD;
import static org.codice.ddf.admin.sources.fields.type.SourceConfigUnionField.FACTORY_PID_FIELD;
import static org.codice.ddf.admin.sources.fields.type.SourceConfigUnionField.SOURCE_NAME_FIELD;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.codice.ddf.admin.api.action.Message;
import org.codice.ddf.admin.api.fields.Field;
import org.codice.ddf.admin.api.fields.ObjectField;
import org.codice.ddf.admin.common.actions.BaseAction;
import org.codice.ddf.admin.configurator.ConfiguratorFactory;
import org.codice.ddf.admin.sources.fields.ServicePid;
import org.codice.ddf.admin.sources.fields.SourceInfoField;
import org.codice.ddf.admin.sources.fields.type.OpensearchSourceConfigurationField;

import com.google.common.collect.ImmutableList;

public class SaveOpenSearchConfiguration extends BaseAction<SourceInfoField> {

    public static final String ID = "saveOpenSearchSource";

    public static final String DESCRIPTION =
            "Saves an OpenSearch source configuration. If a pid is specified, the source configuration specified by the pid will be updated.";

    private OpensearchSourceConfigurationField config;

    private ServicePid servicePid;

    private ConfiguratorFactory configuratorFactory;


    public SaveOpenSearchConfiguration(ConfiguratorFactory configuratorFactory) {
        super(ID, DESCRIPTION, new SourceInfoField());
        config = new OpensearchSourceConfigurationField();
        servicePid = new ServicePid();

        config.isRequired(true);
        setRequiredField(config, FACTORY_PID_FIELD, true);
        setRequiredField(config, SOURCE_NAME_FIELD, true);
        setRequiredField(config, ENDPOINT_URL_FIELD, true);

        this.configuratorFactory = configuratorFactory;
    }

    @Override
    public SourceInfoField performAction() {
        if (StringUtils.isNotEmpty(servicePid.getValue())) {
            return updateExistingConfig();
        }
        return persistNewConfig();
    }

    @Override
    public List<Field> getArguments() {
        return ImmutableList.of(config, servicePid);
    }

    private SourceInfoField updateExistingConfig() {
        List<Message> results = updateConfig(servicePid, config, configuratorFactory, openSearchConfigToServiceProps(config));

        if(CollectionUtils.isNotEmpty(results)) {
            results.forEach(this::addArgumentMessage);
            return null;
        }

        return createSourceInfoField(ID, true, config);
    }

    private SourceInfoField persistNewConfig() {
        List<Message> results = persist(config, configuratorFactory, openSearchConfigToServiceProps(config));

        if (CollectionUtils.isNotEmpty(results)) {
            results.forEach(this::addArgumentMessage);
            return null;
        }

        return createSourceInfoField(ID, true, config);
    }

    private void setRequiredField(ObjectField objectField, String fieldName, boolean required) {
        objectField.getFields()
                .stream()
                .filter(field -> field.fieldName()
                        .equals(fieldName))
                .findFirst()
                .ifPresent(field -> field.isRequired(required));
    }
}
