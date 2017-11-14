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

import static org.codice.ddf.admin.api.fields.ScalarField.ScalarType.INTEGER;

import java.util.concurrent.Callable;

import org.codice.ddf.admin.common.fields.base.BaseListField;

public class IntegerField extends BaseScalarField<Integer> {

  public static final String DEFAULT_FIELD_NAME = "integer";

  public IntegerField() {
    this(DEFAULT_FIELD_NAME);
  }

  public IntegerField(int value) {
    this();
    setValue(value);
  }

  public IntegerField(String fieldName) {
    super(fieldName, null, null, INTEGER);
  }

  protected IntegerField(String fieldName, String fieldTypeName, String description) {
    super(fieldName, fieldTypeName, description, INTEGER);
  }

  public static class ListImpl extends BaseListField<IntegerField> {

    public static final String DEFAULT_FIELD_NAME = "integers";
    private Callable<IntegerField> newInteger;

    public ListImpl(String fieldName) {
      super(fieldName);
      newInteger = IntegerField::new;
    }

    public ListImpl() {
      this(DEFAULT_FIELD_NAME);
    }

    @Override
    public Callable<IntegerField> getCreateListEntryCallable() {
      return newInteger;
    }

    @Override
    public ListImpl useDefaultRequired() {
      newInteger =
              () -> {
                IntegerField newIntegerField = new IntegerField();
                newIntegerField.isRequired(true);
                return newIntegerField;
              };

      return this;
    }
  }
}
