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

import java.util.concurrent.Callable;

import org.codice.ddf.admin.api.fields.EnumValue;
import org.codice.ddf.admin.common.fields.base.BaseEnumField;
import org.codice.ddf.admin.common.fields.base.BaseListField;

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

    protected Realm(EnumValue<String> realm) {
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

    public static final class LdapRealm implements EnumValue<String> {
        public static final String LDAP = "ldap";

        public static final String DESCRIPTION =
                "An LDAP used for authentication of users within it's database.";

        @Override
        public String enumTitle() {
            return LDAP;
        }

        @Override
        public String description() {
            return DESCRIPTION;
        }

        @Override
        public String value() {
            return LDAP;
        }
    }

    public static final class KarafRealm implements EnumValue<String> {
        public static final String KARAF = "karaf";

        public static final String DESCRIPTION =
                "The default realm. The karaf realm authenticates against the users.properties file.";

        @Override
        public String enumTitle() {
            return KARAF;
        }

        @Override
        public String description() {
            return DESCRIPTION;
        }

        @Override
        public String value() {
            return KARAF;
        }
    }

    public static class Realms extends BaseListField<Realm> {

        public static final String DEFAULT_FIELD_NAME = "realms";

        public Realms() {
            super(DEFAULT_FIELD_NAME);
        }

        @Override
        public Callable<Realm> getCreateListEntryCallable() {
            return Realm::new;
        }
    }
}