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
import static org.codice.ddf.admin.ldap.commons.LdapMessages.noGroupsInBaseGroupDnError;
import static org.codice.ddf.admin.ldap.commons.LdapMessages.noGroupsWithMembersError;
import static org.codice.ddf.admin.ldap.commons.LdapMessages.noReferencedMemberError;
import static org.codice.ddf.admin.ldap.commons.LdapMessages.noUsersInBaseUserDnError;
import static org.codice.ddf.admin.ldap.commons.LdapMessages.userNameAttributeNotFoundError;
import static org.codice.ddf.admin.ldap.fields.config.LdapUseCase.ATTRIBUTE_STORE;
import static org.codice.ddf.admin.ldap.fields.config.LdapUseCase.AUTHENTICATION_AND_ATTRIBUTE_STORE;

import java.io.IOException;
import java.util.Arrays;
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
import org.forgerock.opendj.ldap.Connection;
import org.forgerock.opendj.ldap.Filter;
import org.forgerock.opendj.ldap.SearchScope;
import org.forgerock.opendj.ldap.responses.SearchResultEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

public class LdapTestSettings extends TestFunctionField {
    private static final Logger LOGGER = LoggerFactory.getLogger(LdapTestSettings.class);

    public static final String FIELD_NAME = "testSettings";

    public static final String DESCRIPTION =
            "Tests whether the given LDAP dn's and user attributes exist.";

    private LdapConnectionField conn;

    private LdapBindUserInfo bindInfo;

    private LdapSettingsField settings;

    private LdapTestingUtils utils;

    public LdapTestSettings() {
        super(FIELD_NAME, DESCRIPTION);
        conn = new LdapConnectionField().useDefaultRequired();
        bindInfo = new LdapBindUserInfo().useDefaultRequired();
        settings = new LdapSettingsField().useDefaultAuthentication();
        updateArgumentPaths();

        utils = new LdapTestingUtils();
    }

    @Override
    public List<DataType> getArguments() {
        return ImmutableList.of(conn, bindInfo, settings);
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

            checkDirExists(settings.baseGroupDnField(), ldapConnection);
            checkDirExists(settings.baseUserDnField(), ldapConnection);

            // Short-circuit return here, if either the user or group directory does not exist
            if (containsErrorMsgs()) {
                return new BooleanField(false);
            }

            checkUsersInDir(ldapConnection);

            // Short-circuit return here, if there are no users in base dir
            if (containsErrorMsgs()) {
                return new BooleanField(false);
            }

            // Check if group objectClass is on at least one entry in the directory
            checkGroupObjectClass(ldapConnection);

            // Don't check the group if there is no entry with the correct objectClass
            if (!containsErrorMsgs()) {
                // Then, check that there is a group entry (of the correct objectClass) that has
                // any member references
                checkGroup(ldapConnection);
            }
        } catch (IOException e) {
            LOGGER.warn("Error closing LDAP connection", e);
        }

        return new BooleanField(!containsErrorMsgs());
    }

    @Override
    public void validate() {
        if (settings.useCase() != null && (settings.useCase()
                .equals(ATTRIBUTE_STORE) || settings.useCase()
                .equals(AUTHENTICATION_AND_ATTRIBUTE_STORE))) {
            settings.useDefaultAttributeStore();
        }

        super.validate();
    }

    @Override
    public FunctionField<BooleanField> newInstance() {
        return new LdapTestSettings();
    }

    /**
     * Confirms that the dn exists
     *
     * @param ldapConnection
     */
    private void checkDirExists(LdapDistinguishedName dirDn, Connection ldapConnection) {
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
     * @param ldapConnection
     */
    private void checkUsersInDir(Connection ldapConnection) {
        List<SearchResultEntry> baseUsersResults = utils.getLdapQueryResults(ldapConnection,
                settings.baseUserDn(),
                Filter.present(settings.usernameAttribute())
                        .toString(),
                SearchScope.WHOLE_SUBTREE,
                1);

        if (baseUsersResults.isEmpty()) {
            addArgumentMessage(noUsersInBaseUserDnError(settings.baseUserDnField()
                    .path()));
            addArgumentMessage(userNameAttributeNotFoundError(settings.usernameAttributeField()
                    .path()));
        }
    }

    /**
     * Checks if the baseGroupDn contains the groupObjectclass
     *
     * @param ldapConnection
     */
    private void checkGroupObjectClass(Connection ldapConnection) {
        List<SearchResultEntry> baseGroupResults = utils.getLdapQueryResults(ldapConnection,
                settings.baseGroupDn(),
                Filter.equality("objectClass", settings.groupObjectClass())
                        .toString(),
                SearchScope.WHOLE_SUBTREE,
                1);

        if (baseGroupResults.isEmpty()) {
            addArgumentMessage(noGroupsInBaseGroupDnError(settings.baseGroupDnField()
                    .path()));
            addArgumentMessage(noGroupsInBaseGroupDnError(settings.groupObjectClassField()
                    .path()));
        }
    }

    private void checkGroup(Connection ldapConnection) {
        List<SearchResultEntry> groups = utils.getLdapQueryResults(ldapConnection,
                settings.baseGroupDn(),
                Filter.and(Filter.equality("objectClass", settings.groupObjectClass()),
                        Filter.present(settings.groupAttributeHoldingMember()))
                        .toString(),
                SearchScope.WHOLE_SUBTREE,
                1);

        if (groups.isEmpty()) {
            addArgumentMessage(noGroupsWithMembersError(settings.groupAttributeHoldingMemberField()
                    .path()));
        } else {
            checkReferencedUser(ldapConnection, groups.get(0));
        }
    }

    private void checkReferencedUser(Connection ldapConnection, SearchResultEntry group) {
        String memberRef = group.getAttribute(settings.groupAttributeHoldingMember())
                .firstValueAsString();
        // This memberRef will be in the format:
        // memberAttributeReferencedInGroup + username + baseUserDN
        // Strip the baseUserDN and query for the remainder as a Filter
        // beneath the baseUserDN
        List<String> split = Arrays.asList(memberRef.split(","));

        String userFilter = split.get(0);
        String checkUserBase = String.join(",", split.subList(1, split.size()));
        // Check that the userFilter is correctly formatted and that the expected userBase was
        // found in a matched group
        if (checkUserBase.equalsIgnoreCase(settings.baseUserDn())
                && userFilter.split("=")[0].equalsIgnoreCase(settings.memberAttributeReferencedInGroup())) {
            List<SearchResultEntry> foundMember = utils.getLdapQueryResults(ldapConnection,
                    settings.baseUserDn(),
                    userFilter,
                    SearchScope.WHOLE_SUBTREE,
                    1);

            if (foundMember.isEmpty()) {
                addArgumentMessage(noReferencedMemberError(settings.memberAttributeReferencedInGroupField()
                        .path()));
            }
        } else {
            addArgumentMessage(noReferencedMemberError(settings.memberAttributeReferencedInGroupField()
                    .path()));
        }
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
