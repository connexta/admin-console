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
package org.codice.ddf.admin.common.fields.common;

import com.google.common.collect.ImmutableList;
import java.util.List;
import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.common.fields.base.BaseObjectField;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;

public class CredentialsField extends BaseObjectField {

  public static final String DEFAULT_FIELD_NAME = "creds";

  public static final String FIELD_TYPE_NAME = "Credentials";

  public static final String DESCRIPTION = "Credentials required for authentication.";

  public static final String USERNAME_FIELD_NAME = "username";

  @SuppressWarnings("squid:S2068" /* Password field name not a hard-coded password */)
  public static final String PASSWORD_FIELD_NAME = "password";

  private StringField username;

  private PasswordField password;

  public CredentialsField() {
    super(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
    this.username = new StringField(USERNAME_FIELD_NAME);
    this.password = new PasswordField(PASSWORD_FIELD_NAME);
  }

  public CredentialsField username(String username) {
    this.username.setValue(username);
    return this;
  }

  public CredentialsField password(String password) {
    this.password.setValue(password);
    return this;
  }

  public StringField usernameField() {
    return username;
  }

  public PasswordField passwordField() {
    return password;
  }

  public String password() {
    return password.getValue();
  }

  public String username() {
    return username.getValue();
  }

  public CredentialsField useDefaultRequiredFields() {
    username.isRequired(true);
    password.isRequired(true);
    return this;
  }

  @Override
  public List<Field> getFields() {
    return ImmutableList.of(username, password);
  }
}
