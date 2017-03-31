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

import static org.codice.ddf.admin.ldap.sample.SampleFields.SAMPLE_LDAP_SETTINGS;

import java.util.List;

import org.codice.ddf.admin.api.fields.Field;
import org.codice.ddf.admin.common.actions.BaseAction;
import org.codice.ddf.admin.security.common.fields.ldap.LdapConnectionField;
import org.codice.ddf.admin.security.common.fields.ldap.LdapCredentialsField;
import org.codice.ddf.admin.security.common.fields.ldap.LdapSettingsField;

import com.google.common.collect.ImmutableList;

public class LdapRecommendedSettings extends BaseAction<LdapSettingsField> {

    public static final String NAME = "recommendedSettings";

    public static final String DESCRIPTION =
            "Attempts to retrieve recommended settings from the LDAP connection.";

    private LdapConnectionField connection = new LdapConnectionField();

    private LdapCredentialsField credentials = new LdapCredentialsField();

    public LdapRecommendedSettings() {
        super(NAME, DESCRIPTION, new LdapSettingsField());
    }

    @Override
    public List<Field> getArguments() {
        return ImmutableList.of(connection, credentials);
    }

    @Override
    public LdapSettingsField performAction() {
        return SAMPLE_LDAP_SETTINGS;
    }
}
