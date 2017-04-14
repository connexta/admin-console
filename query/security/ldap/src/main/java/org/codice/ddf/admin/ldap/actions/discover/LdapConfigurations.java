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
package org.codice.ddf.admin.ldap.actions.discover;

import java.util.List;

import org.codice.ddf.admin.api.fields.Field;
import org.codice.ddf.admin.api.fields.ListField;
import org.codice.ddf.admin.common.actions.BaseAction;
import org.codice.ddf.admin.common.fields.base.ListFieldImpl;
import org.codice.ddf.admin.common.fields.common.PidField;
import org.codice.ddf.admin.configurator.ConfiguratorFactory;
import org.codice.ddf.admin.ldap.actions.commons.services.LdapServiceCommons;
import org.codice.ddf.admin.ldap.fields.config.LdapConfigurationField;

import com.google.common.collect.ImmutableList;

public class LdapConfigurations extends BaseAction<ListField<LdapConfigurationField>> {

    public static final String NAME = "configs";

    public static final String CONFIGS_ARG_NAME = "configs";

    public static final String DESCRIPTION = "Retrieves all currently configured LDAP settings.";

    private PidField pid = new PidField();

    private ConfiguratorFactory configuratorFactory;
    private LdapServiceCommons serviceCommons;

    private List<Field> arguments = ImmutableList.of(pid);

    public LdapConfigurations(ConfiguratorFactory configuratorFactory) {
        super(NAME, DESCRIPTION, new ListFieldImpl<>(CONFIGS_ARG_NAME, LdapConfigurationField.class));
        this.configuratorFactory = configuratorFactory;
        serviceCommons = new LdapServiceCommons();
    }

    @Override
    public List<Field> getArguments() {
        return arguments;
    }

    @Override
    public ListField<LdapConfigurationField> performAction() {
        return serviceCommons.getLdapConfigurations(configuratorFactory);
    }
}
