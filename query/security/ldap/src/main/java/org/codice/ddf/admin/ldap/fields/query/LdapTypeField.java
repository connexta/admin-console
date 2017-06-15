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
package org.codice.ddf.admin.ldap.fields.query;

import org.codice.ddf.admin.api.DataType;
import org.codice.ddf.admin.common.fields.base.BaseEnumField;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;

import com.google.common.collect.ImmutableList;

public class LdapTypeField extends BaseEnumField<String> {
    public static final String DEFAULT_FIELD_NAME = "type";

    public static final String FIELD_TYPE_NAME = "LdapType";

    public static final String DESCRIPTION = "The type of LDAP being connected to.";

    static final String UNKNOWN = "unknown";

    static final String ACTIVE_DIRECTORY = "activeDirectory";

    static final String OPEN_LDAP = "openLdap";

    static final String OPEN_DJ = "openDj";

    static final String EMBEDDED = "embeddedLdap";

    public LdapTypeField() {
        this(null);
    }

    private LdapTypeField(DataType<String> ldapType) {
        super(DEFAULT_FIELD_NAME,
                FIELD_TYPE_NAME,
                DESCRIPTION,
                ImmutableList.of(new Unknown(),
                        new ActiveDirectory(),
                        new OpenLdap(),
                        new OpenDJ(),
                        new EmbeddedLdap()),
                ldapType);
    }

    protected static final class Unknown extends StringField {
        public static final String DESCRIPTION =
                "Use if the type of LDAP is unknown/is not listed in this enum set.";

        Unknown() {
            super(UNKNOWN, UNKNOWN, DESCRIPTION);
        }

        @Override
        public String getValue() {
            return UNKNOWN;
        }
    }

    protected static final class ActiveDirectory extends StringField {
        public static final String DESCRIPTION =
                "Active Directory (AD) is a directory service that Microsoft developed for Windows domain networks.";

        ActiveDirectory() {
            super(ACTIVE_DIRECTORY, ACTIVE_DIRECTORY, DESCRIPTION);
        }

        @Override
        public String getValue() {
            return ACTIVE_DIRECTORY;
        }
    }

    protected static final class OpenLdap extends StringField {
        public static final String DESCRIPTION =
                "OpenLDAP is a free, open source implementation of the Lightweight Directory Access Protocol (LDAP) developed by the OpenLDAP Project.";

        OpenLdap() {
            super(OPEN_LDAP, OPEN_LDAP, DESCRIPTION);
        }

        @Override
        public String getValue() {
            return OPEN_LDAP;
        }
    }

    protected static final class OpenDJ extends StringField {
        public static final String DESCRIPTION =
                "OpenDJ is a directory server which implements a wide range of Lightweight Directory Access Protocol and related standards, including full compliance with LDAPv3 but also support for Directory Service Markup Language (DSMLv2).";

        OpenDJ() {
            super(OPEN_DJ, OPEN_DJ, DESCRIPTION);
        }

        @Override
        public String getValue() {
            return OPEN_DJ;
        }
    }

    protected static final class EmbeddedLdap extends StringField {
        public static final String DESCRIPTION =
                "The Embedded LDAP application is an internal LDAP server that has a default set of schemas and users loaded to help facilitate authentication and authorization testing.";

        EmbeddedLdap() {
            super(EMBEDDED, EMBEDDED, DESCRIPTION);
        }

        @Override
        public String getValue() {
            return EMBEDDED;
        }
    }
}
