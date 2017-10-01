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

import static org.codice.ddf.admin.common.report.message.DefaultMessages.invalidUriError;

import com.google.common.collect.ImmutableSet;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;
import org.codice.ddf.admin.api.report.ErrorMessage;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;
import org.codice.ddf.admin.common.report.message.DefaultMessages;

public class UriField extends StringField {

  public static final String DEFAULT_FIELD_NAME = "uri";

  public static final String FIELD_TYPE_NAME = "URI";

  public static final String DESCRIPTION =
      "A Universal Resource Indicator used to identify a name or resource on the internet. Formatted according to RFC 3986.";

  public UriField() {
    this(DEFAULT_FIELD_NAME);
  }

  public UriField(String fieldName) {
    super(fieldName, FIELD_TYPE_NAME, DESCRIPTION);
  }

  protected UriField(String fieldName, String fieldTypeName, String description) {
    super(fieldName, fieldTypeName, description);
  }

  public UriField uri(String uri) {
    setValue(uri);
    return this;
  }

  @Override
  public List<ErrorMessage> validate() {
    List<ErrorMessage> validationMsgs = super.validate();
    if (!validationMsgs.isEmpty()) {
      return validationMsgs;
    }

    if (getValue() != null) {
      try {
        new URI(getValue());
      } catch (URISyntaxException e) {
        validationMsgs.add(invalidUriError(getPath()));
      }
    }
    return validationMsgs;
  }

  @Override
  public Set<String> getErrorCodes() {
    return new ImmutableSet.Builder<String>()
        .addAll(super.getErrorCodes())
        .add(DefaultMessages.INVALID_URI)
        .build();
  }
}
