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
import static org.codice.ddf.admin.ldap.commons.LdapMessages.noGroupsWithMembersWarning;
import static org.codice.ddf.admin.ldap.commons.LdapMessages.noReferencedMemberWarning;
import static org.codice.ddf.admin.ldap.commons.LdapMessages.noUsersInBaseUserDnError;
import static org.codice.ddf.admin.ldap.commons.LdapMessages.userNameAttributeNotFoundWarning;
import static org.codice.ddf.admin.ldap.fields.config.LdapUseCase.ATTRIBUTE_STORE;
import static org.codice.ddf.admin.ldap.fields.config.LdapUseCase.AUTHENTICATION_AND_ATTRIBUTE_STORE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import org.codice.ddf.admin.api.DataType;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.api.report.ErrorMessage;
import org.codice.ddf.admin.common.fields.base.function.TestFunctionField;
import org.codice.ddf.admin.common.fields.base.scalar.BooleanField;
import org.codice.ddf.admin.ldap.commons.LdapConnectionAttempt;
import org.codice.ddf.admin.ldap.commons.LdapTestingUtils;
import org.codice.ddf.admin.ldap.fields.config.LdapSettingsField;
import org.codice.ddf.admin.ldap.fields.connection.LdapBindUserInfo;
import org.codice.ddf.admin.ldap.fields.connection.LdapConnectionField;
import org.forgerock.opendj.ldap.Connection;
import org.forgerock.opendj.ldap.Filter;
import org.forgerock.opendj.ldap.SearchScope;
import org.forgerock.opendj.ldap.responses.SearchResultEntry;

import com.google.common.collect.ImmutableList;

public class LdapTestSettings extends TestFunctionField {
    public static final String ID = "testLdapSettings";

    public static final String DESCRIPTION =
            "Tests whether the given LDAP dn's and user attributes exist.";

    private LdapConnectionField conn;

    private LdapBindUserInfo bindInfo;

    private LdapSettingsField settings;

    private LdapTestingUtils utils;

    public LdapTestSettings() {
        super(ID, DESCRIPTION);
        conn = new LdapConnectionField().useDefaultRequired();
        bindInfo = new LdapBindUserInfo().useDefaultRequired();
        settings = new LdapSettingsField();

        settings.useCaseField()
                .isRequired(true);
        settings.usernameAttributeField()
                .isRequired(true);
        settings.baseUserDnField()
                .isRequired(true);
        settings.baseGroupDnField()
                .isRequired(true);

        updateArgumentPaths();

        utils = new LdapTestingUtils();
    }

    @Override
    public List<DataType> getArguments() {
        return ImmutableList.of(conn, bindInfo, settings);
    }

    @Override
    public BooleanField performFunction() {
        LdapConnectionAttempt connectionAttempt = utils.bindUserToLdapConnection(conn, bindInfo);
        addResultMessages(connectionAttempt.messages());
        addArgumentMessages(connectionAttempt.argumentMessages());

        if (!connectionAttempt.connection()
                .isPresent()) {
            return new BooleanField(false);
        }

        Connection ldapConnection = connectionAttempt.connection()
                .get();

        if (!checkDirExists(settings.baseGroupDn(), ldapConnection)) {
            addArgumentMessage(dnDoesNotExistError(settings.baseGroupDnField()
                    .path()));
        }
        if (!checkDirExists(settings.baseUserDn(), ldapConnection)) {
            addArgumentMessage(dnDoesNotExistError(settings.baseUserDnField()
                    .path()));
        }
        // Short-circuit return here, if either the user or group directory does not exist
        if (containsErrorMsgs()) {
            return new BooleanField(false);
        }

        addArgumentMessages(checkUsersInDir(settings, ldapConnection));

        // Short-circuit return here, if there are no users in base dir
        if (containsErrorMsgs()) {
            return new BooleanField(false);
        }

        // Check if group objectClass is on at least one entry in the directory
        addArgumentMessages(checkGroupObjectClass(settings, ldapConnection));

        // Don't check the group if there is no entry with the correct objectClass
        if (!containsErrorMsgs()) {
            // Then, check that there is a group entry (of the correct objectClass) that has
            // any member references
            addArgumentMessages(checkGroup(settings, ldapConnection));
        }

        return new BooleanField(!containsErrorMsgs());
    }

    /**
     * Confirms that the baseUserDn exists.
     *
     * @param ldapConnection
     * @return
     */
    private boolean checkDirExists(String dirDn, Connection ldapConnection) {
        return !utils.getLdapQueryResults(ldapConnection,
                dirDn,
                Filter.present("objectClass")
                        .toString(),
                SearchScope.BASE_OBJECT,
                1)
                .isEmpty();
    }

