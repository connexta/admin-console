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
package org.codice.ddf.admin.common.fields.base.scalar;

import static org.codice.ddf.admin.api.fields.ScalarField.ScalarType.STRING;
import static org.codice.ddf.admin.common.report.message.DefaultMessages.emptyFieldError;

import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import org.codice.ddf.admin.api.report.ErrorMessage;
import org.codice.ddf.admin.common.fields.base.BaseListField;
import org.codice.ddf.admin.common.report.message.DefaultMessages;

public class StringField extends BaseScalarField<String> {

  public static final String DEFAULT_STING_FIELD_NAME = "string";

  protected StringField(String fieldName, String fieldTypeName, String description) {
    super(fieldName, fieldTypeName, description, STRING);
  }

  public StringField(String fieldName) {
    this(fieldName, null, null);
  }

  public StringField() {
    this(DEFAULT_STING_FIELD_NAME);
  }

  @Override
  public List<ErrorMessage> validate() {
    List<ErrorMessage> validationMsgs = super.validate();

    if (!validationMsgs.isEmpty()) {
      return validationMsgs;
    }

    if (getValue() != null && getValue().trim().isEmpty()) {
      validationMsgs.add(emptyFieldError(getPath()));
    }

    return validationMsgs;
  }

  @Override
  public StringField isRequired(boolean required) {
    super.isRequired(required);
    return this;
  }

  @Override
  public Set<String> getErrorCodes() {
    return new ImmutableSet.Builder<String>()
        .addAll(super.getErrorCodes())
        .add(DefaultMessages.EMPTY_FIELD)
        .build();
  }

  public static class ListImpl extends BaseListField<StringField> {

    public static final String DEFAULT_STING_FIELD_NAME = "strings";
    private Callable<StringField> newString;

    public ListImpl(String fieldName) {
      super(fieldName);
      newString = StringField::new;
    }

    public ListImpl() {
      this(DEFAULT_STING_FIELD_NAME);
    }

    @Override
    public Callable<StringField> getCreateListEntryCallable() {
      return newString;
    }

    @Override
    public ListImpl useDefaultRequired() {
      newString =
          () -> {
            StringField newStringField = new StringField();
            newStringField.isRequired(true);
            return newStringField;
          };

      return this;
    }
  }
}
