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

import org.codice.ddf.admin.api.DataType;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.common.fields.base.BaseFunctionField;

import com.google.common.collect.ImmutableList;

public class RequiredArgsFunction extends BaseFunctionField<TestObject> {

    private TestObject objArg;

    public RequiredArgsFunction() {
        super("requiredArg", "Returns the object passed as an argument", new TestObject());
        objArg = new TestObject();
        objArg.allFieldsRequired(true);
        updateArgumentPaths();
    }

    @Override
    public List<DataType> getArguments() {
        return ImmutableList.of(objArg);
    }

    @Override
    public TestObject performFunction() {
        TestObject testObject = new TestObject();
        testObject.setValue(objArg.getValue());
        return objArg;
    }

    @Override
    public FunctionField<TestObject> newInstance() {
        return new RequiredArgsFunction();
    }
}
