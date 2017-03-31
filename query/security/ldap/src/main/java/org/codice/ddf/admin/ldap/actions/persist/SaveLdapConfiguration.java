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
import org.codice.ddf.admin.common.actions.BaseAction;
import org.codice.ddf.admin.security.common.fields.ldap.LdapConfigurationField;

import com.google.common.collect.ImmutableList;

public class SaveLdapConfiguration extends BaseAction<LdapConfigurationField> {

    public static final String NAME = "saveLdap";

    public static final String DESCRIPTION = "Saves the LDAP configuration.";

    private LdapConfigurationField config;

    public SaveLdapConfiguration() {
        super(NAME, DESCRIPTION, new LdapConfigurationField());
        config = new LdapConfigurationField();
    }

    @Override
    public List<Field> getArguments() {
        return ImmutableList.of(config);
    }

    @Override
    public LdapConfigurationField performAction() {
        return config;
    }
}
