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
package org.codice.ddf.admin.ldap.fields;

import static org.codice.ddf.admin.ldap.commons.LdapMessages.invalidUserAttribute;

import com.google.common.collect.ImmutableSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import org.codice.ddf.admin.api.report.ErrorMessage;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;
import org.codice.ddf.admin.ldap.commons.LdapMessages;

/**
 * The description of an attribute names and the logic for validating them are defined in RFC 4512.
 *
 * @see <a href="https://www.ietf.org/rfc/rfc4512.txt">RFC 4512 </a>
 */
public class LdapAttributeName extends StringField {

  public static final String DEFAULT_FIELD_NAME = "attriName";

  public static final String FIELD_TYPE_NAME = "LdapAttributeName";

  public static final String DESCRIPTION =
      "The short descriptive name of an LDAP attribute as defined in RFC4512.";

  private static final Pattern ATTRIBUTE_NAME_PATTERN = Pattern.compile("^[a-zA-Z][a-zA-Z0-9-]*$");

  public LdapAttributeName() {
    super(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
  }

  public LdapAttributeName(String fieldName) {
    super(fieldName, FIELD_TYPE_NAME, DESCRIPTION);
  }

  @Override
  public List<ErrorMessage> validate() {
    List<ErrorMessage> errors = super.validate();
    if (!errors.isEmpty()) {
      return errors;
    }

    if (getValue() != null) {
      errors.addAll(validate(getValue(), path()));
    }
    return errors;
  }

  // TODO: 7/7/17 - tbatie - This validate should be reformatted once there is a generic way to
  // create MapField objects that contain different value field.
  public static List<ErrorMessage> validate(String attribute, List<String> path) {
    List<ErrorMessage> errors = new ArrayList<>();

    if (!ATTRIBUTE_NAME_PATTERN.matcher(attribute).matches()) {
      errors.add(invalidUserAttribute(path));
    }
    return errors;
  }

  @Override
  public LdapAttributeName isRequired(boolean required) {
    super.isRequired(required);
    return this;
  }

  @Override
  public Set<String> getErrorCodes() {
    return new ImmutableSet.Builder<String>()
        .addAll(super.getErrorCodes())
        .add(LdapMessages.INVALID_USER_ATTRIBUTE)
        .build();
  }
}
