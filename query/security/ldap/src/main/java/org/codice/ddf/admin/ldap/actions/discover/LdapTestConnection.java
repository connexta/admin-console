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
package org.codice.ddf.admin.ldap.actions.discover;

import java.util.List;

import org.codice.ddf.admin.api.fields.Field;
import org.codice.ddf.admin.common.actions.TestAction;
import org.codice.ddf.admin.common.fields.base.scalar.BooleanField;
import org.codice.ddf.admin.ldap.actions.commons.LdapConnectionAttempt;
import org.codice.ddf.admin.ldap.actions.commons.LdapTestingUtils;
import org.codice.ddf.admin.ldap.fields.connection.LdapConnectionField;

import com.google.common.collect.ImmutableList;

public class LdapTestConnection extends TestAction {

    public static final String NAME = "testConnect";

    public static final String DESCRIPTION =
            "Attempts to established a connection with the given connection configuration";

    private LdapConnectionField connection;
    private LdapTestingUtils utils;

    public LdapTestConnection() {
        super(NAME, DESCRIPTION);
        connection = new LdapConnectionField();
        utils = new LdapTestingUtils();
    }

    @Override
    public List<Field> getArguments() {
        return ImmutableList.of(connection);
    }

    // Possible message types: CANNOT_CONFIGURE, CANNOT_CONNECT
    @Override
    public BooleanField performAction() {
        LdapConnectionAttempt connectionAttempt = utils.getLdapConnection(connection);
        addReturnValueMessages(connectionAttempt.messages());
        return new BooleanField(connectionAttempt.connection().isPresent());
    }
}
