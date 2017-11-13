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
package org.codice.ddf.admin.security.common.fields.ldap;

import com.google.common.collect.ImmutableList;
import org.codice.ddf.admin.api.fields.EnumValue;
import org.codice.ddf.admin.common.fields.base.BaseEnumField;

public class LdapUseCase extends BaseEnumField<String> {
  public static final String DEFAULT_FIELD_NAME = "useCase";

  public static final String FIELD_TYPE_NAME = "LdapUseCase";

  public static final String DESCRIPTION = "Describes the intended use of the LDAP settings.";

  public static final EnumValue<String> AUTHENTICATION = new AuthenticationEnumValue();

  public static final EnumValue<String> ATTRIBUTE_STORE = new AttributeStore();

  public static final EnumValue<String> AUTHENTICATION_AND_ATTRIBUTE_STORE =
      new AuthenticationAndAttributeStore();

  public LdapUseCase() {
    this(null);
  }

  private LdapUseCase(EnumValue<String> bindMethod) {
    super(
        DEFAULT_FIELD_NAME,
        FIELD_TYPE_NAME,
        DESCRIPTION,
        ImmutableList.of(
            new AuthenticationEnumValue(),
            new AttributeStore(),
            new AuthenticationAndAttributeStore()),
        bindMethod);
  }

  public boolean isAuthentication() {
    return AuthenticationEnumValue.AUTHENTICATION.equals(getValue())
        || AuthenticationAndAttributeStore.AUTHENTICATION_AND_ATTRIBUTE_STORE.equals(getValue());
  }

  public boolean isAttributeStore() {
    return AttributeStore.ATTRIBUTE_STORE.equals(getValue())
        || AuthenticationAndAttributeStore.AUTHENTICATION_AND_ATTRIBUTE_STORE.equals(getValue());
  }

  public static final class AuthenticationEnumValue implements EnumValue<String> {

    public static final String DESCRIPTION =
        "Indicates the LDAP is intended to be used as a source to login into.";

    public static final String AUTHENTICATION = "Authentication";

    @Override
    public String getEnumTitle() {
      return AUTHENTICATION;
    }

    @Override
    public String getDescription() {
      return DESCRIPTION;
    }

    @Override
    public String getValue() {
      return AUTHENTICATION;
    }
  }

  public static final class AttributeStore implements EnumValue<String> {
    public static final String DESCRIPTION =
        "Indicates the LDAP is intended to be used as store for retrieving attributes of entries.";

    public static final String ATTRIBUTE_STORE = "AttributeStore";

    @Override
    public String getEnumTitle() {
      return ATTRIBUTE_STORE;
    }

    @Override
    public String getDescription() {
      return DESCRIPTION;
    }

    @Override
    public String getValue() {
      return ATTRIBUTE_STORE;
    }
  }

  public static final class AuthenticationAndAttributeStore implements EnumValue<String> {
    public static final String DESCRIPTION =
        "Indicates the LDAP is intended to be used as both a source of login and a store for retrieving attributes of entries.";

    public static final String AUTHENTICATION_AND_ATTRIBUTE_STORE =
        "AuthenticationAndAttributeStore";

    @Override
    public String getEnumTitle() {
      return AUTHENTICATION_AND_ATTRIBUTE_STORE;
    }

    @Override
    public String getDescription() {
      return DESCRIPTION;
    }

    @Override
    public String getValue() {
      return AUTHENTICATION_AND_ATTRIBUTE_STORE;
    }
  }
}
