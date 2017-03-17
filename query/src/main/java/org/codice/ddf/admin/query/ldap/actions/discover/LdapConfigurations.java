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
package org.codice.ddf.admin.query.ldap.actions.discover;

import static org.codice.ddf.admin.query.ldap.sample.SampleFields.SAMPLE_LDAP_CONFIGURATION;

import java.util.List;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.commons.actions.BaseAction;
import org.codice.ddf.admin.query.commons.fields.common.PidField;
import org.codice.ddf.admin.query.ldap.fields.LdapConfigurationsField;

import com.google.common.collect.ImmutableList;

public class LdapConfigurations extends BaseAction<LdapConfigurationsField> {

    public static final String NAME = "configs";
    public static final String DESCRIPTION = "Retrieves all currently configured LDAP settings.";

    private PidField pid = new PidField();
    private List<Field> arguments = ImmutableList.of(pid);

    public LdapConfigurations() {
        super(NAME, DESCRIPTION, new LdapConfigurationsField());
    }

    @Override
    public LdapConfigurationsField process() {
        return new LdapConfigurationsField()
                .add(SAMPLE_LDAP_CONFIGURATION)
                .add(SAMPLE_LDAP_CONFIGURATION);
    }

    @Override
    public List<Field> getArguments() {
        return arguments;
    }
}
