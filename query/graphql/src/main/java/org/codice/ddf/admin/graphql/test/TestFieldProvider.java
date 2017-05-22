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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.common.fields.base.function.BaseFieldProvider;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;

import com.google.common.collect.ImmutableList;

public class TestFieldProvider extends BaseFieldProvider {

    private RecursiveFunction recurse;
    private TestObject testObj;
    private MultiArgFunction multiArgFunc;
    private RequiredArgsFunction reqArgFunc;
    private StringField stringField;

    public TestFieldProvider() {
        super("testing", "Test", "Testing purposes only.");
        recurse = new RecursiveFunction(this);
        testObj = new TestObject().setString("sampleString")
                .setInteger(100)
                .setList(Arrays.asList("entry1", "entry2", "entry3"))
                .setEnum(TestEnum.EnumA.ENUM_A);
        stringField = new StringField();
        stringField.setValue("testValue");
        multiArgFunc = new MultiArgFunction();
        reqArgFunc = new RequiredArgsFunction();
        updateInnerFieldPaths();
    }

    @Override
    public List<Field> getDiscoveryFields() {
        return ImmutableList.of(recurse, testObj, multiArgFunc, reqArgFunc, stringField);
    }

    @Override
    public List<FunctionField> getMutationFunctions() {
        return new ArrayList<>();
    }

}
