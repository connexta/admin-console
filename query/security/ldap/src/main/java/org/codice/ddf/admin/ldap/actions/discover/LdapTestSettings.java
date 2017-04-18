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

import static org.codice.ddf.admin.ldap.actions.commons.LdapMessages.BASE_GROUP_DN_NOT_FOUND;
import static org.codice.ddf.admin.ldap.actions.commons.LdapMessages.BASE_USER_DN_NOT_FOUND;
import static org.codice.ddf.admin.ldap.actions.commons.LdapMessages.NO_GROUPS_IN_BASE_GROUP_DN;
import static org.codice.ddf.admin.ldap.actions.commons.LdapMessages.NO_GROUPS_WITH_MEMBERS;
import static org.codice.ddf.admin.ldap.actions.commons.LdapMessages.NO_REFERENCED_MEMBER;
import static org.codice.ddf.admin.ldap.actions.commons.LdapMessages.NO_USERS_IN_BASE_USER_DN;
import static org.codice.ddf.admin.ldap.actions.commons.LdapMessages.USER_NAME_ATTRIBUTE_NOT_FOUND;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.codice.ddf.admin.api.action.Message;
import org.codice.ddf.admin.api.fields.Field;
import org.codice.ddf.admin.common.actions.TestAction;
import org.codice.ddf.admin.common.fields.base.scalar.BooleanField;
import org.codice.ddf.admin.ldap.actions.commons.LdapConnectionAttempt;
import org.codice.ddf.admin.ldap.actions.commons.LdapTestingUtils;
import org.codice.ddf.admin.ldap.fields.config.LdapSettingsField;
import org.codice.ddf.admin.ldap.fields.connection.LdapBindUserInfo;
import org.codice.ddf.admin.ldap.fields.connection.LdapConnectionField;
import org.forgerock.opendj.ldap.Connection;
import org.forgerock.opendj.ldap.Filter;
import org.forgerock.opendj.ldap.SearchScope;
import org.forgerock.opendj.ldap.responses.SearchResultEntry;

import com.google.common.collect.ImmutableList;

public class LdapTestSettings extends TestAction {
    public static final String NAME = "testSettings";

    public static final String DESCRIPTION =
            "Tests whether the given LDAP dn's and user attributes exist.";

    private LdapConnectionField conn;

    private LdapBindUserInfo bindInfo;

    private LdapSettingsField settings;

    private LdapTestingUtils utils;

    public LdapTestSettings() {
        super(NAME, DESCRIPTION);
        conn = new LdapConnectionField();
        bindInfo = new LdapBindUserInfo();
        settings = new LdapSettingsField();
        utils = new LdapTestingUtils();
    }

    @Override
    public List<Field> getArguments() {
        return ImmutableList.of(conn, bindInfo, settings);
    }

    @Override
    public BooleanField performAction() {
        LdapConnectionAttempt connectionAttempt = utils.bindUserToLdapConnection(conn, bindInfo);
        addMessages(connectionAttempt.messages());

        if (!connectionAttempt.connection()
                .isPresent()) {
            return new BooleanField(false);
        }

        Connection ldapConnection = connectionAttempt.connection()
                .get();

        if (!checkDirExists(settings.baseUserDn(), ldapConnection)) {
            addArgumentMessage(BASE_USER_DN_NOT_FOUND.addFieldToPath(settings.baseUserDnField()));
        } else {
            addArgumentMessages(checkUsersInDir(settings, ldapConnection));
        }

        if (!checkDirExists(settings.baseGroupDn(), ldapConnection)) {
            addArgumentMessage(BASE_GROUP_DN_NOT_FOUND.addFieldToPath(settings.baseGroupDnField()));
        } else {
            // First check the group objectClass is on at least one entry in the directory
            addArgumentMessages(checkGroupObjectClass(settings, ldapConnection));

            // Then, check that there is a group entry (of the correct objectClass) that has
            // any member references
            addArgumentMessages(checkGroup(settings, ldapConnection));
        }

        return new BooleanField(containsErrorMsgs());
    }

