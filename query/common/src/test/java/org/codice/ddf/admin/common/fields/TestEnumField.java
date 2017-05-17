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
package org.codice.ddf.admin.common.fields;

import org.codice.ddf.admin.common.fields.base.BaseEnumField;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;

import com.google.common.collect.ImmutableList;

public class TestEnumField extends BaseEnumField<String> {
    public static final String TEST_FIELD_NAME = "testFieldName";

    public static final String TEST_TYPE_NAME = "testTypeName";

    public static final String TEST_DESCRIPTION = "testDescription";

    public static final String TEST_TYPE_NAME_1 = "enumValue1";

    public static final String TEST_TYPE_NAME_2 = "enumValue2";

    public TestEnumField() {
        this(null);
    }

    protected TestEnumField(StringField enumValue) {
        super(TEST_FIELD_NAME,
                TEST_TYPE_NAME,
                TEST_DESCRIPTION,
                ImmutableList.of(new TestEnumValue1(), new TestEnumValue2()),
                enumValue);
    }

    protected static final class TestEnumValue1 extends StringField {

        TestEnumValue1() {
            super(TEST_TYPE_NAME_1, TEST_TYPE_NAME_1, TEST_DESCRIPTION);
        }

        @Override
        public String getValue() {
            return TEST_TYPE_NAME_1;
        }
    }

    protected static final class TestEnumValue2 extends StringField {

        TestEnumValue2() {
            super(TEST_TYPE_NAME_2, TEST_TYPE_NAME_2, TEST_DESCRIPTION);
        }

        @Override
        public String getValue() {
            return TEST_TYPE_NAME_2;
        }
    }
}
