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
package org.codice.ddf.admin.ldap.actions.persist;

import java.util.List;

import org.codice.ddf.admin.api.fields.Field;
import org.codice.ddf.admin.api.fields.ListField;
import org.codice.ddf.admin.common.actions.BaseAction;
import org.codice.ddf.admin.common.fields.base.ListFieldImpl;
import org.codice.ddf.admin.common.fields.common.PidField;
import org.codice.ddf.admin.configurator.Configurator;
import org.codice.ddf.admin.configurator.ConfiguratorFactory;
import org.codice.ddf.admin.configurator.OperationReport;
import org.codice.ddf.admin.ldap.actions.commons.services.LdapServiceCommons;
import org.codice.ddf.admin.ldap.fields.config.LdapConfigurationField;
import org.codice.ddf.internal.admin.configurator.opfactory.ManagedServiceOpFactory;
import org.codice.ddf.internal.admin.configurator.opfactory.PropertyOpFactory;

import com.google.common.collect.ImmutableList;

public class DeleteLdapConfiguration extends BaseAction<ListField<LdapConfigurationField>> {

    public static final String NAME = "deleteLdapConfig";

    public static final String DESCRIPTION = "Deletes the specified LDAP configuration.";

    private PidField pid;

    private ConfiguratorFactory configuratorFactory;

    private ManagedServiceOpFactory managedServiceOpFactory;

    private PropertyOpFactory propertyOpFactory;

    private LdapServiceCommons serviceCommons;

    public DeleteLdapConfiguration(ConfiguratorFactory configuratorFactory,
            ManagedServiceOpFactory managedServiceOpFactory, PropertyOpFactory propertyOpFactory) {
        super(NAME, DESCRIPTION, new ListFieldImpl<>("configs", LdapConfigurationField.class));
        this.propertyOpFactory = propertyOpFactory;
        pid = new PidField();

        this.configuratorFactory = configuratorFactory;
        this.managedServiceOpFactory = managedServiceOpFactory;
        serviceCommons = new LdapServiceCommons(managedServiceOpFactory, this.propertyOpFactory);
    }

    @Override
    public List<Field> getArguments() {
        return ImmutableList.of(pid);
    }

    @Override
    public ListField<LdapConfigurationField> performAction() {
        Configurator configurator = configuratorFactory.getConfigurator();
        configurator.add(managedServiceOpFactory.delete(pid.getValue()));
        OperationReport report =
                configurator.commit("LDAP Configuration deleted for servicePid: {}",
                        pid.getValue());
        // TODO: tbatie - 4/3/17 - Add error reporting here
        return serviceCommons.getLdapConfigurations();
    }
}
