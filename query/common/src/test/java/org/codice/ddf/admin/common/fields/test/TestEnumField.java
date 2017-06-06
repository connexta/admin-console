/**
 * Copyright (c) Codice Foundation
 * <p>
 * This is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details. A copy of the GNU Lesser General Public License
 * is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 **/
package org.codice.ddf.admin.common.fields.test;

import org.codice.ddf.admin.common.fields.base.BaseEnumField;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;

import com.google.common.collect.ImmutableList;

public class TestEnumField extends BaseEnumField<String> {
    public static final String TEST_FIELD_NAME = "enumeration";

    public static final String TEST_TYPE_NAME = "Enumeration";

    public static final String TEST_DESCRIPTION = "Sample enum for testing purposes.";

    public static final String ENUM_A = "ENUM_A";

    public static final String ENUM_B = "ENUM_B";

    public TestEnumField() {
        this(null);
    }

    protected TestEnumField(StringField enumValue) {
        super(TEST_FIELD_NAME,
                TEST_TYPE_NAME,
                TEST_DESCRIPTION,
                ImmutableList.of(new EnumA(), new EnumB()),
                enumValue);
    }

    protected static final class EnumA extends StringField {

        EnumA() {
            super(ENUM_A, ENUM_A, TEST_DESCRIPTION);
        }

        @Override
        public String getValue() {
            return ENUM_A;
        }
    }

    protected static final class EnumB extends StringField {

        EnumB() {
            super(ENUM_B, ENUM_B, TEST_DESCRIPTION);
        }

        @Override
        public String getValue() {
            return ENUM_B;
        }
    }
}