    /**
     * Confirms that the baseUserDn exists.
     *
     * @param ldapConnection
     * @return
     */
    boolean checkDirExists(String dirDn, Connection ldapConnection) {
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
    private List<Message> checkUsersInDir(LdapSettingsField ldapSettings,
            Connection ldapConnection) {
        List<Message> errors = new ArrayList<>();
        List<SearchResultEntry> baseUsersResults = utils.getLdapQueryResults(ldapConnection,
                ldapSettings.baseUserDn(),
                Filter.present(ldapSettings.usernameAttribute())
                        .toString(),
                SearchScope.WHOLE_SUBTREE,
                1);

        if (baseUsersResults.isEmpty()) {
            errors.add(NO_USERS_IN_BASE_USER_DN.addFieldToPath(ldapSettings.baseUserDnField()));
            errors.add(USER_NAME_ATTRIBUTE_NOT_FOUND.addFieldToPath(ldapSettings.usernameAttributeField()));
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
    List<Message> checkGroupObjectClass(LdapSettingsField settings, Connection ldapConnection) {
        List<Message> errors = new ArrayList<>();
        List<SearchResultEntry> baseGroupResults = utils.getLdapQueryResults(ldapConnection,
                settings.baseGroupDn(),
                Filter.equality("objectClass", settings.groupObjectClass())
                        .toString(),
                SearchScope.WHOLE_SUBTREE,
                1);

        if (baseGroupResults.isEmpty()) {
            errors.add(NO_GROUPS_IN_BASE_GROUP_DN.addFieldToPath(settings.baseGroupDnField()));
            errors.add(NO_GROUPS_IN_BASE_GROUP_DN.addFieldToPath(settings.groupObjectClassField()));
        }

        return errors;
    }

    List<Message> checkGroup(LdapSettingsField settings, Connection ldapConnection) {
        List<Message> errors = new ArrayList<>();
        List<SearchResultEntry> groups = utils.getLdapQueryResults(ldapConnection,
                settings.baseGroupDn(),
                Filter.and(Filter.equality("objectClass", settings.groupObjectClass()),
                        Filter.present(settings.groupAttributeHoldingMember()))
                        .toString(),
                SearchScope.WHOLE_SUBTREE,
                1);

        if (groups.isEmpty()) {
            errors.add(NO_GROUPS_WITH_MEMBERS.addFieldToPath(settings.groupAttributeHoldingMemberField()));
        } else {
            errors.addAll(checkReferencedUser(settings, ldapConnection, groups.get(0)));
        }

        return errors;
    }

    List<Message> checkReferencedUser(LdapSettingsField settings, Connection ldapConnection,
            SearchResultEntry group) {
        List<Message> errors = new ArrayList<>();
        String memberRef = group.getAttribute(settings.groupAttributeHoldingMember())
                .firstValueAsString();
        // This memberRef will be in the format:
        // memberAttributeReferencedInGroup + username + baseUserDN
        // Strip the baseUserDN and query for the remainder as a Filter
        // beneath the baseUserDN
        List<String> split = Arrays.asList(memberRef.split(","));

        String userFilter = split.get(0);
        String checkUserBase = String.join(",", split.subList(1, split.size()));
        if (!checkUserBase.equalsIgnoreCase(settings.baseUserDn())) {
            errors.add(NO_REFERENCED_MEMBER.addFieldToPath(settings.memberAttributeReferencedInGroupField()));
        }
        if (!userFilter.split("=")[0].equalsIgnoreCase(settings.memberAttributeReferencedInGroup())) {
            errors.add(NO_REFERENCED_MEMBER.addFieldToPath(settings.memberAttributeReferencedInGroupField()));
        }
        List<SearchResultEntry> foundMember = utils.getLdapQueryResults(ldapConnection,
                settings.baseUserDn(),
                userFilter,
                SearchScope.WHOLE_SUBTREE,
                1);

        if (foundMember.isEmpty()) {
            errors.add(NO_REFERENCED_MEMBER.addFieldToPath(settings.memberAttributeReferencedInGroupField()));
        }

        return errors;
    }

    // TODO: tbatie - 4/3/17 - Implement validation
    //    @Override
    //    public List<ConfigurationMessage> validateOptionalFields(LdapConfiguration configuration) {
    //        List<ConfigurationMessage> validationResults = validateBindRealm(configuration);
    //        if (configuration.ldapUseCase()
    //                .equals(ATTRIBUTE_STORE) || configuration.ldapUseCase()
    //                .equals(AUTHENTICATION_AND_ATTRIBUTE_STORE)) {
    //            validationResults.addAll(configuration.validate(ImmutableList.of(GROUP_OBJECT_CLASS,
    //                    GROUP_ATTRIBUTE_HOLDING_MEMBER,
    //                    MEMBER_ATTRIBUTE_REFERENCED_IN_GROUP)));
    //        }
    //        return validationResults;
    //    }
}
