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

import static org.codice.ddf.admin.ldap.commons.LdapMessages.md5NeedsEncryptedError;
import static org.codice.ddf.admin.ldap.fields.connection.LdapBindMethod.DigestMd5Sasl.DIGEST_MD5_SASL;
import static org.codice.ddf.admin.ldap.fields.connection.LdapEncryptionMethodField.LdapsEncryption.LDAPS;
import static org.codice.ddf.admin.ldap.fields.connection.LdapEncryptionMethodField.StartTlsEncryption.START_TLS;

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
import org.codice.ddf.admin.ldap.commons.LdapMessages;
import org.codice.ddf.admin.ldap.commons.LdapTestingUtils;
import org.codice.ddf.admin.ldap.fields.connection.LdapBindUserInfo;
import org.codice.ddf.admin.ldap.fields.connection.LdapConnectionField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LdapTestBind extends TestFunctionField {
  private static final Logger LOGGER = LoggerFactory.getLogger(LdapTestBind.class);

  public static final String FUNCTION_NAME = "testBind";

  public static final String DESCRIPTION =
      "Attempts to bind a user to the given ldap connection given the ldap bind user credentials.";

  private LdapConnectionField conn;

  private LdapBindUserInfo creds;

  private LdapTestingUtils utils;

  public LdapTestBind() {
    super(FUNCTION_NAME, DESCRIPTION);
    conn = new LdapConnectionField().useDefaultRequired();
    creds = new LdapBindUserInfo().useDefaultRequired();

    utils = new LdapTestingUtils();
  }

  @Override
  public List<Field> getArguments() {
    return ImmutableList.of(conn, creds);
  }

  // Possible message types: CANNOT_CONFIGURE, CANNOT_CONNECT, CANNOT_BIND
  @Override
  public BooleanField performFunction() {
    try (LdapConnectionAttempt ldapConnectionAttempt =
        utils.bindUserToLdapConnection(conn, creds)) {
      addErrorMessages(ldapConnectionAttempt);
    } catch (IOException e) {
      LOGGER.warn("Error closing LDAP connection", e);
    }
    return new BooleanField(!containsErrorMsgs());
  }

  @Override
  public FunctionField<BooleanField> newInstance() {
    return new LdapTestBind();
  }

  @Override
  public void validate() {
    super.validate();

    if (containsErrorMsgs()) {
      return;
    }

    // To use MD5, we require an encrypted connection
    if (DIGEST_MD5_SASL.equals(creds.bindMethod())
        && (!(START_TLS.equals(conn.encryptionMethod())
            || LDAPS.equals(conn.encryptionMethod())))) {
      addErrorMessage(md5NeedsEncryptedError(creds.bindMethodField().getPath()));
    }
  }

  @Override
  public Set<String> getFunctionErrorCodes() {
    return ImmutableSet.of(
        LdapMessages.CANNOT_BIND,
        LdapMessages.MD5_NEEDS_ENCRYPTED,
        DefaultMessages.FAILED_TEST_SETUP,
        DefaultMessages.CANNOT_CONNECT);
  }

  @VisibleForTesting
  private void setTestingUtils(LdapTestingUtils utils) {
    this.utils = utils;
  }
}
