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
import static org.codice.ddf.admin.sources.commons.utils.SourceUtilCommons.getSourceConfigurations;
import static org.codice.ddf.admin.sources.services.CswServiceProperties.CSW_FACTORY_PIDS;
import static org.codice.ddf.admin.sources.services.CswServiceProperties.SERVICE_PROPS_TO_CSW_CONFIG;

import java.util.List;

import org.codice.ddf.admin.api.DataType;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.api.fields.ListField;
import org.codice.ddf.admin.common.fields.base.BaseFunctionField;
import org.codice.ddf.admin.common.fields.base.ListFieldImpl;
import org.codice.ddf.admin.common.fields.common.PidField;
import org.codice.ddf.admin.configurator.ConfiguratorFactory;
import org.codice.ddf.admin.sources.fields.SourceInfoField;
import org.codice.ddf.internal.admin.configurator.actions.ManagedServiceActions;
import org.codice.ddf.internal.admin.configurator.actions.ServiceActions;
import org.codice.ddf.internal.admin.configurator.actions.ServiceReader;

import com.google.common.collect.ImmutableList;

public class GetCswConfigurations extends BaseFunctionField<ListField<SourceInfoField>> {

    public static final String ID = "cswConfigs";

    public static final String DESCRIPTION =
            "Retrieves all currently configured CSW sources. If the pid argument is specified, only the source configuration with that pid will be returned.";

    private PidField pid;

    private ConfiguratorFactory configuratorFactory;

    private final ServiceActions serviceActions;

    private final ManagedServiceActions managedServiceActions;

    private final ServiceReader serviceReader;

    public GetCswConfigurations(ConfiguratorFactory configuratorFactory,
            ServiceActions serviceActions, ManagedServiceActions managedServiceActions,
            ServiceReader serviceReader) {
        super(ID, DESCRIPTION, new ListFieldImpl<>(SourceInfoField.class));
        this.configuratorFactory = configuratorFactory;
        this.serviceActions = serviceActions;
        this.managedServiceActions = managedServiceActions;
        this.serviceReader = serviceReader;

        pid = new PidField();
        updateArgumentPaths();
    }

    @Override
    public ListField<SourceInfoField> performFunction() {
        return getSourceConfigurations(CSW_FACTORY_PIDS,
                SERVICE_PROPS_TO_CSW_CONFIG,
                pid.getValue(),
                serviceActions,
                managedServiceActions,
                serviceReader);
    }

    @Override
    public void validate() {
        super.validate();
        if (containsErrorMsgs()) {
            return;
        }

        if (pid.getValue() != null) {
            addMessages(serviceConfigurationExists(pid, serviceActions));
        }
    }

    @Override
    public List<DataType> getArguments() {
        return ImmutableList.of(pid);
    }

    @Override
    public FunctionField<ListField<SourceInfoField>> newInstance() {
        return new GetCswConfigurations(configuratorFactory,
                serviceActions,
                managedServiceActions,
                serviceReader);
    }
}
