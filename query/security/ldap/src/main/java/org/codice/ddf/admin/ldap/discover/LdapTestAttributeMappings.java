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

import static org.codice.ddf.admin.ldap.commons.LdapMessages.dnDoesNotExistError;
import static org.codice.ddf.admin.ldap.commons.LdapMessages.mappingAttributeNotFoundError;

import java.io.IOException;
import java.util.List;

import org.codice.ddf.admin.api.DataType;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.common.fields.base.function.TestFunctionField;
import org.codice.ddf.admin.common.fields.base.scalar.BooleanField;
import org.codice.ddf.admin.ldap.commons.LdapConnectionAttempt;
import org.codice.ddf.admin.ldap.commons.LdapTestingUtils;
import org.codice.ddf.admin.ldap.fields.LdapDistinguishedName;
import org.codice.ddf.admin.ldap.fields.config.LdapSettingsField;
import org.codice.ddf.admin.ldap.fields.connection.LdapBindUserInfo;
import org.codice.ddf.admin.ldap.fields.connection.LdapConnectionField;
import org.codice.ddf.admin.security.common.fields.wcpm.ClaimsMapEntry;
import org.forgerock.opendj.ldap.Connection;
import org.forgerock.opendj.ldap.Filter;
import org.forgerock.opendj.ldap.SearchScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

public class LdapTestAttributeMappings extends TestFunctionField {
    private static final Logger LOGGER = LoggerFactory.getLogger(LdapTestAttributeMappings.class);

    public static final String FIELD_NAME = "testAttributeFields";

    public static final String DESCRIPTION =
            "Tests whether the attributes mapped to claims exist on users beneath the user base.";

    private LdapConnectionField conn;

    private LdapBindUserInfo bindInfo;

    private LdapSettingsField settings;

    private LdapTestingUtils utils;

    public LdapTestAttributeMappings() {
        super(FIELD_NAME, DESCRIPTION);
        conn = new LdapConnectionField().useDefaultRequired();
        bindInfo = new LdapBindUserInfo().useDefaultRequired();
        settings = new LdapSettingsField().useDefaultUserAttributes();
        updateArgumentPaths();

        utils = new LdapTestingUtils();
    }

    @Override
    public List<DataType> getArguments() {
        return ImmutableList.of(conn, bindInfo, settings);
    }

    @Override
    public FunctionField<BooleanField> newInstance() {
        return new LdapTestAttributeMappings();
    }

    @Override
    public BooleanField performFunction() {
        try (LdapConnectionAttempt connectionAttempt = utils.bindUserToLdapConnection(conn,
                bindInfo)) {
            addMessages(connectionAttempt);

            if (containsErrorMsgs()) {
                return new BooleanField(false);
            }

            Connection ldapConnection = connectionAttempt.result();

            checkUserDirExists(ldapConnection);

            // Short-circuit return here, if either the user or group directory does not exist
            if (containsErrorMsgs()) {
                return new BooleanField(false);
            }

            settings.attributeMapField()
                    .getList()
                    .stream()
                    .map(ClaimsMapEntry::claimValueField)
                    .filter(claim -> !mappingAttributeFound(ldapConnection, claim.getValue()))
                    .forEach(claim -> addArgumentMessage(mappingAttributeNotFoundError(claim.path())));

            if (containsErrorMsgs()) {
                return new BooleanField(false);
            }
        } catch (IOException e) {
            LOGGER.warn("Error closing LDAP connection", e);
        }

        return new BooleanField(!containsErrorMsgs());
    }

    /**
     * Confirms that at least one user exists in the user base with the attribute provided
     *
     * @param ldapConnection connection used to query ldap
     */
    private void checkUserDirExists(Connection ldapConnection) {
        LdapDistinguishedName dirDn = settings.baseUserDnField();
        boolean dirExists = !utils.getLdapQueryResults(ldapConnection,
                dirDn.getValue(),
                Filter.present("objectClass")
                        .toString(),
                SearchScope.BASE_OBJECT,
                1)
                .isEmpty();

        if (!dirExists) {
            addArgumentMessage(dnDoesNotExistError(dirDn.path()));
        }
    }

    /**
     * Checks the baseUserDn for users.
     *
     * @param ldapConnection connection used to query ldap
     * @param mapAttribute
     */
    private boolean mappingAttributeFound(Connection ldapConnection, String mapAttribute) {
        return !utils.getLdapQueryResults(ldapConnection,
                settings.baseUserDn(),
                Filter.and(Filter.present(settings.usernameAttribute()),
                        Filter.present(mapAttribute))
                        .toString(),
                SearchScope.WHOLE_SUBTREE,
                1)
                .isEmpty();
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
