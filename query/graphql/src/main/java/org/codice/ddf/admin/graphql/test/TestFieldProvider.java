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

import org.codice.ddf.admin.api.DataType;
import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.api.fields.ListField;
import org.codice.ddf.admin.common.fields.base.BaseFunctionField;
import org.codice.ddf.admin.common.fields.base.BaseObjectField;
import org.codice.ddf.admin.common.fields.base.ListFieldImpl;
import org.codice.ddf.admin.common.fields.base.function.BaseFieldProvider;
import org.codice.ddf.admin.common.fields.base.function.GetFunctionField;
import org.codice.ddf.admin.common.fields.base.scalar.BooleanField;
import org.codice.ddf.admin.common.fields.base.scalar.IntegerField;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;
import org.codice.ddf.admin.common.report.message.ErrorMessage;
import org.codice.ddf.admin.common.report.message.WarningMessage;

import com.google.common.collect.ImmutableList;

public class TestFieldProvider extends BaseFieldProvider {

    private GetInt getInt;
    private GetBoolean getBoolean;
    private GetString getString;
    private GetList getList;
    private GetEnum getEnum;
    private PathField errorAndWarningsPath;
    private MultiArgFunction multiArgFunc;
    private RequiredArgsFunction reqArgFunc;
    private SampleMutation sampleMutation;

    public TestFieldProvider() {
        super("testing", "Test", "Testing purposes only.");
        getInt = new GetInt();
        getBoolean = new GetBoolean();
        getString = new GetString();
        getList = new GetList();
        getEnum = new GetEnum();
        errorAndWarningsPath = new PathField();
        multiArgFunc = new MultiArgFunction();
        reqArgFunc = new RequiredArgsFunction();
        sampleMutation = new SampleMutation();
        updateInnerFieldPaths();
    }

    @Override
    public List<Field> getDiscoveryFields() {
        return ImmutableList.of(getInt, getBoolean, getString, getList, getEnum, errorAndWarningsPath, multiArgFunc, reqArgFunc);
    }

    @Override
    public List<FunctionField> getMutationFunctions() {
        return ImmutableList.of(sampleMutation);
    }

    public static class GetInt extends GetFunctionField<IntegerField> {

        public static final int GET_INT_VALUE = 999;

        public GetInt() {
            super("getInteger", "Returns a sample integer.", IntegerField.class);
        }

        @Override
        public IntegerField performFunction() {
            IntegerField integerField = new IntegerField();
            integerField.setValue(GET_INT_VALUE);
            return integerField;
        }

        @Override
        public FunctionField<IntegerField> newInstance() {
            return new GetInt();
        }
    }

    public static class GetBoolean extends GetFunctionField<BooleanField> {

        public static final boolean GET_BOOLEAN_VALUE = true;

        public GetBoolean() {
            super("getBoolean", "Returns a sample boolean.", BooleanField.class);
        }

        @Override
        public BooleanField performFunction() {
            BooleanField booleanField = new BooleanField();
            booleanField.setValue(GET_BOOLEAN_VALUE);
            return booleanField;
        }

        @Override
        public FunctionField<BooleanField> newInstance() {
            return new GetBoolean();
        }
    }

    public static class GetString extends GetFunctionField<StringField> {

        public static final String GET_STRING_VALUE = "SAMPLE_STRING";

        public GetString() {
            super("getString", "Returns a sample string.", StringField.class);
        }

        @Override
        public StringField performFunction() {
            StringField strField = new StringField();
            strField.setValue(GET_STRING_VALUE);
            return strField;
        }

        @Override
        public FunctionField<StringField> newInstance() {
            return new GetString();
        }
    }

    public static class GetList extends GetFunctionField<ListField<TestObject>> {

        public static final TestObject ENTRY_1_VALUE = TestObject.createSampleTestObject();
        public static final TestObject ENTRY_2_VALUE = TestObject.createSampleTestObject();
        public static final TestObject ENTRY_3_VALUE = TestObject.createSampleTestObject();

        public GetList() {
            super("getList", "Returns a sample list of sample objects.", new ListFieldImpl<>(TestObject.class));
        }

        @Override
        public ListField performFunction() {
            ListFieldImpl<TestObject> list = new ListFieldImpl<>(TestObject.class);
            list.addAll(Arrays.asList(ENTRY_1_VALUE, ENTRY_2_VALUE, ENTRY_3_VALUE));
            return list;
        }

        @Override
        public FunctionField<ListField<TestObject>> newInstance() {
            return new GetList();
        }
    }

    public static class GetEnum extends GetFunctionField<TestEnum> {

        public static final String GET_ENUM_VALUE = TestEnum.EnumA.ENUM_A;

        public GetEnum() {
            super("getEnum", "Returns a sample enumerated value.", TestEnum.class);
        }

        @Override
        public TestEnum performFunction() {
            TestEnum enumField = new TestEnum();
            enumField.setValue(GET_ENUM_VALUE);
            return enumField;
        }

        @Override
        public FunctionField<TestEnum> newInstance() {
            return new GetEnum();
        }
    }

    public static class RequiredArgsFunction extends BaseFunctionField<TestObject> {

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

    public static class MultiArgFunction extends BaseFunctionField<TestObject> {

        private StringField stringArg;

        private IntegerField integerArg;

        private BooleanField booleanArg;

        private ListFieldImpl<StringField> listArg;

        private TestEnum enumArg;

