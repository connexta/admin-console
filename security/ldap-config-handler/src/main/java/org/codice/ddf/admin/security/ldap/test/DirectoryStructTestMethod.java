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
 */

package org.codice.ddf.admin.security.ldap.test;

import static org.codice.ddf.admin.api.config.ldap.LdapConfiguration.BASE_GROUP_DN;
import static org.codice.ddf.admin.api.config.ldap.LdapConfiguration.BASE_USER_DN;
import static org.codice.ddf.admin.api.config.ldap.LdapConfiguration.BIND_METHOD;
import static org.codice.ddf.admin.api.config.ldap.LdapConfiguration.BIND_REALM;
import static org.codice.ddf.admin.api.config.ldap.LdapConfiguration.BIND_USER_DN;
import static org.codice.ddf.admin.api.config.ldap.LdapConfiguration.BIND_USER_PASSWORD;
import static org.codice.ddf.admin.api.config.ldap.LdapConfiguration.ENCRYPTION_METHOD;
import static org.codice.ddf.admin.api.config.ldap.LdapConfiguration.GROUP_ATTRIBUTE_HOLDING_MEMBER;
import static org.codice.ddf.admin.api.config.ldap.LdapConfiguration.GROUP_OBJECT_CLASS;
import static org.codice.ddf.admin.api.config.ldap.LdapConfiguration.HOST_NAME;
import static org.codice.ddf.admin.api.config.ldap.LdapConfiguration.MEMBER_ATTRIBUTE_REFERENCED_IN_GROUP;
import static org.codice.ddf.admin.api.config.ldap.LdapConfiguration.PORT;
import static org.codice.ddf.admin.api.config.ldap.LdapConfiguration.USER_NAME_ATTRIBUTE;
import static org.codice.ddf.admin.api.handler.commons.HandlerCommons.SUCCESSFUL_TEST;
import static org.codice.ddf.admin.api.handler.report.Report.createReport;
import static org.codice.ddf.admin.api.validation.LdapValidationUtils.ATTRIBUTE_STORE;
import static org.codice.ddf.admin.api.validation.LdapValidationUtils.AUTHENTICATION_AND_ATTRIBUTE_STORE;
import static org.codice.ddf.admin.api.validation.LdapValidationUtils.validateBindRealm;
import static org.codice.ddf.admin.security.ldap.LdapConnectionResult.BASE_GROUP_DN_NOT_FOUND;
import static org.codice.ddf.admin.security.ldap.LdapConnectionResult.BASE_USER_DN_NOT_FOUND;
import static org.codice.ddf.admin.security.ldap.LdapConnectionResult.CANNOT_BIND;
import static org.codice.ddf.admin.security.ldap.LdapConnectionResult.CANNOT_CONFIGURE;
import static org.codice.ddf.admin.security.ldap.LdapConnectionResult.CANNOT_CONNECT;
import static org.codice.ddf.admin.security.ldap.LdapConnectionResult.NO_GROUPS_IN_BASE_GROUP_DN;
import static org.codice.ddf.admin.security.ldap.LdapConnectionResult.NO_GROUPS_WITH_MEMBERS;
import static org.codice.ddf.admin.security.ldap.LdapConnectionResult.NO_REFERENCED_MEMBER;
import static org.codice.ddf.admin.security.ldap.LdapConnectionResult.NO_USERS_IN_BASE_USER_DN;
import static org.codice.ddf.admin.security.ldap.LdapConnectionResult.SUCCESSFUL_BIND;
import static org.codice.ddf.admin.security.ldap.LdapConnectionResult.USER_NAME_ATTRIBUTE_NOT_FOUND;
import static org.codice.ddf.admin.security.ldap.LdapConnectionResult.toDescriptionMap;
import static org.codice.ddf.admin.security.ldap.test.LdapTestingCommons.LdapConnectionAttempt;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.codice.ddf.admin.api.config.ldap.LdapConfiguration;
import org.codice.ddf.admin.api.handler.ConfigurationMessage;
import org.codice.ddf.admin.api.handler.method.TestMethod;
import org.codice.ddf.admin.api.handler.report.Report;
import org.forgerock.opendj.ldap.Connection;
import org.forgerock.opendj.ldap.Filter;
import org.forgerock.opendj.ldap.SearchScope;
import org.forgerock.opendj.ldap.responses.SearchResultEntry;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;

public class DirectoryStructTestMethod extends TestMethod<LdapConfiguration> {
    private static final String LDAP_DIRECTORY_STRUCT_TEST_ID = "dir-struct";

    private static final String DESCRIPTION =
            "Verifies that the specified directory structure, entries and required attributes to configure LDAP exist.";

