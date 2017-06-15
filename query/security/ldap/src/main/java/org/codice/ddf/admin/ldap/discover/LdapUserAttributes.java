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
import org.codice.ddf.admin.api.fields.ListField;
import org.codice.ddf.admin.common.fields.base.BaseFunctionField;
import org.codice.ddf.admin.common.fields.base.ListFieldImpl;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;
import org.codice.ddf.admin.ldap.commons.LdapConnectionAttempt;
import org.codice.ddf.admin.ldap.commons.LdapTestingUtils;
import org.codice.ddf.admin.ldap.commons.ServerGuesser;
import org.codice.ddf.admin.ldap.fields.config.LdapSettingsField;
import org.codice.ddf.admin.ldap.fields.connection.LdapBindUserInfo;
import org.codice.ddf.admin.ldap.fields.connection.LdapConnectionField;
import org.codice.ddf.admin.ldap.fields.query.LdapTypeField;
import org.forgerock.opendj.ldap.LdapException;
import org.forgerock.opendj.ldap.SearchResultReferenceIOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

public class LdapUserAttributes extends BaseFunctionField<ListField<StringField>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(LdapUserAttributes.class);

    public static final String FIELD_NAME = "userAttributes";

    public static final String DESCRIPTION =
            "Retrieves a subset of available user attributes based on the LDAP settings provided.";

    private LdapConnectionField conn;

    private LdapBindUserInfo creds;

    private LdapSettingsField settings;

    private LdapTypeField ldapType;

    private LdapTestingUtils utils;

    public LdapUserAttributes() {
        super(FIELD_NAME, DESCRIPTION, new ListFieldImpl<>(StringField.class));
        conn = new LdapConnectionField().useDefaultRequired();
        creds = new LdapBindUserInfo().useDefaultRequired();
        settings = new LdapSettingsField().useDefaultAttributeStore();

        ldapType = new LdapTypeField();
        updateArgumentPaths();

        utils = new LdapTestingUtils();
    }

    @Override
    public List<DataType> getArguments() {
        return ImmutableList.of(conn, creds, settings, ldapType);
    }

    @Override
    public ListField<StringField> performFunction() {
        Set<String> ldapEntryAttributes;
        ServerGuesser serverGuesser;
        ListFieldImpl<StringField> entries = null;
        try (LdapConnectionAttempt connectionAttempt = utils.bindUserToLdapConnection(conn,
                creds)) {
            addMessages(connectionAttempt);

            if (containsErrorMsgs()) {
                return null;
            }

            ldapEntryAttributes = new HashSet<>();
            serverGuesser = ServerGuesser.buildGuesser(ldapType.getValue(),
                    connectionAttempt.result());
            try {
                ldapEntryAttributes = serverGuesser.getClaimAttributeOptions(settings.baseUserDn());

            } catch (SearchResultReferenceIOException | LdapException e) {
                LOGGER.warn("Error retrieving attributes from LDAP server; this may indicate a "
                        + "configuration issue with config.");
            }

            entries = new ListFieldImpl<>(StringField.class);
            entries.setValue(Arrays.asList(ldapEntryAttributes.toArray()));

        } catch (IOException e) {
            LOGGER.warn("Error closing LDAP connection", e);
        }

        return entries;
    }

    @Override
    public FunctionField<ListField<StringField>> newInstance() {
        return new LdapUserAttributes();
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
