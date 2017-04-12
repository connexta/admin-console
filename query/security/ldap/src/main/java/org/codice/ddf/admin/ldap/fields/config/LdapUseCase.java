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
package org.codice.ddf.admin.ldap.fields.config;

import org.codice.ddf.admin.api.fields.Field;
import org.codice.ddf.admin.common.fields.base.BaseEnumField;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;

import com.google.common.collect.ImmutableList;

public class LdapUseCase extends BaseEnumField<String> {

    public static final String DEFAULT_FIELD_NAME = "useCase";

    public static final String FIELD_TYPE_NAME = "LdapUseCase";

    public static final String DESCRIPTION = "Describes the intended use of the LDAP settings.";

    public static final String LOGIN = "Login";

    public static final String ATTRIBUTE_STORE = "AttributeStore";

    public static final String LOGIN_AND_ATTRIBUTE_STORE = "LoginAndAttributeStore";

    public static final LdapUseCase LOGIN_FIELD = new LdapUseCase(new Login());

    public static final LdapUseCase ATTRIBUTE_STORE_FIELD = new LdapUseCase(new AttributeStore());

    public static final LdapUseCase LOGIN_AND_ATTRIBUTE_STORE_FIELD = new LdapUseCase(new LoginAndAttributeStore());

    public LdapUseCase() {
        this(null);
    }

    // TODO: tbatie - 3/27/17 - Add constructor for supporting additional authtypes. Do this for all enum fields
    protected LdapUseCase(Field<String> bindMethod) {
        super(DEFAULT_FIELD_NAME,
                FIELD_TYPE_NAME,
                DESCRIPTION,
                ImmutableList.of(new Login(), new AttributeStore(), new LoginAndAttributeStore()),
                bindMethod);
    }

    protected static final class Login extends StringField {

        public static final String DESCRIPTION =
                "Indicates the LDAP is intended to be used as a source to login into.";

        public Login() {
            super(LOGIN, LOGIN, DESCRIPTION);
        }

        @Override
        public String getValue() {
            return LOGIN;
        }
    }

    protected static final class AttributeStore extends StringField {
        public static final String DESCRIPTION =
                "Indicates the LDAP is intended to be used as store for retrieving attributes of entries.";

        public AttributeStore() {
            super(ATTRIBUTE_STORE, ATTRIBUTE_STORE, DESCRIPTION);
        }

        @Override
        public String getValue() {
            return ATTRIBUTE_STORE;
        }
    }

    protected static final class LoginAndAttributeStore extends StringField {
        public static final String DESCRIPTION =
                "Inticates the LDAP is intended to be used as both a source of login and a store for retrieving attributes of entries.";

        public LoginAndAttributeStore() {
            super(LOGIN_AND_ATTRIBUTE_STORE, LOGIN_AND_ATTRIBUTE_STORE, DESCRIPTION);
        }

        @Override
        public String getValue() {
            return LOGIN_AND_ATTRIBUTE_STORE;
        }
    }
}
