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
import org.codice.ddf.admin.common.fields.base.ListFieldImpl;
import org.codice.ddf.admin.common.fields.base.scalar.IntegerField;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;

import com.google.common.collect.ImmutableList;

public class MultiArgFunction extends BaseFunctionField<TestObject> {

    private StringField stringArg;

    private IntegerField integerArg;

    private ListFieldImpl<StringField> listArg;

    private TestEnum enumArg;

    public MultiArgFunction() {
        super("multipleArgs",
                "Generates a TestObject using the various args passed.",
                new TestObject());
        stringArg = new StringField();
        integerArg = new IntegerField();
        listArg = new ListFieldImpl<>(StringField.class);
        enumArg = new TestEnum();
        updateArgumentPaths();
    }

    @Override
    public List<DataType> getArguments() {
        return ImmutableList.of(stringArg, integerArg, listArg, enumArg);
    }

    @Override
    public TestObject performFunction() {
        return new TestObject().setString(stringArg.getValue())
                .setInteger(integerArg.getValue())
                .setList(listArg.getValue())
                .setEnum(enumArg.getValue());
    }

    @Override
    public FunctionField<TestObject> newInstance() {
        return new MultiArgFunction();
    }
}
