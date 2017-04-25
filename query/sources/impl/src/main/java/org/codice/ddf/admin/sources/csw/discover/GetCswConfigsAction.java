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
package org.codice.ddf.admin.sources.csw.discover;

import static org.codice.ddf.admin.sources.commons.SourceActionCommons.createSourceInfoField;
import static org.codice.ddf.admin.sources.commons.services.CswServiceProperties.CSW_FACTORY_PIDS;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.codice.ddf.admin.api.fields.Field;
import org.codice.ddf.admin.common.actions.BaseAction;
import org.codice.ddf.admin.common.fields.base.ListFieldImpl;
import org.codice.ddf.admin.configurator.ConfigReader;
import org.codice.ddf.admin.configurator.ConfiguratorFactory;
import org.codice.ddf.admin.sources.commons.services.CswServiceProperties;
import org.codice.ddf.admin.sources.fields.ServicePid;
import org.codice.ddf.admin.sources.fields.SourceInfoField;

import com.google.common.collect.ImmutableList;

public class GetCswConfigsAction extends BaseAction<ListFieldImpl<SourceInfoField>> {

    public static final String ID = "cswConfigs";

    public static final String DESCRIPTION =
            "Retrieves all currently configured CSW sources. If a the pid argument is specified, only the source configuration with that pid will be returned.";

    private ServicePid servicePid;

    private ConfiguratorFactory configuratorFactory;

    public GetCswConfigsAction(ConfiguratorFactory configuratorFactory) {
        super(ID, DESCRIPTION, new ListFieldImpl<>(SourceInfoField.class));
        this.configuratorFactory = configuratorFactory;
        servicePid = new ServicePid();
        servicePid.isRequired(false);
    }

    @Override
    public List<Field> getArguments() {
        return ImmutableList.of(servicePid);
    }

    @Override
    public ListFieldImpl<SourceInfoField> performAction() {
        ListFieldImpl<SourceInfoField> sourceInfoListField =
                new ListFieldImpl<>(SourceInfoField.class);
        ConfigReader configReader = configuratorFactory.getConfigReader();

        CSW_FACTORY_PIDS.stream()
                .flatMap(factoryPid -> configReader.getManagedServiceConfigs(factoryPid)
                        .values()
                        .stream())
                .map(CswServiceProperties::servicePropsToCswConfig)
                .filter(config -> {
                    if (StringUtils.isNotEmpty(servicePid.getValue())) {
                        return config.servicePid()
                                .equals(servicePid.getValue());
                    }
                    return true;
                })
                .forEach(config -> sourceInfoListField.add(createSourceInfoField(ID,
                        true,
                        config)));

        sourceInfoListField.getList()
                .forEach(source -> source.config()
                        .credentials()
                        .password("*****"));

        return sourceInfoListField;
    }
}
