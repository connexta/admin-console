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
package org.codice.ddf.admin.common.fields.base

import com.google.common.collect.ImmutableList

import org.codice.ddf.admin.api.fields.Field
import org.codice.ddf.admin.api.fields.ObjectField
import spock.lang.Specification

class BaseFieldTest extends Specification {

    static final OBJECT_FIELD_NAME1 = "mockObjectField1"

    static final OBJECT_FIELD_NAME2 = "mockObjectField2"

    static final FIELD_NAME = "mockField"

    BaseObjectField field

    def setup() {
       field = new TestObjectField(OBJECT_FIELD_NAME1, "MockObjectField1", "Description of MockObjectField1")
    }

    def 'test field paths of nested ObjectFields is correct order'() {
        when:
        def field1 = field.getFields().get(0)
        def field2 = ((ObjectField) field1).getFields().get(0)

        then:
        field.path() == [OBJECT_FIELD_NAME1]
        field1.path() == [OBJECT_FIELD_NAME1, OBJECT_FIELD_NAME2]
        field2.path() == [OBJECT_FIELD_NAME1, OBJECT_FIELD_NAME2, FIELD_NAME]
    }

    /*
        Test class fields
     */
    class TestObjectField extends BaseObjectField {

        def objectField

        TestObjectField(String fieldName, String fieldTypeName, String description) {
            super(fieldName, fieldTypeName, description)
        }

        @Override
        List<Field> getFields() {
            return ImmutableList.of(objectField)
        }

        @Override
        void initializeFields() {
            objectField = new TestObjectField1(OBJECT_FIELD_NAME2,  "MockObjectField2", "Description of MockObjectField2")
        }
    }

    class TestObjectField1 extends BaseObjectField {

        def field

        TestObjectField1(String fieldName, String fieldTypeName, String description) {
            super(fieldName, fieldTypeName, description)
        }

        @Override
        List<Field> getFields() {
            return ImmutableList.of(field)
        }

        @Override
        void initializeFields() {
            field = new TestBaseField(FIELD_NAME, "MockField", "Description of MockField", Field.FieldBaseType.STRING)
        }
    }

    class TestBaseField extends BaseField {

        TestBaseField(String fieldName, String fieldTypeName, String description, Field.FieldBaseType fieldBaseType) {
            super(fieldName, fieldTypeName, description, fieldBaseType)
        }

        @Override
        Object getValue() {
            return value
        }

        @Override
        void setValue(Object value) {
            this.value = value
        }
    }
}
