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
package org.codice.ddf.admin.security.common.fields.ldap;

import java.util.List;

import org.codice.ddf.admin.api.fields.Field;
import org.codice.ddf.admin.common.fields.base.BaseObjectField;
import org.codice.ddf.admin.common.fields.common.HostnameField;
import org.codice.ddf.admin.common.fields.common.PortField;

import com.google.common.collect.ImmutableList;

public class LdapConnectionField extends BaseObjectField {
    public static final String FIELD_NAME = "connection";

    public static final String FIELD_TYPE_NAME = "LdapConnection";

    public static final String DESCRIPTION =
            "Contains the required information to establish an LDAP connection.";

    private HostnameField hostname;

    private PortField port;

    private LdapEncryptionMethodField encryptionMethod;

    public LdapConnectionField() {
        super(FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
        hostname = new HostnameField();
        port = new PortField();
        encryptionMethod = new LdapEncryptionMethodField();
    }

    public LdapConnectionField hostname(String hostname) {
        this.hostname.setValue(hostname);
        return this;
    }

    public LdapConnectionField port(int port) {
        this.port.setValue(port);
        return this;
    }

    public LdapConnectionField encryptionMethod(String encryptionMethod) {
        this.encryptionMethod.setValue(encryptionMethod);
        return this;
    }

    @Override
    public List<Field> getFields() {
        return ImmutableList.of(hostname, port, encryptionMethod);
    }
}
