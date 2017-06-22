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

import org.codice.ddf.admin.api.fields.EnumValue;
import org.codice.ddf.admin.common.fields.base.BaseEnumField;

import com.google.common.collect.ImmutableList;

public class LdapBindMethod extends BaseEnumField<String> {
    public static final String DEFAULT_FIELD_NAME = "bindMethod";

    public static final String FIELD_TYPE_NAME = "BindMethod";

    public static final String DESCRIPTION = "The method of binding a user to the LDAP connection.";

    LdapBindMethod() {
        this(null);
    }

    private LdapBindMethod(EnumValue<String> bindMethod) {
        super(DEFAULT_FIELD_NAME,
                FIELD_TYPE_NAME,
                DESCRIPTION,
                ImmutableList.of(new Simple(), new DigestMd5Sasl()),
                bindMethod);
    }

    public static final class Simple implements EnumValue<String> {
        public static final String SIMPLE = "Simple";

        public static final String DESCRIPTION =
                "Authenticates a client to a server, using a plaintext password";

        @Override
        public String enumTitle() {
            return SIMPLE;
        }

        @Override
        public String description() {
            return DESCRIPTION;
        }

        @Override
        public String value() {
            return SIMPLE;
        }
    }

    public static final class DigestMd5Sasl implements EnumValue<String> {
        public static final String DIGEST_MD5_SASL = "DigestMD5SASL";

        public static final String DESCRIPTION =
                "Allows for password-based authentication without exposing the password in the clear (although it does require that both the client and the server have access to the clear-text password).";

        @Override
        public String enumTitle() {
            return DIGEST_MD5_SASL;
        }

        @Override
        public String description() {
            return DESCRIPTION;
        }

        @Override
        public String value() {
            return DIGEST_MD5_SASL;
        }
    }
}
