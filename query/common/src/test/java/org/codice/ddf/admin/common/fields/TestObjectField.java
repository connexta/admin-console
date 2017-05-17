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

import java.util.Collections;
import java.util.List;

import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.api.report.Message;
import org.codice.ddf.admin.common.fields.base.BaseObjectField;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;
import org.codice.ddf.admin.common.report.message.ErrorMessage;

import com.google.common.collect.ImmutableList;

public class TestObjectField extends BaseObjectField {

    public static final String DEFAULT_FIELD_NAME = "testObjectField";

    public static final String FIELD_TYPE_NAME = "TestObjectField";

    public static final String DESCRIPTION = "TestObjectField Description";

    public static final String TEST_ERROR_CODE = "TEST_ERROR_CODE";

    private InnerTestObjectField testField;

    public TestObjectField() {
        this(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION, FieldBaseType.OBJECT);
    }

    public TestObjectField(String fieldName) {
        this(fieldName, FIELD_TYPE_NAME, DESCRIPTION, FieldBaseType.OBJECT);
    }

    public TestObjectField(String fieldName, String fieldTypeName, String description,
            FieldBaseType baseType) {
        super(fieldName, fieldTypeName, description, baseType);
        testField = new InnerTestObjectField();
        updateInnerFieldPaths();
    }

    @Override
    public List<Field> getFields() {
        return ImmutableList.of(testField);
    }

    public class InnerTestObjectField extends BaseObjectField {

        public static final String DEFAULT_FIELD_NAME = "innerTestObjectField";

        public static final String FIELD_TYPE_NAME = "InnerTestObjectField";

        public static final String DESCRIPTION = "InnerTestObjectField Description";

        public static final String TEST_VALUE = "testValue";

        public static final String SUB_FIELD_FIELD_NAME = "testSubFieldName";

        public StringField subFieldOfInnerField;

        InnerTestObjectField() {
            this(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
        }

        InnerTestObjectField(String fieldName, String fieldTypeName, String description) {
            super(fieldName, fieldTypeName, description);
            subFieldOfInnerField = new StringField(SUB_FIELD_FIELD_NAME);
            subFieldOfInnerField.setValue(TEST_VALUE);
            updateInnerFieldPaths();
        }

        @Override
        public List<Message> validate() {
            return Collections.singletonList(new ErrorMessage(TEST_ERROR_CODE, path()));
        }

        @Override
        public List<Field> getFields() {
            return ImmutableList.of(subFieldOfInnerField);
        }
    }
}
