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

import static org.codice.ddf.admin.common.services.ServiceCommons.serviceConfigurationExists;
import static org.codice.ddf.admin.sources.commons.SourceActionCommons.getSourceConfigurations;
import static org.codice.ddf.admin.sources.services.CswServiceProperties.CSW_FACTORY_PIDS;
import static org.codice.ddf.admin.sources.services.CswServiceProperties.SERVICE_PROPS_TO_CSW_CONFIG;

import java.util.List;

import org.codice.ddf.admin.api.fields.Field;
import org.codice.ddf.admin.api.fields.ListField;
import org.codice.ddf.admin.common.actions.BaseAction;
import org.codice.ddf.admin.common.fields.base.ListFieldImpl;
import org.codice.ddf.admin.common.fields.common.PidField;
import org.codice.ddf.admin.configurator.ConfiguratorFactory;
import org.codice.ddf.admin.sources.fields.SourceInfoField;

import com.google.common.collect.ImmutableList;

public class GetCswConfigsAction extends BaseAction<ListField<SourceInfoField>> {

    public static final String ID = "cswConfigs";

    public static final String DESCRIPTION =
            "Retrieves all currently configured CSW sources. If the pid argument is specified, only the source configuration with that pid will be returned.";

    private PidField pid;

    private ConfiguratorFactory configuratorFactory;

    public GetCswConfigsAction(ConfiguratorFactory configuratorFactory) {
        super(ID, DESCRIPTION, new ListFieldImpl<>(SourceInfoField.class));
        pid = new PidField();
        this.configuratorFactory = configuratorFactory;
    }

    @Override
    public ListField<SourceInfoField> performAction() {
        return getSourceConfigurations(CSW_FACTORY_PIDS,
                SERVICE_PROPS_TO_CSW_CONFIG, pid.getValue(),
                configuratorFactory);
    }

    @Override
    public void validate() {
        super.validate();
        if(containsErrorMsgs()) {
            return;
        }

        if(pid.getValue() != null) {
            addMessages(serviceConfigurationExists(pid, configuratorFactory));
        }
    }

    @Override
    public List<Field> getArguments() {
        return ImmutableList.of(pid);
    }
}
