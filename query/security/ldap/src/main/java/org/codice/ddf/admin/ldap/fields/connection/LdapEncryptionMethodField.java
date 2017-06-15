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

public class LdapEncryptionMethodField extends BaseEnumField<String> {

    public static final String DEFAULT_FIELD_NAME = "encryption";

    public static final String FIELD_TYPE_NAME = "EncryptionMethod";

    public static final String DESCRIPTION =
            "All possible encryption methods supported to establish an LDAP connection.";

    public static final String NONE = "none";

    public static final String LDAPS = "ldaps";

    public static final String START_TLS = "startTls";

    public LdapEncryptionMethodField() {
        this(null);
    }

    protected LdapEncryptionMethodField(DataType<String> encryptionMethod) {
        super(DEFAULT_FIELD_NAME,
                FIELD_TYPE_NAME,
                DESCRIPTION,
                ImmutableList.of(new NoEncryption(),
                        new LdapsEncryption(),
                        new StartTlsEncryption()),
                encryptionMethod);
    }

    protected static final class NoEncryption extends StringField {
        public static final String DESCRIPTION = "No encryption enabled for LDAP connection";

        public NoEncryption() {
            super(NONE, NONE, DESCRIPTION);
        }

        @Override
        public String getValue() {
            return NONE;
        }
    }

    protected static final class LdapsEncryption extends StringField {

        public static final String DESCRIPTION = "Secure LDAPS encryption.";

        public LdapsEncryption() {
            super(LDAPS, LDAPS, DESCRIPTION);
        }

        @Override
        public String getValue() {
            return LDAPS;
        }
    }

    protected static final class StartTlsEncryption extends StringField {
        public static final String DESCRIPTION =
                "Attempts to upgrade a non encrypted connection to LDAPS.";

        public StartTlsEncryption() {
            super(START_TLS, START_TLS, DESCRIPTION);
        }

        @Override
        public String getValue() {
            return START_TLS;
        }
    }

}
