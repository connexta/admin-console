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
package org.codice.ddf.admin.ldap.fields.query;

import static org.codice.ddf.admin.ldap.commons.LdapMessages.invalidQueryError;

import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Set;
import org.codice.ddf.admin.api.report.ErrorMessage;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;
import org.codice.ddf.admin.ldap.commons.LdapMessages;
import org.forgerock.opendj.ldap.Filter;

public class LdapQueryField extends StringField {
  public static final String DEFAULT_FIELD_NAME = "query";

  public static final String FIELD_TYPE_NAME = "LdapQuery";

  public static final String DESCRIPTION =
      "A Search filters that enables you to define search criteria. Ex: (objectClass=*). LDAP query syntax can be found at: https://msdn.microsoft.com/en-us/library/aa746475(v=vs.85).aspx";

  public LdapQueryField() {
    super(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
  }

  public LdapQueryField query(String query) {
    setValue(query);
    return this;
  }

  @Override
  public List<ErrorMessage> validate() {
    List<ErrorMessage> validationMsgs = super.validate();
    if (!validationMsgs.isEmpty()) {
      return validationMsgs;
    }

    if (getValue() != null && !validQuery(getValue())) {
      validationMsgs.add(invalidQueryError(path()));
    }

    return validationMsgs;
  }

  private boolean validQuery(String query) {
    try {
      Filter.valueOf(query);
    } catch (Exception e) {
      return false;
    }
    return true;
  }

  @Override
  public Set<String> getErrorCodes() {
    return new ImmutableSet.Builder<String>()
        .addAll(super.getErrorCodes())
        .add(LdapMessages.INVALID_QUERY)
        .build();
  }
}
