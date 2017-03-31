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
package org.codice.ddf.admin.security.common.fields.ldap;

import java.util.List;

import org.codice.ddf.admin.api.fields.Field;
import org.codice.ddf.admin.common.fields.base.BaseObjectField;
import org.codice.ddf.admin.common.fields.common.PidField;

import com.google.common.collect.ImmutableList;

public class LdapConfigurationField extends BaseObjectField {

    public static final String FIELD_NAME = "config";

    public static final String FIELD_TYPE_NAME = "LdapConfiguration";

    public static final String DESCRIPTION =
            "A configuration containing all the required fields for saving LDAP settings";

    private PidField pid;

    private LdapConnectionField connection;

    private LdapCredentialsField credentials;

    private LdapSettingsField settings;

    public LdapConfigurationField() {
        super(FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
        this.pid = new PidField();
        this.connection = new LdapConnectionField();
        this.credentials = new LdapCredentialsField();
        this.settings = new LdapSettingsField();
    }

    public LdapConfigurationField connection(LdapConnectionField connection) {
        this.connection = connection;
        return this;
    }

    public LdapConfigurationField credentials(LdapCredentialsField credentials) {
        this.credentials = credentials;
        return this;
    }

    public LdapConfigurationField settings(LdapSettingsField settings) {
        this.settings = settings;
        return this;
    }

    @Override
    public List<Field> getFields() {
        return ImmutableList.of(pid, connection, credentials, settings);
    }
}
