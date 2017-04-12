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
package org.codice.ddf.admin.ldap.fields.connection;

import java.util.List;

import org.codice.ddf.admin.api.fields.Field;
import org.codice.ddf.admin.common.fields.base.BaseObjectField;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;

import com.google.common.collect.ImmutableList;

public class LdapBindUserInfo extends BaseObjectField {
    public static final String FIELD_NAME = "bindInfo";

    public static final String FIELD_TYPE_NAME = "BindUserInfo";

    public static final String DESCRIPTION =
            "Contains the required information to bind a user to an LDAP connection.";

    public static final String USERNAME = "username";

    public static final String PASSWORD = "password";

    private StringField username;

    private StringField password;

    private LdapBindMethod bindMethod;

    private LdapRealm realm;

    public LdapBindUserInfo() {
        super(FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
        this.username = new StringField(USERNAME);
        this.password = new StringField(PASSWORD);
        this.bindMethod = new LdapBindMethod();
        this.realm = new LdapRealm();
    }

    public LdapBindUserInfo username(String username) {
        this.username.setValue(username);
        return this;
    }

    public LdapBindUserInfo password(String password) {
        this.password.setValue(password);
        return this;
    }

    public LdapBindUserInfo bindMethod(String bindMethod) {
        // TODO: tbatie - 4/2/17 - Match the bind method to a specific type
        this.bindMethod.setValue(bindMethod);
        return this;
    }

    public LdapBindUserInfo realm(String realm) {
        // TODO: tbatie - 4/2/17 - Match the bind method to a specific type
        this.realm.setValue(realm);
        return this;
    }


    public String username() {
        return username.getValue();
    }

    public String password() {
        return password.getValue();
    }

    public String bindMethod() {
        return bindMethod.getValue();
    }

    public String realm() {
        return realm.getValue();
    }

    @Override
    public List<Field> getFields() {
        return ImmutableList.of(username, password, bindMethod, realm);
    }
}
