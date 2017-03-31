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
import org.codice.ddf.admin.security.common.fields.ldap.LdapConnectionField;

import com.google.common.collect.ImmutableList;

public class LdapTestConnectionField extends TestAction {

    public static final String NAME = "testConnect";

    public static final String DESCRIPTION =
            "Attempts to established a connection with the given connection configuration";

    private LdapConnectionField connection;

    public LdapTestConnectionField() {
        super(NAME, DESCRIPTION);
        connection = new LdapConnectionField();
    }

    @Override
    public List<Field> getArguments() {
        return ImmutableList.of(connection);
    }

    @Override
    public BooleanField performAction() {
        BooleanField testPassed = new BooleanField();
        testPassed.setValue(true);
        return testPassed;
    }
}
