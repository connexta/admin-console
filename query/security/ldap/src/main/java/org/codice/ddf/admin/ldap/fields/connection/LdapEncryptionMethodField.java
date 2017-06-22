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

public class LdapEncryptionMethodField extends BaseEnumField<String> {

    public static final String DEFAULT_FIELD_NAME = "encryption";

    public static final String FIELD_TYPE_NAME = "EncryptionMethod";

    public static final String DESCRIPTION =
            "All possible encryption methods supported to establish an LDAP connection.";

    public LdapEncryptionMethodField() {
        this(null);
    }

    protected LdapEncryptionMethodField(EnumValue<String> encryptionMethod) {
        super(DEFAULT_FIELD_NAME,
                FIELD_TYPE_NAME,
                DESCRIPTION,
                ImmutableList.of(new NoEncryption(),
                        new LdapsEncryption(),
                        new StartTlsEncryption()),
                encryptionMethod);
    }

    public static final class NoEncryption implements EnumValue<String> {
        public static final String DESCRIPTION = "No encryption enabled for LDAP connection";

        public static final String NONE = "none";

        @Override
        public String enumTitle() {
            return NONE;
        }

        @Override
        public String description() {
            return DESCRIPTION;
        }

        @Override
        public String value() {
            return NONE;
        }
    }

    public static final class LdapsEncryption implements EnumValue<String> {

        public static final String DESCRIPTION = "Secure LDAPS encryption.";

        public static final String LDAPS = "ldaps";

        @Override
        public String enumTitle() {
            return LDAPS;
        }

        @Override
        public String description() {
            return DESCRIPTION;
        }

        @Override
        public String value() {
            return LDAPS;
        }
    }

    public static final class StartTlsEncryption implements EnumValue<String> {
        public static final String DESCRIPTION =
                "Attempts to upgrade a non encrypted connection to LDAPS.";

        public static final String START_TLS = "startTls";

        @Override
        public String enumTitle() {
            return START_TLS;
        }

        @Override
        public String description() {
            return DESCRIPTION;
        }

        @Override
        public String value() {
            return START_TLS;
        }
    }
}
