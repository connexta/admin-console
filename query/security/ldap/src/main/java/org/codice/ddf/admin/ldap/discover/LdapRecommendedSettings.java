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

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.common.fields.base.BaseFunctionField;
import org.codice.ddf.admin.common.report.message.DefaultMessages;
import org.codice.ddf.admin.ldap.commons.LdapConnectionAttempt;
import org.codice.ddf.admin.ldap.commons.LdapMessages;
import org.codice.ddf.admin.ldap.commons.LdapTestingUtils;
import org.codice.ddf.admin.ldap.commons.ServerGuesser;
import org.codice.ddf.admin.ldap.fields.connection.LdapBindUserInfo;
import org.codice.ddf.admin.ldap.fields.connection.LdapConnectionField;
import org.codice.ddf.admin.ldap.fields.query.LdapRecommendedSettingsField;
import org.codice.ddf.admin.ldap.fields.query.LdapTypeField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LdapRecommendedSettings extends BaseFunctionField<LdapRecommendedSettingsField> {

  private static final Logger LOGGER = LoggerFactory.getLogger(LdapRecommendedSettings.class);

  public static final String FUNCTION_NAME = "recommendedSettings";

  public static final String DESCRIPTION =
      "Attempts to retrieve recommended settings from the LDAP connection.";

  public static final LdapRecommendedSettingsField RETURN_TYPE = new LdapRecommendedSettingsField();

  private LdapConnectionField conn;

  private LdapBindUserInfo creds;

  private LdapTypeField ldapType;

  private LdapTestingUtils utils;

  public LdapRecommendedSettings() {
    super(FUNCTION_NAME, DESCRIPTION);
    conn = new LdapConnectionField().useDefaultRequired();
    creds = new LdapBindUserInfo().useDefaultRequired();
    ldapType = new LdapTypeField();
    ldapType.isRequired(true);

    utils = new LdapTestingUtils();
  }

  @Override
  public List<Field> getArguments() {
    return ImmutableList.of(conn, creds, ldapType);
  }

  @Override
  public LdapRecommendedSettingsField performFunction() {
    try (LdapConnectionAttempt connectionAttempt = utils.bindUserToLdapConnection(conn, creds)) {
      addErrorMessages(connectionAttempt);

      if (!connectionAttempt.isResultPresent()) {
        return null;
      }

      ServerGuesser guesser =
          ServerGuesser.buildGuesser(ldapType.getValue(), connectionAttempt.getResult());

      return new LdapRecommendedSettingsField()
          .userDns(guesser.getUserBaseChoices())
          .groupDns(guesser.getGroupBaseChoices())
          .loginUserAttributes(guesser.getLoginUserAttribute())
          .groupObjectClasses(guesser.getGroupObjectClass())
          .groupAttributesHoldingMember(guesser.getGroupAttributeHoldingMember())
          .memberAttributesReferencedInGroup(guesser.getMemberAttributeReferencedInGroup())
          .queryBases(guesser.getBaseContexts());
    } catch (IOException e) {
      LOGGER.warn("Error closing LDAP connection", e);
      return null;
    }
  }

  @Override
  public FunctionField<LdapRecommendedSettingsField> newInstance() {
    return new LdapRecommendedSettings();
  }

  @Override
  public LdapRecommendedSettingsField getReturnType() {
    return RETURN_TYPE;
  }

  @Override
  public Set<String> getFunctionErrorCodes() {
    return ImmutableSet.of(
        LdapMessages.CANNOT_BIND,
        DefaultMessages.FAILED_TEST_SETUP,
        DefaultMessages.CANNOT_CONNECT);
  }

  @VisibleForTesting
  private void setTestingUtils(LdapTestingUtils utils) {
    this.utils = utils;
  }
}