    /**
     * Checks the baseUserDn for users.
     * <p>
     * Possible message return types: NO_USERS_IN_BASE_USER_DN, USER_NAME_ATTRIBUTE_NOT_FOUND
     *
     * @param ldapSettings
     * @param ldapConnection
     * @return
     */
    private List<ErrorMessage> checkUsersInDir(LdapSettingsField ldapSettings,
            Connection ldapConnection) {
        List<ErrorMessage> errors = new ArrayList<>();
        List<SearchResultEntry> baseUsersResults = utils.getLdapQueryResults(ldapConnection,
                ldapSettings.baseUserDn(),
                Filter.present(ldapSettings.usernameAttribute())
                        .toString(),
                SearchScope.WHOLE_SUBTREE,
                1);

        if (baseUsersResults.isEmpty()) {
            // TODO: tbatie - 5/3/17 - This first error looks incorrect
            errors.add(noUsersInBaseUserDnError(ldapSettings.baseUserDnField()
                    .path()));
            errors.add(userNameAttributeNotFoundWarning(ldapSettings.usernameAttributeField()
                    .path()));
        }

        return errors;
    }

    /**
     * Checks if the baseGroupDn contains the groupObjectclass
     * <p>
     * Possible message return types:
     *
     * @param settings
     * @param ldapConnection
     */
    List<ErrorMessage> checkGroupObjectClass(LdapSettingsField settings, Connection ldapConnection) {
        List<Message> errors = new ArrayList<>();
        List<SearchResultEntry> baseGroupResults = utils.getLdapQueryResults(ldapConnection,
                settings.baseGroupDn(),
                Filter.equality("objectClass", settings.groupObjectClass())
                        .toString(),
                SearchScope.WHOLE_SUBTREE,
                1);

        if (baseGroupResults.isEmpty()) {
            // TODO: tbatie - 4/18/17 - I think we should return back a general error rather than givving the same error for multiple fields
            errors.add(noGroupsInBaseGroupDnError(settings.baseGroupDnField()
                    .path()));
            errors.add(noGroupsInBaseGroupDnError(settings.groupObjectClassField()
                    .path()));
        }

        return errors;
    }

    List<ErrorMessage> checkGroup(LdapSettingsField settings, Connection ldapConnection) {
        List<Message> errors = new ArrayList<>();
        List<SearchResultEntry> groups = utils.getLdapQueryResults(ldapConnection,
                settings.baseGroupDn(),
                Filter.and(Filter.equality("objectClass", settings.groupObjectClass()),
                        Filter.present(settings.groupAttributeHoldingMember()))
                        .toString(),
                SearchScope.WHOLE_SUBTREE,
                1);

        if (groups.isEmpty()) {
            // TODO: tbatie - 5/3/17 - There needs to be a different message for this
            errors.add(noGroupsWithMembersWarning(settings.groupAttributeHoldingMemberField()
                    .path()));
        } else {
            errors.addAll(checkReferencedUser(settings, ldapConnection, groups.get(0)));
        }

        return errors;
    }

    List<ErrorMessage> checkReferencedUser(LdapSettingsField settings, Connection ldapConnection,
            SearchResultEntry group) {
        List<ErrorMessage> errors = new ArrayList<>();
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
                errors.add(noReferencedMemberWarning(settings.memberAttributeReferencedInGroupField()
                        .path()));
            }
        } else {
            errors.add(noReferencedMemberWarning(settings.memberAttributeReferencedInGroupField()
                    .path()));
        }

        return errors;
    }

    @Override
    public void validate() {
        super.validate();

        if (settings.useCase() != null && (settings.useCase()
                .equals(ATTRIBUTE_STORE) || settings.useCase()
                .equals(AUTHENTICATION_AND_ATTRIBUTE_STORE))) {

            Stream.of(settings.groupObjectClassField()
                            .isRequired(true),
                    settings.groupAttributeHoldingMemberField()
                            .isRequired(true),
                    settings.memberAttributeReferencedInGroupField()
                            .isRequired(true),
                    settings.attributeMapField()
                            .isRequired(true))
                    .map(DataType::validate)
                    .flatMap(Collection::stream)
                    .forEach(this::addArgumentMessage);

        }
    }

    @Override
    public FunctionField<BooleanField> newInstance() {
        return new LdapTestSettings();
    }

    public void setTestingUtils(LdapTestingUtils utils) {
        this.utils = utils;
    }
}
