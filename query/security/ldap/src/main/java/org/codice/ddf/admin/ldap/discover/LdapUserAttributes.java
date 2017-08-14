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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.codice.ddf.admin.api.DataType;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.common.fields.base.BaseFunctionField;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;
import org.codice.ddf.admin.common.report.message.DefaultMessages;
import org.codice.ddf.admin.ldap.commons.LdapConnectionAttempt;
import org.codice.ddf.admin.ldap.commons.LdapMessages;
import org.codice.ddf.admin.ldap.commons.LdapTestingUtils;
import org.codice.ddf.admin.ldap.commons.ServerGuesser;
import org.codice.ddf.admin.ldap.fields.LdapDistinguishedName;
import org.codice.ddf.admin.ldap.fields.connection.LdapBindUserInfo;
import org.codice.ddf.admin.ldap.fields.connection.LdapConnectionField;
import org.forgerock.opendj.ldap.LdapException;
import org.forgerock.opendj.ldap.SearchResultReferenceIOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class LdapUserAttributes extends BaseFunctionField<StringField.ListImpl> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LdapUserAttributes.class);

    public static final String FIELD_NAME = "userAttributes";

    public static final String DESCRIPTION =
            "Retrieves a subset of available user attributes based on the LDAP settings provided.";

    public static final String BASE_USER_DN = "baseUserDn";

    public static final StringField.ListImpl RETURN_TYPE =
            new StringField.ListImpl();

    private LdapConnectionField conn;

    private LdapBindUserInfo bindInfo;

    private LdapDistinguishedName baseUserDn;

    private LdapTestingUtils utils;

    public LdapUserAttributes() {
        super(FIELD_NAME, DESCRIPTION);
        conn = new LdapConnectionField().useDefaultRequired();
        bindInfo = new LdapBindUserInfo().useDefaultRequired();
        baseUserDn = new LdapDistinguishedName(BASE_USER_DN);
        baseUserDn.isRequired(true);
        updateArgumentPaths();

        utils = new LdapTestingUtils();
    }

    @Override
    public List<DataType> getArguments() {
        return ImmutableList.of(conn, bindInfo, baseUserDn);
    }

    @Override
    public StringField.ListImpl performFunction() {
        StringField.ListImpl entries = null;
        try (LdapConnectionAttempt connectionAttempt = utils.bindUserToLdapConnection(conn,
                bindInfo)) {
            addReportMessages(connectionAttempt);

            if (containsErrorMsgs()) {
                return null;
            }

            Set<String> ldapEntryAttributes = new HashSet<>();
            ServerGuesser serverGuesser = ServerGuesser.buildGuesser(connectionAttempt.getResult());
            try {
                ldapEntryAttributes = serverGuesser.getClaimAttributeOptions(baseUserDn.getValue());

            } catch (SearchResultReferenceIOException | LdapException e) {
                LOGGER.warn("Error retrieving attributes from LDAP server; this may indicate a "
                        + "configuration issue with config.");
            }

            entries = new StringField.ListImpl();
            entries.setValue(Arrays.asList(ldapEntryAttributes.toArray()));

        } catch (IOException e) {
            LOGGER.warn("Error closing LDAP connection", e);
        }

        return entries;
    }

    @Override
    public StringField.ListImpl getReturnType() {
        return RETURN_TYPE;
    }

    @Override
    public FunctionField<StringField.ListImpl> newInstance() {
        return new LdapUserAttributes();
    }

    @Override
    public Set<String> getFunctionErrorCodes() {
        return ImmutableSet.of(LdapMessages.CANNOT_BIND,
                DefaultMessages.FAILED_TEST_SETUP,
                DefaultMessages.CANNOT_CONNECT);
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
