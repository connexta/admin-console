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
import org.codice.ddf.admin.common.fields.base.function.TestFunctionField;
import org.codice.ddf.admin.common.fields.base.scalar.BooleanField;
import org.codice.ddf.admin.common.report.message.DefaultMessages;
import org.codice.ddf.admin.ldap.commons.LdapConnectionAttempt;
import org.codice.ddf.admin.ldap.commons.LdapTestingUtils;
import org.codice.ddf.admin.ldap.fields.connection.LdapConnectionField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LdapTestConnection extends TestFunctionField {
  private static final Logger LOGGER = LoggerFactory.getLogger(LdapTestConnection.class);

  public static final String FIELD_NAME = "testConnect";

  public static final String DESCRIPTION =
      "Attempts to established a connection with the given connection configuration";

  private LdapConnectionField connection;

  private LdapTestingUtils utils;

  public LdapTestConnection() {
    super(FIELD_NAME, DESCRIPTION);
    connection = new LdapConnectionField().useDefaultRequired();

    utils = new LdapTestingUtils();
  }

  @Override
  public List<Field> getArguments() {
    return ImmutableList.of(connection);
  }

  @Override
  public BooleanField performFunction() {
    try (LdapConnectionAttempt ldapConnectionAttempt = utils.getLdapConnection(connection)) {
      addErrorMessages(ldapConnectionAttempt);
      return new BooleanField(!containsErrorMsgs());
    } catch (IOException e) {
      LOGGER.warn("Error closing LDAP connection", e);
      return new BooleanField(false);
    }
  }

  @Override
  public FunctionField<BooleanField> newInstance() {
    return new LdapTestConnection();
  }

  @Override
  public Set<String> getFunctionErrorCodes() {
    return ImmutableSet.of(DefaultMessages.FAILED_TEST_SETUP, DefaultMessages.CANNOT_CONNECT);
  }

  @VisibleForTesting
  private void setTestingUtils(LdapTestingUtils utils) {
    this.utils = utils;
  }
}
