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

import org.codice.ddf.admin.api.fields.Field;
import org.codice.ddf.admin.common.fields.base.BaseEnumField;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;

import com.google.common.collect.ImmutableList;

public class LdapRealm extends BaseEnumField<String> {

    public static final String DEFAULT_FIELD_NAME = "realm";

    public static final String FIELD_TYPE_NAME = "LdapRealm";

    public static final String DESCRIPTION =
            "The ldap realm performs authentication using information from an LDAP server. User information includes user name, password, and the groups to which the user belongs. To use an LDAP realm, the users and groups must already be defined in your LDAP directory";

    public static final String DIGEST_MD5_SASL = "DigestMD5SASL";

    public static final LdapBindMethod DIGEST_MD5_SASL_REALM_FIELD =
            new LdapBindMethod(new DigestMd5SaslRealm());

    public LdapRealm() {
        this(null);
    }

    // TODO: tbatie - 3/27/17 - Add constructor for supporting additional authtypes. Do this for all enum fields
    protected LdapRealm(Field<String> realm) {
        super(DEFAULT_FIELD_NAME,
                FIELD_TYPE_NAME,
                DESCRIPTION,
                ImmutableList.of(new DigestMd5SaslRealm()),
                realm);
    }

    protected static final class DigestMd5SaslRealm extends StringField {
        public static final String FIELD_NAME = DIGEST_MD5_SASL;

        public static final String FIELD_TYPE = DIGEST_MD5_SASL;

        // TODO: tbatie - 4/2/17 - find a description for this realm;
        public static final String DESCRIPTION = "";

        public DigestMd5SaslRealm() {
            super(FIELD_NAME, FIELD_TYPE, DESCRIPTION);
        }

        @Override
        public String getValue() {
            return DIGEST_MD5_SASL;
        }
    }
}
