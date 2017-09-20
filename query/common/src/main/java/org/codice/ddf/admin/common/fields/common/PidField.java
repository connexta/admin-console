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

import org.codice.ddf.admin.common.fields.base.scalar.StringField;

public class PidField extends StringField {
  public static final String DEFAULT_FIELD_NAME = "pid";

  public static final String FIELD_TYPE_NAME = "Pid";

  public static final String DESCRIPTION = "A unique id used for persisting.";

  public PidField() {
    super(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
  }

  public PidField(String fieldName) {
    super(fieldName, FIELD_TYPE_NAME, DESCRIPTION);
  }
}
