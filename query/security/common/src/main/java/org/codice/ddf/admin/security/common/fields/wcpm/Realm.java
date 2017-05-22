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
package org.codice.ddf.admin.security.common.fields.wcpm;

import org.codice.ddf.admin.api.DataType;
import org.codice.ddf.admin.common.fields.base.BaseEnumField;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;

import com.google.common.collect.ImmutableList;

public class Realm extends BaseEnumField<String> {

    public static final String DEFAULT_FIELD_NAME = "realm";

    public static final String FIELD_TYPE_NAME = "Realm";

    public static final String DESCRIPTION =
            "Authenticating Realms are used to authenticate an incoming authentication token and create a Subject on successful authentication.";

    public static final Realm LDAP_REALM = new Realm(new LdapRealm());

    public static final Realm KARAF_REALM = new Realm(new KarafRealm());

    public Realm() {
        this(null);
    }

    protected Realm(DataType<String> realm) {
        super(DEFAULT_FIELD_NAME,
                FIELD_TYPE_NAME,
                DESCRIPTION,
                ImmutableList.of(new LdapRealm(), new KarafRealm()),
                realm);
    }

    @Override
    public Realm isRequired(boolean required) {
        super.isRequired(required);
        return this;
    }

    protected static final class LdapRealm extends StringField {
        public static final String LDAP = "ldap";

        public static final String DESCRIPTION =
                "An LDAP used for authentication of users within it's database.";

        public LdapRealm() {
            super(LDAP, LDAP, DESCRIPTION);
        }

        @Override
        public String getValue() {
            return LDAP;
        }
    }

    protected static final class KarafRealm extends StringField {
        public static final String KARAF = "karaf";

        public static final String DESCRIPTION =
                "The default realm. The karaf realm authenticates against the users.properties file.";

        public KarafRealm() {
            super(KARAF, KARAF, DESCRIPTION);
        }

        @Override
        public String getValue() {
            return KARAF;
        }
    }
}