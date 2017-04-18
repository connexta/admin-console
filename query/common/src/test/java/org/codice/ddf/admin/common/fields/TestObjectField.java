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

import java.util.List;

import org.codice.ddf.admin.api.fields.Field;
import org.codice.ddf.admin.common.fields.base.BaseObjectField;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;

import com.google.common.collect.ImmutableList;

public class TestObjectField extends BaseObjectField {

    public static final String DEFAULT_FIELD_NAME = "testObjectField";

    public static final String FIELD_TYPE_NAME = "TestObjectField";

    public static final String DESCRIPTION = "TestObjectField Description";

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
    }

    @Override
    public List<Field> getFields() {
        return ImmutableList.of(testField);
    }

    @Override
    public void initializeFields() {
        testField = new InnerTestObjectField();
    }


    public class InnerTestObjectField extends BaseObjectField {

        public static final String DEFAULT_FIELD_NAME = "innerTestObjectField";

        public static final String FIELD_TYPE_NAME = "InnerTestObjectField";

        public static final String DESCRIPTION = "InnerTestObjectField Description";

        StringField subFieldOfInnerField;

        InnerTestObjectField() {
            this(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
        }

        InnerTestObjectField(String fieldName, String fieldTypeName, String description) {
            super(fieldName, fieldTypeName, description);
        }

        @Override
        public List<Field> getFields() {
            return ImmutableList.of(subFieldOfInnerField);
        }

        @Override
        public void initializeFields() {
            subFieldOfInnerField = new StringField();
        }
    }
}
