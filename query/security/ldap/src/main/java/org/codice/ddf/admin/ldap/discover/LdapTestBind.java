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
package org.codice.ddf.admin.ldap.discover;

import java.io.IOException;
import java.util.List;

import org.codice.ddf.admin.api.DataType;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.common.fields.base.function.TestFunctionField;
import org.codice.ddf.admin.common.fields.base.scalar.BooleanField;
import org.codice.ddf.admin.ldap.commons.LdapConnectionAttempt;
import org.codice.ddf.admin.ldap.commons.LdapTestingUtils;
import org.codice.ddf.admin.ldap.fields.connection.LdapBindUserInfo;
import org.codice.ddf.admin.ldap.fields.connection.LdapConnectionField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

public class LdapTestBind extends TestFunctionField {
    private static final Logger LOGGER = LoggerFactory.getLogger(LdapTestBind.class);

    public static final String FIELD_NAME = "testBind";

    public static final String DESCRIPTION =
            "Attempts to bind a user to the given ldap connection given the ldap bind user credentials.";

    private LdapConnectionField conn;

    private LdapBindUserInfo creds;

    private LdapTestingUtils utils;

    public LdapTestBind() {
        super(FIELD_NAME, DESCRIPTION);
        conn = new LdapConnectionField().useDefaultRequired();
        creds = new LdapBindUserInfo().useDefaultRequired();
        updateArgumentPaths();

        utils = new LdapTestingUtils();
    }

    @Override
    public List<DataType> getArguments() {
        return ImmutableList.of(conn, creds);
    }

    // Possible message types: CANNOT_CONFIGURE, CANNOT_CONNECT, CANNOT_BIND
    @Override
    public BooleanField performFunction() {
        try (LdapConnectionAttempt ldapConnectionAttempt = utils.bindUserToLdapConnection(conn,
                creds)) {
            addMessages(ldapConnectionAttempt);
        } catch (IOException e) {
            LOGGER.warn("Error closing LDAP connection", e);
        }
        return new BooleanField(!containsErrorMsgs());
    }

    @Override
    public FunctionField<BooleanField> newInstance() {
        return new LdapTestBind();
    }

    /**
     * Intentionally scoped as private.
     * This is a test support method to be invoked by Spock tests which will elevate scope as needed
     * in order to execute. If Java-based unit tests are ever needed, this scope will need to be
     * updated to package-private.
     *
     * @param utils Ldap support utilities
     */
    private void setTestingUtils(LdapTestingUtils utils) {
        this.utils = utils;
    }
}
