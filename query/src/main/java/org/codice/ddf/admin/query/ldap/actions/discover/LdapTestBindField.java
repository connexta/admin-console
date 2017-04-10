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
package org.codice.ddf.admin.query.ldap.actions.discover;

import java.util.List;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.commons.actions.TestAction;
import org.codice.ddf.admin.query.commons.fields.common.ReportField;
import org.codice.ddf.admin.query.commons.fields.common.message.FailureMessageField;
import org.codice.ddf.admin.query.commons.fields.common.message.MessageField;
import org.codice.ddf.admin.query.commons.fields.common.message.SuccessMessageField;
import org.codice.ddf.admin.query.commons.fields.common.message.WarningMessageField;
import org.codice.ddf.admin.query.ldap.fields.LdapConnectionField;
import org.codice.ddf.admin.query.ldap.fields.LdapCredentialsField;

import com.google.common.collect.ImmutableList;

public class LdapTestBindField extends TestAction {

    public static final String NAME = "testBind";
    public static final String DESCRIPTION = "Attempts to bind a user to the given ldap connection given the ldap bind user credentials.";

    private LdapConnectionField connection;
    private LdapCredentialsField credentials;

    public LdapTestBindField() {
        super(NAME, DESCRIPTION);
        connection = new LdapConnectionField();
        credentials = new LdapCredentialsField();
    }

    @Override
    public ReportField process() {
        MessageField successMsg = new SuccessMessageField("SUCCESS", "Able to bind user to connection.");
        MessageField warningMsg = new WarningMessageField("WARNING", "Unable to bind user to connection.");
        MessageField failureMsg = new FailureMessageField("CANNOT_BIND", "Failed to connect to the specified LDAP");
        return new ReportField().messages(successMsg, warningMsg, failureMsg);
    }

    @Override
    public List<Field> getArguments() {
        return ImmutableList.of(connection, credentials);
    }
}
