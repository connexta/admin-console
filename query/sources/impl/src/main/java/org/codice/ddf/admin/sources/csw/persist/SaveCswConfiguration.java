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
 **/
package org.codice.ddf.admin.sources.csw.persist;

import static org.codice.ddf.admin.common.message.DefaultMessages.noExistingConfigError;
import static org.codice.ddf.admin.common.services.ServiceCommons.configExists;
import static org.codice.ddf.admin.common.services.ServiceCommons.update;
import static org.codice.ddf.admin.sources.commons.SourceActionCommons.persistSource;
import static org.codice.ddf.admin.sources.commons.utils.SourceValidationUtils.validateSourceName;
import static org.codice.ddf.admin.sources.services.CswServiceProperties.cswConfigToServiceProps;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.codice.ddf.admin.api.fields.Field;
import org.codice.ddf.admin.common.actions.BaseAction;
import org.codice.ddf.admin.common.fields.base.scalar.BooleanField;
import org.codice.ddf.admin.configurator.ConfiguratorFactory;
import org.codice.ddf.admin.sources.fields.ServicePid;
import org.codice.ddf.admin.sources.fields.type.CswSourceConfigurationField;

import com.google.common.collect.ImmutableList;

public class SaveCswConfiguration extends BaseAction<BooleanField> {

    public static final String ID = "saveCswSource";

    public static final String DESCRIPTION =
            "Saves a CSW source configuration. If a pid is specified, the source configuration specified by the pid will be updated. Returns true on success and false on failure.";

    private CswSourceConfigurationField config;

    private ServicePid servicePid;

    private ConfiguratorFactory configuratorFactory;


    public SaveCswConfiguration(ConfiguratorFactory configuratorFactory) {
        super(ID, DESCRIPTION, new BooleanField());
        config = new CswSourceConfigurationField();
        servicePid = new ServicePid();
        config.isRequired(true);
        config.factoryPidField().isRequired(true);
        config.sourceNameField().isRequired(true);
        config.endpointUrlField().isRequired(true);
        this.configuratorFactory = configuratorFactory;
    }

    @Override
    public BooleanField performAction() {
        if (StringUtils.isNotEmpty(servicePid.getValue())) {
            addArgumentMessages(update(servicePid, cswConfigToServiceProps(config), configuratorFactory));
        } else {
            addArgumentMessages(persistSource(config, cswConfigToServiceProps(config), configuratorFactory));
        }

        if(containsErrorMsgs()) {
            return new BooleanField(false);
        }
        return new BooleanField(true);
    }

    @Override
    public void validate() {
        super.validate();
        if(containsErrorMsgs()) {
            return;
        }

        if(servicePid.getValue() != null && !configExists(servicePid.getValue(), configuratorFactory)) {
            addArgumentMessage(noExistingConfigError(servicePid.path()));
        } else {
            addArgumentMessages(validateSourceName(config.sourceNameField(), configuratorFactory, servicePid));
        }
    }

    @Override
    public List<Field> getArguments() {
        return ImmutableList.of(config, servicePid);
    }
}
