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

import org.codice.ddf.admin.common.fields.base.scalar.StringField;

public class LdapRealm extends StringField {
    public static final String DEFAULT_FIELD_NAME = "realm";

    public static final String FIELD_TYPE_NAME = "LdapRealm";

    public static final String DESCRIPTION =
            "The ldap realm performs authentication using information emptyReport an LDAP server. User information includes user name, password, and the groups to which the user belongs. To use an LDAP realm, the users and groups must already be defined in your LDAP directory";

    LdapRealm() {
        super(DEFAULT_FIELD_NAME);
    }
}
