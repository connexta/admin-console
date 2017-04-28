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

import static org.codice.ddf.admin.common.report.message.DefaultMessages.failedDeleteError;
import static org.codice.ddf.admin.common.report.message.DefaultMessages.noExistingConfigError;

import java.util.List;

import org.codice.ddf.admin.api.DataType;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.api.fields.ListField;
import org.codice.ddf.admin.common.fields.base.BaseFunctionField;
import org.codice.ddf.admin.common.fields.base.ListFieldImpl;
import org.codice.ddf.admin.common.fields.common.PidField;
import org.codice.ddf.admin.configurator.Configurator;
import org.codice.ddf.admin.configurator.ConfiguratorFactory;
import org.codice.ddf.admin.configurator.OperationReport;
import org.codice.ddf.admin.ldap.commons.LdapTestingUtils;
import org.codice.ddf.admin.ldap.commons.services.LdapServiceCommons;
import org.codice.ddf.admin.ldap.fields.config.LdapConfigurationField;
import org.codice.ddf.internal.admin.configurator.actions.ManagedServiceActions;
import org.codice.ddf.internal.admin.configurator.actions.PropertyActions;
import org.codice.ddf.internal.admin.configurator.actions.ServiceActions;

import com.google.common.collect.ImmutableList;

public class DeleteLdapConfiguration extends BaseFunctionField<ListField<LdapConfigurationField>> {

    public static final String NAME = "deleteLdapConfig";

    public static final String DESCRIPTION = "Deletes the specified LDAP configuration.";

    private PidField pid;

    private final ConfiguratorFactory configuratorFactory;

    private final ManagedServiceActions managedServiceActions;

    private final PropertyActions propertyActions;

    private final ServiceActions serviceActions;

    private LdapServiceCommons serviceCommons;

    private LdapTestingUtils testingUtils;

    public DeleteLdapConfiguration(ConfiguratorFactory configuratorFactory,
            ManagedServiceActions managedServiceActions, PropertyActions propertyActions,
            ServiceActions serviceActions) {
        super(NAME, DESCRIPTION, new ListFieldImpl<>("configs", LdapConfigurationField.class));
        this.managedServiceActions = managedServiceActions;
        this.propertyActions = propertyActions;
        this.serviceActions = serviceActions;
        pid = new PidField();
        updateArgumentPaths();
        this.configuratorFactory = configuratorFactory;
        serviceCommons = new LdapServiceCommons(this.propertyActions, this.managedServiceActions);
        testingUtils = new LdapTestingUtils();

    }

    @Override
    public List<DataType> getArguments() {
        return ImmutableList.of(pid);
    }

    @Override
    public ListField<LdapConfigurationField> performFunction() {
        Configurator configurator = configuratorFactory.getConfigurator();
        configurator.add(managedServiceActions.delete(pid.getValue()));
        OperationReport report =
                configurator.commit("LDAP Configuration deleted for servicePid: {}",
                        pid.getValue());

        if (report.containsFailedResults()) {
            addResultMessage(failedDeleteError());
        }

        return serviceCommons.getLdapConfigurations();
    }

    @Override
    public void validate() {
        super.validate();
        if (containsErrorMsgs()) {
            return;
        }

        if (!testingUtils.serviceExists(pid.getValue(), serviceActions)) {
            addArgumentMessage(noExistingConfigError(pid.path()));
        }
    }

    @Override
    public FunctionField<ListField<LdapConfigurationField>> newInstance() {
        return new DeleteLdapConfiguration(configuratorFactory,
                managedServiceActions,
                propertyActions,
                serviceActions);
    }
}
