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
import org.codice.ddf.admin.common.fields.common.ReportField;
import org.codice.ddf.admin.common.fields.common.message.FailureMessageField;
import org.codice.ddf.admin.common.fields.common.message.MessageField;
import org.codice.ddf.admin.common.fields.common.message.SuccessMessageField;
import org.codice.ddf.admin.common.fields.common.message.WarningMessageField;
import org.codice.ddf.admin.security.common.fields.ldap.LdapSettingsField;

import com.google.common.collect.ImmutableList;

public class LdapTestSettingsField extends TestAction {
    public static final String NAME = "testSettings";
    public static final String DESCRIPTION = "Tests whether the given LDAP dn's and user attributes exist.";

    private LdapSettingsField settings;

    public LdapTestSettingsField() {
        super(NAME, DESCRIPTION);
        settings = new LdapSettingsField();
    }

    @Override
    public ReportField process() {
        MessageField successMsg = new SuccessMessageField("SUCCESS", "All fields have been successfully validated.");
        MessageField warningMsg = new WarningMessageField("WARNING", "No users in baseDN with the given attributes");
        MessageField failureMsg = new FailureMessageField("CANNOT_BIND", "The specified user DN does not exist.");
        return new ReportField().messages(successMsg, warningMsg, failureMsg);
    }

    @Override
    public List<Field> getArguments() {
        return ImmutableList.of(settings);
    }
}
