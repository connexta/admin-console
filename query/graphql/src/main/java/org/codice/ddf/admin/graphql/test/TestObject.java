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
package org.codice.ddf.admin.graphql.test;

import java.util.List;

import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.common.fields.base.BaseObjectField;
import org.codice.ddf.admin.common.fields.base.ListFieldImpl;
import org.codice.ddf.admin.common.fields.base.scalar.IntegerField;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;

import com.google.common.collect.ImmutableList;

public class TestObject extends BaseObjectField {

    private StringField stringField;

    private IntegerField integerField;

    private ListFieldImpl<StringField> listField;

    private TestEnum enumField;

    public TestObject() {
        super("testObj", "TestObject", "A sample object containing all supported base types.");
        stringField = new StringField();
        integerField = new IntegerField();
        listField = new ListFieldImpl<>(StringField.class);
        enumField = new TestEnum();
        updateInnerFieldPaths();
    }

    public TestObject setString(String val) {
        stringField.setValue(val);
        return this;
    }

    public TestObject setInteger(Integer val) {
        this.integerField.setValue(val);
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

    @Override
    public List<Field> getFields() {
        return ImmutableList.of(stringField, integerField, listField, enumField);
    }
}
