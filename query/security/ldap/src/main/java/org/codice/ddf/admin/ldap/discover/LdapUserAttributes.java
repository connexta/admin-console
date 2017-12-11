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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.codice.ddf.admin.api.Field;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LdapUserAttributes extends BaseFunctionField<StringField.ListImpl> {

  private static final Logger LOGGER = LoggerFactory.getLogger(LdapUserAttributes.class);

  public static final String FIELD_NAME = "userAttributes";

  public static final String DESCRIPTION =
      "Retrieves a subset of available user attributes based on the LDAP settings provided.";

  public static final String BASE_USER_DN = "baseUserDn";

  public static final StringField.ListImpl RETURN_TYPE = new StringField.ListImpl();

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

    utils = new LdapTestingUtils();
  }

  @Override
  public List<Field> getArguments() {
    return ImmutableList.of(conn, bindInfo, baseUserDn);
  }

  @Override
  public StringField.ListImpl performFunction() {
    StringField.ListImpl entries = null;
    try (LdapConnectionAttempt connectionAttempt = utils.bindUserToLdapConnection(conn, bindInfo)) {
      addErrorMessages(connectionAttempt);

      if (containsErrorMsgs()) {
        return null;
      }

      ServerGuesser serverGuesser = ServerGuesser.buildGuesser(connectionAttempt.getResult());
      Set<String> ldapEntryAttributes =
          serverGuesser.getClaimAttributeOptions(baseUserDn.getValue());

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
    return ImmutableSet.of(
        LdapMessages.CANNOT_BIND,
        DefaultMessages.FAILED_TEST_SETUP,
        DefaultMessages.CANNOT_CONNECT);
  }

  @SuppressWarnings(
      "squid:UnusedPrivateMethod" /* For testing purposes only. Groovy can access private methods. */)
  private void setTestingUtils(LdapTestingUtils utils) {
    this.utils = utils;
  }
}
