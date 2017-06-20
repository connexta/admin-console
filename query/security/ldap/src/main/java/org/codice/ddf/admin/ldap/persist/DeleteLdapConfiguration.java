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
package org.codice.ddf.admin.ldap.persist;

import java.util.List;

import org.codice.ddf.admin.api.DataType;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.common.fields.base.BaseFunctionField;
import org.codice.ddf.admin.common.fields.base.scalar.BooleanField;
import org.codice.ddf.admin.common.fields.common.PidField;
import org.codice.ddf.admin.common.services.ServiceCommons;
import org.codice.ddf.admin.configurator.ConfiguratorFactory;
import org.codice.ddf.internal.admin.configurator.actions.ManagedServiceActions;
import org.codice.ddf.internal.admin.configurator.actions.PropertyActions;
import org.codice.ddf.internal.admin.configurator.actions.ServiceActions;

import com.google.common.collect.ImmutableList;

public class DeleteLdapConfiguration extends BaseFunctionField<BooleanField> {
    public static final String FIELD_NAME = "deleteLdapConfig";

    public static final String DESCRIPTION = "Deletes the specified LDAP configuration.";

    public static final BooleanField RETURN_TYPE = new BooleanField();

    private PidField pid;

    private final ConfiguratorFactory configuratorFactory;

    private final ManagedServiceActions managedServiceActions;

    private final PropertyActions propertyActions;

    private final ServiceActions serviceActions;

    private ServiceCommons serviceCommons;

    public DeleteLdapConfiguration(ConfiguratorFactory configuratorFactory,
            ManagedServiceActions managedServiceActions, PropertyActions propertyActions,
            ServiceActions serviceActions) {
        super(FIELD_NAME, DESCRIPTION);
        this.configuratorFactory = configuratorFactory;
        this.managedServiceActions = managedServiceActions;
        this.propertyActions = propertyActions;
        this.serviceActions = serviceActions;

        pid = new PidField();
        pid.isRequired(true);

        updateArgumentPaths();
        serviceCommons = new ServiceCommons(managedServiceActions,
                serviceActions,
                null,
                configuratorFactory);
    }

    @Override
    public BooleanField getReturnType() {
        return RETURN_TYPE;
    }

    @Override
    public List<DataType> getArguments() {
        return ImmutableList.of(pid);
    }

    @Override
    public BooleanField performFunction() {
        addMessages(serviceCommons.deleteService(pid));
        return new BooleanField(!containsErrorMsgs());
    }

    @Override
    public void validate() {
        super.validate();
        if (containsErrorMsgs()) {
            return;
        }

        addMessages(serviceCommons.serviceConfigurationExists(pid));
    }

    @Override
    public FunctionField<BooleanField> newInstance() {
        return new DeleteLdapConfiguration(configuratorFactory,
                managedServiceActions,
                propertyActions,
                serviceActions);
    }
}
