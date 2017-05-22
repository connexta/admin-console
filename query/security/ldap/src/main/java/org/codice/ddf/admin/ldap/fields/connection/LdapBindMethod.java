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

import org.codice.ddf.admin.api.DataType;
import org.codice.ddf.admin.common.fields.base.BaseEnumField;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;

import com.google.common.collect.ImmutableList;

public class LdapBindMethod extends BaseEnumField<String> {

    public static final String DEFAULT_FIELD_NAME = "bindMethod";

    public static final String FIELD_TYPE_NAME = "BindMethod";

    public static final String DESCRIPTION = "The method of binding a user to the LDAP connection.";

    public static final String SIMPLE = "Simple";

    public static final String DIGEST_MD5_SASL = "DigestMD5SASL";

    //  These fields are not currently supported for binding
    //  public static final String SASL = "SASL";
    //  public static final String GSSAPI_SASL = "GSSAPI SASL";

    public static final LdapBindMethod SIMPLE_BIND_FIELD = new LdapBindMethod(new Simple());

    public static final LdapBindMethod DIGEST_MD5_SASL_FIELD =
            new LdapBindMethod(new DigestMd5Sasl());

    public LdapBindMethod() {
        this(null);
    }

    // TODO: tbatie - 3/27/17 - Add constructor for supporting additional authtypes. Do this for all enum fields
    protected LdapBindMethod(DataType<String> bindMethod) {
        super(DEFAULT_FIELD_NAME,
                FIELD_TYPE_NAME,
                DESCRIPTION,
                ImmutableList.of(new Simple(), new DigestMd5Sasl()),
                bindMethod);
    }

    protected static final class Simple extends StringField {
        public static final String FIELD_NAME = SIMPLE;

        public static final String FIELD_TYPE = SIMPLE;

        public static final String DESCRIPTION =
                "Authenticates a client to a server, using a plaintext password";

        public Simple() {
            super(FIELD_NAME, FIELD_TYPE, DESCRIPTION);
        }

        @Override
        public String getValue() {
            return SIMPLE;
        }
    }

    protected static final class DigestMd5Sasl extends StringField {
        public static final String FIELD_NAME = DIGEST_MD5_SASL;

        public static final String FIELD_TYPE = DIGEST_MD5_SASL;

        public static final String DESCRIPTION =
                "Allows for password-based authentication without exposing the password in the clear (although it does require that both the client and the server have access to the clear-text password).";

        public DigestMd5Sasl() {
            super(FIELD_NAME, FIELD_TYPE, DESCRIPTION);
        }

        @Override
        public String getValue() {
            return DIGEST_MD5_SASL;
        }
    }
}