    private static final List<String> REQUIRED_FIELDS = ImmutableList.of(HOST_NAME,
            PORT,
            ENCRYPTION_METHOD,
            BIND_USER_DN,
            BIND_USER_PASSWORD,
            BIND_METHOD,
            BASE_USER_DN,
            BASE_GROUP_DN,
            USER_NAME_ATTRIBUTE);

    private static final List<String> OPTIONAL_FIELDS = ImmutableList.of(BIND_REALM,
            GROUP_OBJECT_CLASS,
            GROUP_ATTRIBUTE_HOLDING_MEMBER,
            MEMBER_ATTRIBUTE_REFERENCED_IN_GROUP);

    private static final Map<String, String> SUCCESS_TYPES = ImmutableMap.of(SUCCESSFUL_TEST,
            "All directory fields have been verified successfully");

    private static final Map<String, String> FAILURE_TYPES = toDescriptionMap(Arrays.asList(
            CANNOT_CONFIGURE,
            CANNOT_CONNECT,
            CANNOT_BIND,
            BASE_USER_DN_NOT_FOUND,
            BASE_GROUP_DN_NOT_FOUND,
            USER_NAME_ATTRIBUTE_NOT_FOUND));
    // TODO: tbatie - 1/25/17 - There are additional failure types to add to this list

    private static final Map<String, String> WARNING_TYPES = toDescriptionMap(Arrays.asList(
            NO_USERS_IN_BASE_USER_DN,
            NO_GROUPS_IN_BASE_GROUP_DN,
            NO_GROUPS_WITH_MEMBERS,
            NO_REFERENCED_MEMBER));

    private final LdapTestingCommons ldapTestingCommons;

    public DirectoryStructTestMethod(LdapTestingCommons ldapTestingCommons) {
        super(LDAP_DIRECTORY_STRUCT_TEST_ID, DESCRIPTION, REQUIRED_FIELDS, OPTIONAL_FIELDS,
                SUCCESS_TYPES,
                FAILURE_TYPES,
                WARNING_TYPES);
        this.ldapTestingCommons = ldapTestingCommons;
    }

    @Override
    public Report test(LdapConfiguration configuration) {
        LdapConnectionAttempt connectionAttempt = ldapTestingCommons.bindUserToLdapConnection(
                configuration);

        if (connectionAttempt.result() != SUCCESSFUL_BIND) {
            return createReport(SUCCESS_TYPES,
                    FAILURE_TYPES,
                    WARNING_TYPES,
                    Collections.singletonList(connectionAttempt.result()
                            .name()));
        }

        Multimap<String, String> resultsWithConfigIds = ArrayListMultimap.create();
        try (Connection ldapConnection = connectionAttempt.connection()) {
            if (checkUserDir(configuration, ldapConnection)) {
                resultsWithConfigIds.put(BASE_USER_DN_NOT_FOUND.name(), BASE_USER_DN);
            } else {
                checkUsersInDir(configuration, resultsWithConfigIds, ldapConnection);
            }

            if (checkGroupDir(configuration, ldapConnection)) {
                resultsWithConfigIds.put(BASE_GROUP_DN_NOT_FOUND.name(), BASE_GROUP_DN);
            } else {
                // First check the group objectClass is on at least one entry in the directory
                checkGroupObjectClass(configuration, resultsWithConfigIds, ldapConnection);

                // Then, check that there is a group entry (of the correct objectClass) that has
                // any member references
                checkGroup(configuration, resultsWithConfigIds, ldapConnection);
            }
        }

        if (resultsWithConfigIds.isEmpty()) {
            return createReport(SUCCESS_TYPES, FAILURE_TYPES, WARNING_TYPES, SUCCESSFUL_TEST);
        }

        return createReport(SUCCESS_TYPES, FAILURE_TYPES, WARNING_TYPES, resultsWithConfigIds);
    }

    boolean checkUserDir(LdapConfiguration configuration, Connection ldapConnection) {
        return ldapTestingCommons.getLdapQueryResults(ldapConnection,
                configuration.baseUserDn(),
                Filter.present("objectClass")
                        .toString(),
                SearchScope.BASE_OBJECT,
                1)
                .isEmpty();
    }

    void checkUsersInDir(LdapConfiguration configuration,
            Multimap<String, String> resultsWithConfigIds, Connection ldapConnection) {
        List<SearchResultEntry> baseUsersResults = ldapTestingCommons.getLdapQueryResults(
                ldapConnection,
                configuration.baseUserDn(),
                Filter.present(configuration.userNameAttribute())
                        .toString(),
                SearchScope.SUBORDINATES,
                1);
        if (baseUsersResults.isEmpty()) {
            resultsWithConfigIds.put(NO_USERS_IN_BASE_USER_DN.name(), BASE_USER_DN);
            resultsWithConfigIds.put(USER_NAME_ATTRIBUTE_NOT_FOUND.name(), USER_NAME_ATTRIBUTE);
        }
    }