        public MultiArgFunction() {
            super("multipleArgs",
                    "Generates a TestObject using the various args passed.",
                    new TestObject());
            stringArg = new StringField();
            integerArg = new IntegerField();
            booleanArg = new BooleanField();
            listArg = new ListFieldImpl<>(StringField.class);
            enumArg = new TestEnum();
            updateArgumentPaths();
        }

        @Override
        public List<DataType> getArguments() {
            return ImmutableList.of(stringArg, integerArg, booleanArg, listArg, enumArg);
        }

        @Override
        public TestObject performFunction() {
            return new TestObject().setString(stringArg.getValue())
                    .setInteger(integerArg.getValue())
                    .setBoolean(booleanArg.getValue())
                    .setList(listArg.getValue())
                    .setEnum(enumArg.getValue());
        }

        @Override
        public FunctionField<TestObject> newInstance() {
            return new MultiArgFunction();
        }
    }

    public static class PathField extends BaseObjectField {

        private PathField2 subPath;

        public PathField() {
            super("path", "Path", "Sample path");
            subPath = new PathField2();
            updateInnerFieldPaths();
        }

        @Override
        public List<Field> getFields() {
            return ImmutableList.of(subPath);
        }
    }

    public static class PathField2 extends BaseObjectField {

        private PathField3 subPath;

        public PathField2() {
            super("path2", "Path2", "Sample path");
            subPath = new PathField3();
            updateInnerFieldPaths();
        }

        @Override
        public List<Field> getFields() {
            return ImmutableList.of(subPath);
        }
    }

    public static class PathField3 extends BaseObjectField {

        private ReturnErrorsAndWarning errorsAndWarningFunction;

        public PathField3() {
            super("path3", "Path3", "Sample path");
            errorsAndWarningFunction = new ReturnErrorsAndWarning();
            updateInnerFieldPaths();
        }

        @Override
        public List<Field> getFields() {
            return ImmutableList.of(errorsAndWarningFunction);
        }
    }

    public static class ReturnErrorsAndWarning extends BaseFunctionField<TestObject> {

        public static final String FUNCTION_NAME = "returnErrorsAndWarnings";

        private TestObject objectFieldArg;

        public ReturnErrorsAndWarning() {
            super(FUNCTION_NAME, "Return a set of errors and warnings about the argument, return value, and general errors.", TestObject.class);
            objectFieldArg = new TestObject();
            updateArgumentPaths();
        }

        @Override
        public List<DataType> getArguments() {
            return ImmutableList.of(objectFieldArg);
        }

        @Override
        public TestObject performFunction() {
            addArgumentMessage(sampleArgumentError(objectFieldArg.path()));
            addArgumentMessage(sampleArgumentError(objectFieldArg.getIntegerField().path()));
            addArgumentMessage(sampleArgumentError(objectFieldArg.getBooleanField().path()));
            addArgumentMessage(sampleArgumentError(objectFieldArg.getStringField().path()));
            addArgumentMessage(sampleArgumentError(objectFieldArg.getListField().path()));
            addArgumentMessage(sampleArgumentError(objectFieldArg.getListField().getList().get(0).path()));
            addArgumentMessage(sampleArgumentError(objectFieldArg.getEnumField().path()));

            TestObject testObject = TestObject.createSampleTestObject();
            addResultMessage(sampleReturnValueWarning());
            addResultMessage(sampleReturnValueError(testObject.path()));
            addResultMessage(sampleReturnValueError(testObject.getIntegerField().path()));
            addResultMessage(sampleReturnValueError(testObject.getBooleanField().path()));
            addResultMessage(sampleReturnValueError(testObject.getStringField().path()));
            addResultMessage(sampleReturnValueError(testObject.getListField().path()));
            addResultMessage(sampleReturnValueError(testObject.getListField().getList().get(0).path()));
            addResultMessage(sampleReturnValueError(testObject.getEnumField().path()));
            return testObject;
        }

        @Override
        public FunctionField<TestObject> newInstance() {
            return new ReturnErrorsAndWarning();
        }
    }

    public static final String ARGUMENT_MSG = "ARGUMENT_MSG";
    public static final String RETURN_VALUE_MSG = "RETURN_VALUE_MSG";

    public static ErrorMessage sampleArgumentError() {
        return sampleArgumentError(new ArrayList<>());
    }

    public static ErrorMessage sampleArgumentError(List<String> path) {
        return new ErrorMessage(ARGUMENT_MSG, path);
    }

    public static WarningMessage sampleReturnValueWarning() {
        return new WarningMessage(RETURN_VALUE_MSG);
    }

    public static ErrorMessage sampleReturnValueError(List<String> path) {
        return new ErrorMessage(RETURN_VALUE_MSG, path);
    }

    public static class SampleMutation extends BaseFunctionField<TestObject> {

        private TestObject objFieldArg;

        public SampleMutation() {
            super("sampleMutation", "Returns the argument. Also returns an error about the argument and an error about the return value.", TestObject.class);
            objFieldArg = new TestObject();
            updateArgumentPaths();
        }

        @Override
        public List<DataType> getArguments() {
            return ImmutableList.of(objFieldArg);
        }

        @Override
        public TestObject performFunction() {
            addArgumentMessage(sampleArgumentError(objFieldArg.path()));
            TestObject testObject = TestObject.createSampleTestObject();
            addResultMessage(sampleReturnValueError(testObject.path()));
            return testObject;
        }

        @Override
        public FunctionField<TestObject> newInstance() {
            return new SampleMutation();
        }
    }
}
