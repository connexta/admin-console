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
package org.codice.ddf.admin.common.fields.test;

import com.google.common.collect.ImmutableList;
import org.codice.ddf.admin.api.fields.EnumValue;
import org.codice.ddf.admin.common.fields.base.BaseEnumField;

public class TestEnumField extends BaseEnumField<String> {
  public static final String DEFAULT_FIELD_NAME = "enumeration";

  public static final String DEFAULT_FIELD_TYPE = "Enumeration";

  public static final String DESCRIPTION = "Sample enum for testing purposes.";

  public TestEnumField() {
    this(null);
  }

  protected TestEnumField(EnumValue<String> enumValue) {
    super(
        DEFAULT_FIELD_NAME,
        DEFAULT_FIELD_TYPE,
        DESCRIPTION,
        ImmutableList.of(new EnumA(), new EnumB()),
        enumValue);
  }

  public static final class EnumA implements EnumValue<String> {

    public static final String ENUM_A = "ENUM_A";

    @Override
    public String enumTitle() {
      return ENUM_A;
    }

    @Override
    public String description() {
      return DESCRIPTION;
    }

    @Override
    public String value() {
      return ENUM_A;
    }
  }

  public static final class EnumB implements EnumValue<String> {

    public static final String ENUM_B = "ENUM_B";

    @Override
    public String enumTitle() {
      return ENUM_B;
    }

    @Override
    public String description() {
      return DESCRIPTION;
    }

    @Override
    public String value() {
      return ENUM_B;
    }
  }
}