    boolean checkGroupDir(LdapConfiguration configuration, Connection ldapConnection) {
        return ldapTestingCommons.getLdapQueryResults(ldapConnection,
                configuration.baseGroupDn(),
                Filter.present("objectClass")
                        .toString(),
                SearchScope.BASE_OBJECT,
                1)
                .isEmpty();
    }

    void checkGroupObjectClass(LdapConfiguration configuration,
            Multimap<String, String> resultsWithConfigIds, Connection ldapConnection) {
        List<SearchResultEntry> baseGroupResults = ldapTestingCommons.getLdapQueryResults(
                ldapConnection,
                configuration.baseGroupDn(),
                Filter.equality("objectClass", configuration.groupObjectClass())
                        .toString(),
                SearchScope.SUBORDINATES,
                1);
        if (baseGroupResults.isEmpty()) {
            resultsWithConfigIds.put(NO_GROUPS_IN_BASE_GROUP_DN.name(), BASE_GROUP_DN);
            resultsWithConfigIds.put(NO_GROUPS_IN_BASE_GROUP_DN.name(), GROUP_OBJECT_CLASS);
        }
    }

    void checkGroup(LdapConfiguration configuration, Multimap<String, String> resultsWithConfigIds,
            Connection ldapConnection) {
        List<SearchResultEntry> groups = ldapTestingCommons.getLdapQueryResults(ldapConnection,
                configuration.baseGroupDn(),
                Filter.and(Filter.equality("objectClass", configuration.groupObjectClass()),
                        Filter.present(configuration.groupAttributeHoldingMember()))
                        .toString(),
                SearchScope.SUBORDINATES,
                1);
        if (groups.isEmpty()) {
            resultsWithConfigIds.put(NO_GROUPS_WITH_MEMBERS.name(), GROUP_ATTRIBUTE_HOLDING_MEMBER);
        } else {
            checkReferencedUser(configuration, resultsWithConfigIds, ldapConnection, groups.get(0));
        }
    }

    void checkReferencedUser(LdapConfiguration configuration,
            Multimap<String, String> resultsWithConfigIds, Connection ldapConnection,
            SearchResultEntry group) {
        String memberRef = group.getAttribute(configuration.groupAttributeHoldingMember())
                .firstValueAsString();
        // This memberRef will be in the format:
        // memberAttributeReferencedInGroup + username + baseUserDN
        // Strip the baseUserDN and query for the remainder as a Filter
        // beneath the baseUserDN
        List<String> split = Arrays.asList(memberRef.split(","));

        String userFilter = split.get(0);
        String checkUserBase = String.join(",", split.subList(1, split.size()));
        if (!checkUserBase.equalsIgnoreCase(configuration.baseUserDn())) {
            resultsWithConfigIds.put(NO_REFERENCED_MEMBER.name(),
                    MEMBER_ATTRIBUTE_REFERENCED_IN_GROUP);
        }
        if (!userFilter.split("=")[0].equalsIgnoreCase(configuration.memberAttributeReferencedInGroup())) {
            resultsWithConfigIds.put(NO_REFERENCED_MEMBER.name(),
                    MEMBER_ATTRIBUTE_REFERENCED_IN_GROUP);
        }
        List<SearchResultEntry> foundMember = ldapTestingCommons.getLdapQueryResults(ldapConnection,
                configuration.baseUserDn(),
                userFilter,
                SearchScope.SUBORDINATES,
                1);
        if (foundMember.isEmpty()) {
            resultsWithConfigIds.put(NO_REFERENCED_MEMBER.name(),
                    MEMBER_ATTRIBUTE_REFERENCED_IN_GROUP);
        }
    }

    @Override
    public List<ConfigurationMessage> validateOptionalFields(LdapConfiguration configuration) {
        List<ConfigurationMessage> validationResults = validateBindRealm(configuration);
        if (configuration.ldapUseCase()
                .equals(ATTRIBUTE_STORE) || configuration.ldapUseCase()
                .equals(AUTHENTICATION_AND_ATTRIBUTE_STORE)) {
            validationResults.addAll(configuration.validate(ImmutableList.of(GROUP_OBJECT_CLASS,
                    GROUP_ATTRIBUTE_HOLDING_MEMBER,
                    MEMBER_ATTRIBUTE_REFERENCED_IN_GROUP)));
        }
        return validationResults;
    }
}
