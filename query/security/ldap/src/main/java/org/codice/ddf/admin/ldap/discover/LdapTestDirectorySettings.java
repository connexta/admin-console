/**
 * Copyright (c) Codice Foundation
 *
 * <p>This is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version.
 *
 * <p>This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details. A copy of the GNU Lesser General Public
 * License is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 */
package org.codice.ddf.admin.ldap.discover;

import static org.codice.ddf.admin.ldap.commons.LdapMessages.noGroupsInBaseGroupDnError;
import static org.codice.ddf.admin.ldap.commons.LdapMessages.noGroupsWithMembersError;
import static org.codice.ddf.admin.ldap.commons.LdapMessages.noReferencedMemberError;
import static org.codice.ddf.admin.ldap.commons.LdapMessages.noUsersInBaseUserDnError;
import static org.codice.ddf.admin.ldap.commons.LdapMessages.userAttributeNotFoundError;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.common.fields.base.function.TestFunctionField;
import org.codice.ddf.admin.common.fields.base.scalar.BooleanField;
import org.codice.ddf.admin.common.report.message.DefaultMessages;
import org.codice.ddf.admin.ldap.commons.LdapConnectionAttempt;
import org.codice.ddf.admin.ldap.commons.LdapMessages;
import org.codice.ddf.admin.ldap.commons.LdapTestingUtils;
import org.codice.ddf.admin.ldap.fields.config.LdapDirectorySettingsField;
import org.codice.ddf.admin.ldap.fields.connection.LdapBindUserInfo;
import org.codice.ddf.admin.ldap.fields.connection.LdapConnectionField;
import org.forgerock.opendj.ldap.Connection;
import org.forgerock.opendj.ldap.Filter;
import org.forgerock.opendj.ldap.SearchScope;
import org.forgerock.opendj.ldap.responses.SearchResultEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LdapTestDirectorySettings extends TestFunctionField {
  private static final Logger LOGGER = LoggerFactory.getLogger(LdapTestDirectorySettings.class);

  public static final String FIELD_NAME = "testDirectorySettings";

  public static final String DESCRIPTION =
      "Tests whether the given LDAP DNs and user attributes exist.";

  private LdapConnectionField conn;

  private LdapBindUserInfo bindInfo;

  private LdapDirectorySettingsField settings;

  private LdapTestingUtils utils;

  public LdapTestDirectorySettings() {
    super(FIELD_NAME, DESCRIPTION);
    conn = new LdapConnectionField().useDefaultRequired();
    bindInfo = new LdapBindUserInfo().useDefaultRequired();
    settings = new LdapDirectorySettingsField().useDefaultRequiredForAuthentication();

    utils = new LdapTestingUtils();
  }

  @Override
  public List<Field> getArguments() {
    return ImmutableList.of(conn, bindInfo, settings);
  }

  @Override
  public BooleanField performFunction() {
    try (LdapConnectionAttempt connectionAttempt = utils.bindUserToLdapConnection(conn, bindInfo)) {
      addErrorMessages(connectionAttempt);

      if (containsErrorMsgs()) {
        return new BooleanField(false);
      }

      Connection ldapConnection = connectionAttempt.getResult();

      addErrorMessages(utils.checkDirExists(settings.baseGroupDnField(), ldapConnection));
      addErrorMessages(utils.checkDirExists(settings.baseUserDnField(), ldapConnection));

      // Short-circuit return here, if either the user or group directory does not exist
      if (containsErrorMsgs()) {
        return new BooleanField(false);
      }

      checkUsersInDir(ldapConnection);

      // Short-circuit return here, if there are no users in base dir
      if (containsErrorMsgs()) {
        return new BooleanField(false);
      }

      if (settings.useCaseField().isAttributeStore()) {
        // Check if group objectClass is on at least one entry in the directory
        checkGroupObjectClass(ldapConnection);

        // Don't check the group if there is no entry with the correct objectClass
        if (containsErrorMsgs()) {
          return new BooleanField(false);
        }

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
    if (settings.useCaseField().isAttributeStore()) {
      settings.useDefaultRequiredForAttributeStore();
    }

    super.validate();
  }

  @Override
  public FunctionField<BooleanField> newInstance() {
    return new LdapTestDirectorySettings();
  }

  @Override
  public Set<String> getFunctionErrorCodes() {
    return ImmutableSet.of(
        LdapMessages.CANNOT_BIND,
        LdapMessages.DN_DOES_NOT_EXIST,
        LdapMessages.NO_USERS_IN_BASE_USER_DN,
        LdapMessages.USER_ATTRIBUTE_NOT_FOUND,
        LdapMessages.NO_GROUPS_IN_BASE_GROUP_DN,
        LdapMessages.NO_GROUPS_WITH_MEMBERS,
        LdapMessages.NO_REFERENCED_MEMBER,
        DefaultMessages.FAILED_TEST_SETUP,
        DefaultMessages.CANNOT_CONNECT);
  }

  /**
   * Checks the baseUserDn for users.
   *
   * @param ldapConnection
   */
  void checkUsersInDir(Connection ldapConnection) {
    List<SearchResultEntry> baseUsersResults =
        utils.getLdapQueryResults(
            ldapConnection,
            settings.baseUserDn(),
            Filter.present(settings.loginUserAttribute()).toString(),
            SearchScope.WHOLE_SUBTREE,
            1);

    if (baseUsersResults.isEmpty()) {
      addErrorMessage(noUsersInBaseUserDnError(settings.baseUserDnField().getPath()));
      addErrorMessage(userAttributeNotFoundError(settings.loginUserAttributeField().getPath()));
    }
  }

  /**
   * Checks if the baseGroupDn contains the groupObjectclass
   *
   * @param ldapConnection
   */
  void checkGroupObjectClass(Connection ldapConnection) {
    List<SearchResultEntry> baseGroupResults =
        utils.getLdapQueryResults(
            ldapConnection,
            settings.baseGroupDn(),
            Filter.equality("objectClass", settings.groupObjectClass()).toString(),
            SearchScope.WHOLE_SUBTREE,
            1);

    if (baseGroupResults.isEmpty()) {
      addErrorMessage(noGroupsInBaseGroupDnError(settings.baseGroupDnField().getPath()));
      addErrorMessage(noGroupsInBaseGroupDnError(settings.groupObjectClassField().getPath()));
    }
  }

  void checkGroup(Connection ldapConnection) {
    List<SearchResultEntry> groups =
        utils.getLdapQueryResults(
            ldapConnection,
            settings.baseGroupDn(),
            Filter.and(
                    Filter.equality("objectClass", settings.groupObjectClass()),
                    Filter.present(settings.groupAttributeHoldingMember()))
                .toString(),
            SearchScope.WHOLE_SUBTREE,
            1);

    if (groups.isEmpty()) {
      addErrorMessage(
          noGroupsWithMembersError(settings.groupAttributeHoldingMemberField().getPath()));
    } else {
      checkReferencedUser(ldapConnection, groups.get(0));
    }
  }

  void checkReferencedUser(Connection ldapConnection, SearchResultEntry group) {
    String memberRef =
        group.getAttribute(settings.groupAttributeHoldingMember()).firstValueAsString();
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
      List<SearchResultEntry> foundMember =
          utils.getLdapQueryResults(
              ldapConnection, settings.baseUserDn(), userFilter, SearchScope.WHOLE_SUBTREE, 1);

      if (foundMember.isEmpty()) {
        addErrorMessage(
            noReferencedMemberError(settings.memberAttributeReferencedInGroupField().getPath()));
      }
    } else {
      addErrorMessage(
          noReferencedMemberError(settings.memberAttributeReferencedInGroupField().getPath()));
    }
  }

  /**
   * Intentionally scoped as package private. This is a test support method to be invoked by Spock
   * tests which will elevate scope as needed in order to execute. If Java-based unit tests are ever
   * needed, this scope will need to be updated to package-private.
   *
   * @param utils Ldap support utilities
   */
  void setTestingUtils(LdapTestingUtils utils) {
    this.utils = utils;
  }
}
