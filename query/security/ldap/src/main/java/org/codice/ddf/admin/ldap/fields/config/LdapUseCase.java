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

import org.codice.ddf.admin.api.DataType;
import org.codice.ddf.admin.common.fields.base.BaseEnumField;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;

import com.google.common.collect.ImmutableList;

public class LdapUseCase extends BaseEnumField<String> {
    public static final String DEFAULT_FIELD_NAME = "useCase";

    public static final String FIELD_TYPE_NAME = "LdapUseCase";

    public static final String DESCRIPTION = "Describes the intended use of the LDAP settings.";

    public static final String AUTHENTICATION = "Authentication";

    public static final String ATTRIBUTE_STORE = "AttributeStore";

    public static final String AUTHENTICATION_AND_ATTRIBUTE_STORE =
            "AuthenticationAndAttributeStore";

    public LdapUseCase() {
        this(null);
    }

    private LdapUseCase(DataType<String> bindMethod) {
        super(DEFAULT_FIELD_NAME,
                FIELD_TYPE_NAME,
                DESCRIPTION,
                ImmutableList.of(new Authentication(),
                        new AttributeStore(),
                        new AuthenticationAndAttributeStore()),
                bindMethod);
    }

    protected static final class Authentication extends StringField {

        public static final String DESCRIPTION =
                "Indicates the LDAP is intended to be used as a source to login into.";

        Authentication() {
            super(AUTHENTICATION, AUTHENTICATION, DESCRIPTION);
        }

        @Override
        public String getValue() {
            return AUTHENTICATION;
        }
    }

    protected static final class AttributeStore extends StringField {
        public static final String DESCRIPTION =
                "Indicates the LDAP is intended to be used as store for retrieving attributes of entries.";

        AttributeStore() {
            super(ATTRIBUTE_STORE, ATTRIBUTE_STORE, DESCRIPTION);
        }

        @Override
        public String getValue() {
            return ATTRIBUTE_STORE;
        }
    }

    protected static final class AuthenticationAndAttributeStore extends StringField {
        public static final String DESCRIPTION =
                "Indicates the LDAP is intended to be used as both a source of login and a store for retrieving attributes of entries.";

        AuthenticationAndAttributeStore() {
            super(AUTHENTICATION_AND_ATTRIBUTE_STORE,
                    AUTHENTICATION_AND_ATTRIBUTE_STORE,
                    DESCRIPTION);
        }

        @Override
        public String getValue() {
            return AUTHENTICATION_AND_ATTRIBUTE_STORE;
        }
    }
}
