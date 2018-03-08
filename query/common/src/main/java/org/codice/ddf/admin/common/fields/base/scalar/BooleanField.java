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

import static org.codice.ddf.admin.api.fields.ScalarField.ScalarType.BOOLEAN;

public class BooleanField extends BaseScalarField<Boolean> {

  public static final String DEFAULT_BOOLEAN_FIELD_NAME = "boolean";

  private static final BooleanField RETURN_TYPE = new BooleanField();

  private static final BooleanField FALSE = new BooleanField(false);

  private static final BooleanField TRUE = new BooleanField(false);

  public BooleanField(boolean value) {
    this();
    setValue(value);
  }

  public BooleanField() {
    this(DEFAULT_BOOLEAN_FIELD_NAME);
  }

  public BooleanField(String fieldName) {
    this(fieldName, null, null);
  }

  protected BooleanField(String fieldName, String fieldTypeName, String description) {
    super(fieldName, fieldTypeName, description, BOOLEAN);
  }

  public static BooleanField of(boolean value) {
    return new BooleanField(value);
  }

  public static BooleanField returnType() {
    return RETURN_TYPE;
  }
}
