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
package org.codice.ddf.admin.graphql.test.provider;

import java.util.Arrays;
import java.util.List;

import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.common.fields.base.BaseObjectField;
import org.codice.ddf.admin.common.fields.base.ListFieldImpl;
import org.codice.ddf.admin.common.fields.base.scalar.BooleanField;
import org.codice.ddf.admin.common.fields.base.scalar.IntegerField;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;

import com.google.common.collect.ImmutableList;

public class TestObject extends BaseObjectField {

    public static final String FIELD_NAME = "testObj";

    public static final Integer SAMPLE_INTEGER_VALUE = 999;
    public static final boolean SAMPLE_BOOLEAN_VALUE = true;
    public static final String SAMPLE_STRING_VALUE = "SAMPLE_STRING";
    public static final List<String> SAMPLE_LIST_VALUE = Arrays.asList("entry1", "entry2", "entry3");
    public static final String SAMPLE_ENUM = TestEnum.EnumA.ENUM_A;

    private IntegerField integerField;
    private BooleanField booleanField;
    private StringField stringField;
    private ListFieldImpl<StringField> listField;
    private TestEnum enumField;


    public TestObject() {
        super(FIELD_NAME, "TestObject", "A sample object containing all supported base types.");
        integerField = new IntegerField();
        booleanField = new BooleanField();
        stringField = new StringField();
        listField = new ListFieldImpl<>(new StringField().isRequired(true));
        enumField = new TestEnum();
        updateInnerFieldPaths();
    }

    public IntegerField getIntegerField() {
        return integerField;
    }

    public BooleanField getBooleanField() {
        return booleanField;
    }

    public StringField getStringField() {
        return stringField;
    }

    public ListFieldImpl<StringField> getListField() {
        return listField;
    }

    public TestEnum getEnumField() {
        return enumField;
    }

    public TestObject setInteger(Integer val) {
        this.integerField.setValue(val);
        return this;
    }

    public TestObject setBoolean(boolean val) {
        booleanField.setValue(val);
        return this;
    }

    public TestObject setString(String val) {
        stringField.setValue(val);
        return this;
    }

    public TestObject setList(List<String> strs) {
        listField.setValue(strs);
        return this;
    }

    public TestObject setEnum(String val) {
        this.enumField.setValue(val);
        return this;
    }

    public static TestObject createSampleTestObject() {
        return new TestObject().setInteger(SAMPLE_INTEGER_VALUE)
                .setBoolean(SAMPLE_BOOLEAN_VALUE)
                .setString(SAMPLE_STRING_VALUE)
                .setList(SAMPLE_LIST_VALUE)
                .setEnum(SAMPLE_ENUM);
    }
    @Override
    public List<Field> getFields() {
        return ImmutableList.of(integerField, booleanField, stringField, listField, enumField);
    }
}
