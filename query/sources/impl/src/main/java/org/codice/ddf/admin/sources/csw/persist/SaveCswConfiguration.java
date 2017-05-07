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

import static org.codice.ddf.admin.common.message.DefaultMessages.failedPersistError;
import static org.codice.ddf.admin.common.message.DefaultMessages.noExistingConfigError;
import static org.codice.ddf.admin.common.message.DefaultMessages.unsupportedVersionError;
import static org.codice.ddf.admin.common.services.ServiceCommons.createManagedService;
import static org.codice.ddf.admin.common.services.ServiceCommons.serviceConfigurationExists;
import static org.codice.ddf.admin.common.services.ServiceCommons.updateService;
import static org.codice.ddf.admin.sources.commons.utils.SourceValidationUtils.validateSourceName;
import static org.codice.ddf.admin.sources.services.CswServiceProperties.cswConfigToServiceProps;
import static org.codice.ddf.admin.sources.services.CswServiceProperties.resolveCswFactoryPid;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.codice.ddf.admin.api.fields.Field;
import org.codice.ddf.admin.common.actions.BaseAction;
import org.codice.ddf.admin.common.fields.base.scalar.BooleanField;
import org.codice.ddf.admin.common.fields.common.PidField;
import org.codice.ddf.admin.configurator.ConfiguratorFactory;
import org.codice.ddf.admin.sources.fields.type.CswSourceConfigurationField;

import com.google.common.collect.ImmutableList;

public class SaveCswConfiguration extends BaseAction<BooleanField> {

    public static final String ID = "saveCswSource";

    public static final String DESCRIPTION =
            "Saves a CSW source configuration. If a pid is specified, the source configuration specified by the pid will be updated. Returns true on success and false on failure.";

    private CswSourceConfigurationField config;

    private PidField pid;

    private ConfiguratorFactory configuratorFactory;

    public SaveCswConfiguration(ConfiguratorFactory configuratorFactory) {
        super(ID, DESCRIPTION, new BooleanField());
        config = new CswSourceConfigurationField();
        pid = new PidField();
        config.isRequired(true);
        config.cswProfileField().isRequired(true);
        config.sourceNameField().isRequired(true);
        config.endpointUrlField().isRequired(true);
        this.configuratorFactory = configuratorFactory;
    }

    @Override
    public BooleanField performAction() {
        String factoryPid;
        try {
            factoryPid = resolveCswFactoryPid(config.cswProfile());
        } catch (IllegalArgumentException e) {
            addArgumentMessage(unsupportedVersionError(config.cswProfileField().path()));
            return new BooleanField(false);
        }

        if (StringUtils.isNotEmpty(pid.getValue())) {
            addArgumentMessages(updateService(pid, cswConfigToServiceProps(config), configuratorFactory).argumentMessages());
        } else {
            if (createManagedService(cswConfigToServiceProps(config), factoryPid, configuratorFactory).containsErrorMsgs()) {
                addArgumentMessage(failedPersistError(config.path()));
            }
        }
        return new BooleanField(!containsErrorMsgs());
    }

    @Override
    public void validate() {
        super.validate();
        if(containsErrorMsgs()) {
            return;
        }

        if(pid.getValue() != null && !serviceConfigurationExists(pid.getValue(), configuratorFactory)) {
            addArgumentMessage(noExistingConfigError(pid.path()));
        } else {
            addArgumentMessages(validateSourceName(config.sourceNameField(), configuratorFactory, pid).argumentMessages());
        }
    }

    @Override
    public List<Field> getArguments() {
        return ImmutableList.of(config, pid);
    }
}
