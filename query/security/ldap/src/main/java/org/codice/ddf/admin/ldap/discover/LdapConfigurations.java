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
package org.codice.ddf.admin.ldap.discover;

import static org.codice.ddf.admin.common.report.message.DefaultMessages.noExistingConfigError;

import java.util.List;

import org.codice.ddf.admin.api.DataType;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.api.fields.ListField;
import org.codice.ddf.admin.common.fields.base.BaseFunctionField;
import org.codice.ddf.admin.common.fields.base.ListFieldImpl;
import org.codice.ddf.admin.common.fields.common.PidField;
import org.codice.ddf.admin.configurator.ConfiguratorFactory;
import org.codice.ddf.admin.ldap.commons.LdapTestingUtils;
import org.codice.ddf.admin.ldap.commons.services.LdapServiceCommons;
import org.codice.ddf.admin.ldap.fields.config.LdapConfigurationField;
import org.codice.ddf.internal.admin.configurator.actions.ManagedServiceActions;
import org.codice.ddf.internal.admin.configurator.actions.PropertyActions;
import org.codice.ddf.internal.admin.configurator.actions.ServiceActions;

import com.google.common.collect.ImmutableList;

public class LdapConfigurations extends BaseFunctionField<ListField<LdapConfigurationField>> {

    public static final String NAME = "configs";

    public static final String CONFIGS_ARG_NAME = "configs";

    public static final String DESCRIPTION = "Retrieves all currently configured LDAP settings.";

    private PidField pid;

    private ConfiguratorFactory configuratorFactory;

    private final ManagedServiceActions managedServiceActions;

    private final PropertyActions propertyActions;

    private final ServiceActions serviceActions;

    private LdapServiceCommons serviceCommons;

    private LdapTestingUtils testingUtils;

    public LdapConfigurations(ConfiguratorFactory configuratorFactory,
            ManagedServiceActions managedServiceActions, PropertyActions propertyActions,
            ServiceActions serviceActions) {
        super(NAME,
                DESCRIPTION,
                new ListFieldImpl<>(CONFIGS_ARG_NAME, LdapConfigurationField.class));
        this.configuratorFactory = configuratorFactory;
        this.managedServiceActions = managedServiceActions;
        this.serviceActions = serviceActions;
        this.propertyActions = propertyActions;

        pid = new PidField();
        updateArgumentPaths();

        serviceCommons = new LdapServiceCommons(this.propertyActions, this.managedServiceActions);
        testingUtils = new LdapTestingUtils();
    }

    @Override
    public List<DataType> getArguments() {
        return ImmutableList.of(pid);
    }

    @Override
    public ListField<LdapConfigurationField> performFunction() {
        return serviceCommons.getLdapConfigurations();
    }

    @Override
    public void validate() {
        super.validate();
        if (containsErrorMsgs()) {
            return;
        }

        if (pid.getValue() != null && !testingUtils.serviceExists(pid.getValue(), serviceActions)) {
            addArgumentMessage(noExistingConfigError(pid.path()));
        }
    }

    @Override
    public FunctionField<ListField<LdapConfigurationField>> newInstance() {
        return new LdapConfigurations(configuratorFactory,
                managedServiceActions,
                propertyActions,
                serviceActions);
    }
}
