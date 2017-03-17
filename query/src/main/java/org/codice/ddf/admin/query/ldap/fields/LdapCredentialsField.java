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
package org.codice.ddf.admin.query.ldap.fields;

import java.util.List;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.commons.fields.base.BaseObjectField;
import org.codice.ddf.admin.query.commons.fields.base.scalar.StringField;

import com.google.common.collect.ImmutableList;

public class LdapCredentialsField extends BaseObjectField {
    public static final String FIELD_NAME = "credentials";
    public static final String FIELD_TYPE_NAME = "LdapCredentials";
    public static final String DESCRIPTION = "Contains the required credentials to bind a user to an LDAP connection.";

    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    private StringField username;
    private StringField password;

    public LdapCredentialsField() {
        super(FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
        this.username = new StringField(USERNAME);
        this.password = new StringField(PASSWORD);
    }

    public LdapCredentialsField username(String username) {
        this.username.setValue(username);
        return this;
    }

    public LdapCredentialsField password(String password) {
        this.password.setValue(password);
        return this;
    }

    @Override
    public List<Field> getFields() {
        return ImmutableList.of(username, password);
    }
}
