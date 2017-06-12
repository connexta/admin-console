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
package org.codice.ddf.admin.common.fields.test;

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

import com.google.common.collect.ImmutableList;

public class TestFieldProvider extends BaseFieldProvider {

    public static final String TEST_FUNCTION_NAME = "testing";

    public static final String REQUIRED_ARG_FUNCTION_NAME = "requiredArg";

    public static final String MULTIPLE_ARGS_FUNCTION_NAME = "multipleArgs";

    public static final String RETURN_ERRORS_FUNCTION_NAME = "returnErrors";

    public static final String GET_INT_FUNCTION_NAME = "getInteger";

    public static final String GET_BOOL_FUNCTION_NAME = "getBoolean";

    public static final String GET_STRING_FUNCTION_NAME = "getString";

    public static final String GET_LIST_FUNCTION_NAME = "getList";

    public static final String GET_ENUM_FUNCTION_NAME = "getEnum";

    public static final String PATH_1 = "path";

    public static final String PATH_2 = "path2";

    public static final String PATH_3 = "path3";

    public static final String ARGUMENT_MSG = "ARGUMENT_MSG";

    public static final String RETURN_VALUE_MSG = "RETURN_VALUE_MSG";

    private GetInt getInt;

    private GetBoolean getBoolean;

    private GetString getString;

    private GetList getList;

    private GetEnum getEnum;

    private PathField errorsPath;

    private MultiArgFunction multiArgFunc;

    private RequiredArgsFunction reqArgFunc;

    private SampleMutation sampleMutation;

    public TestFieldProvider() {
        super(TEST_FUNCTION_NAME, "Test", "Testing purposes only.");
        getInt = new GetInt();
        getBoolean = new GetBoolean();
        getString = new GetString();
        getList = new GetList();
        getEnum = new GetEnum();
        errorsPath = new PathField();
        multiArgFunc = new MultiArgFunction();
        reqArgFunc = new RequiredArgsFunction();
        sampleMutation = new SampleMutation();
        updateInnerFieldPaths();
    }

    @Override
    public List<Field> getDiscoveryFields() {
        return ImmutableList.of(getInt,
                getBoolean,
                getString,
                getList,
                getEnum,
                errorsPath,
                multiArgFunc,
                reqArgFunc);
    }

    @Override
    public List<FunctionField> getMutationFunctions() {
        return ImmutableList.of(sampleMutation);
    }

    public static class GetInt extends GetFunctionField<IntegerField> {

        public static final int GET_INT_VALUE = 999;

        public GetInt() {
            super(GET_INT_FUNCTION_NAME, "Returns a sample integer.", IntegerField.class);
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
            super(GET_BOOL_FUNCTION_NAME, "Returns a sample boolean.", BooleanField.class);
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
            super(GET_STRING_FUNCTION_NAME, "Returns a sample string.", StringField.class);
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

    public static class GetList extends GetFunctionField<ListField<TestObjectField>> {

        public static final TestObjectField ENTRY_1_VALUE =
                TestObjectField.createSampleTestObject();

        public static final TestObjectField ENTRY_2_VALUE =
                TestObjectField.createSampleTestObject();

        public static final TestObjectField ENTRY_3_VALUE =
                TestObjectField.createSampleTestObject();

        public GetList() {
            super(GET_LIST_FUNCTION_NAME,
                    "Returns a sample list of sample objects.",
                    new ListFieldImpl<>(TestObjectField.class));
        }

        @Override
        public ListField performFunction() {
            ListFieldImpl<TestObjectField> list = new ListFieldImpl<>(TestObjectField.class);
            list.addAll(Arrays.asList(ENTRY_1_VALUE, ENTRY_2_VALUE, ENTRY_3_VALUE));
            return list;
        }

        @Override
        public FunctionField<ListField<TestObjectField>> newInstance() {
            return new GetList();
        }
    }

    public static class GetEnum extends GetFunctionField<TestEnumField> {

        public static final String GET_ENUM_VALUE = TestEnumField.ENUM_A;

        public GetEnum() {
            super(GET_ENUM_FUNCTION_NAME, "Returns a sample enumerated value.", TestEnumField.class);
        }

        @Override
        public TestEnumField performFunction() {
            TestEnumField enumField = new TestEnumField();
            enumField.setValue(GET_ENUM_VALUE);
            return enumField;
        }

        @Override
        public FunctionField<TestEnumField> newInstance() {
            return new GetEnum();
        }
    }

    public static class RequiredArgsFunction extends BaseFunctionField<TestObjectField> {

        private TestObjectField objArg;

        public RequiredArgsFunction() {
            super(REQUIRED_ARG_FUNCTION_NAME, "Returns the object passed as an argument", new TestObjectField());
            objArg = new TestObjectField();
            objArg.allFieldsRequired(true);
            updateArgumentPaths();
        }

        @Override
        public List<DataType> getArguments() {
            return ImmutableList.of(objArg);
        }

        @Override
        public TestObjectField performFunction() {
            TestObjectField testObject = new TestObjectField();
            testObject.setValue(objArg.getValue());

            return objArg;
        }

        @Override
        public FunctionField<TestObjectField> newInstance() {
            return new RequiredArgsFunction();
        }
    }

    public static class MultiArgFunction extends BaseFunctionField<TestObjectField> {

        private StringField stringArg;

        private IntegerField integerArg;

        private BooleanField booleanArg;

        private ListFieldImpl<StringField> listArg;

        private TestEnumField enumArg;

        public MultiArgFunction() {
            super(MULTIPLE_ARGS_FUNCTION_NAME,
                    "Generates a TestObjectField using the various args passed.",
                    new TestObjectField());
            stringArg = new StringField();
            integerArg = new IntegerField();
            booleanArg = new BooleanField();
            listArg = new ListFieldImpl<>(StringField.class);
            enumArg = new TestEnumField();
            updateArgumentPaths();
        }

        @Override
        public List<DataType> getArguments() {
            return ImmutableList.of(stringArg, integerArg, booleanArg, listArg, enumArg);
        }

        @Override
        public TestObjectField performFunction() {
            return new TestObjectField().setString(stringArg.getValue())
                    .setInteger(integerArg.getValue())
                    .setBoolean(booleanArg.getValue())
                    .setList(listArg.getValue())
                    .setEnum(enumArg.getValue());
        }

        @Override
        public FunctionField<TestObjectField> newInstance() {
            return new MultiArgFunction();
        }
    }

    public static class PathField extends BaseObjectField {

        private PathField2 subPath;

        public PathField() {
            super(PATH_1, "Path", "Sample path");
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
            super(PATH_2, "Path2", "Sample path");
            subPath = new PathField3();
            updateInnerFieldPaths();
        }

        @Override
        public List<Field> getFields() {
            return ImmutableList.of(subPath);
        }
    }

    public static class PathField3 extends BaseObjectField {

        private ReturnErrorsFunction returnErrorsFunction;

        public PathField3() {
            super(PATH_3, "Path3", "Sample path");
            returnErrorsFunction = new ReturnErrorsFunction();
            updateInnerFieldPaths();
        }

        @Override
        public List<Field> getFields() {
            return ImmutableList.of(returnErrorsFunction);
        }
    }

    public static class ReturnErrorsFunction extends BaseFunctionField<TestObjectField> {

        private TestObjectField objectFieldArg;

        public ReturnErrorsFunction() {
            super(RETURN_ERRORS_FUNCTION_NAME,
                    "Return a set of errors about the argument, return value, and general errors.",
                    TestObjectField.class);
            objectFieldArg = new TestObjectField();
            updateArgumentPaths();
        }

        @Override
        public List<DataType> getArguments() {
            return ImmutableList.of(objectFieldArg);
        }

        @Override
        public TestObjectField performFunction() {
            addArgumentMessage(sampleArgumentError(objectFieldArg.path()));
            addArgumentMessage(sampleArgumentError(objectFieldArg.getIntegerField()
                    .path()));
            addArgumentMessage(sampleArgumentError(objectFieldArg.getBooleanField()
                    .path()));
            addArgumentMessage(sampleArgumentError(objectFieldArg.getStringField()
                    .path()));
            addArgumentMessage(sampleArgumentError(objectFieldArg.getListField()
                    .path()));
            addArgumentMessage(sampleArgumentError(objectFieldArg.getListField()
                    .getList()
                    .get(0)
                    .path()));
            addArgumentMessage(sampleArgumentError(objectFieldArg.getEnumField()
                    .path()));

            TestObjectField testObject = TestObjectField.createSampleTestObject();
            addResultMessage(sampleReturnValueError(testObject.path()));
            addResultMessage(sampleReturnValueError(testObject.getIntegerField()
                    .path()));
            addResultMessage(sampleReturnValueError(testObject.getBooleanField()
                    .path()));
            addResultMessage(sampleReturnValueError(testObject.getStringField()
                    .path()));
            addResultMessage(sampleReturnValueError(testObject.getListField()
                    .path()));
            addResultMessage(sampleReturnValueError(testObject.getListField()
                    .getList()
                    .get(0)
                    .path()));
            addResultMessage(sampleReturnValueError(testObject.getEnumField()
                    .path()));
            return testObject;
        }

        @Override
        public FunctionField<TestObjectField> newInstance() {
            return new ReturnErrorsFunction();
        }
    }

    public static ErrorMessage sampleArgumentError(List<String> path) {
        return new ErrorMessage(ARGUMENT_MSG, path);
    }

    public static ErrorMessage sampleReturnValueError(List<String> path) {
        return new ErrorMessage(RETURN_VALUE_MSG, path);
    }

    public static class SampleMutation extends BaseFunctionField<TestObjectField> {

        private TestObjectField objFieldArg;

        public SampleMutation() {
            super("sampleMutation",
                    "Returns the argument. Also returns an error about the argument and an error about the return value.",
                    TestObjectField.class);
            objFieldArg = new TestObjectField();
            updateArgumentPaths();
        }

        @Override
        public List<DataType> getArguments() {
            return ImmutableList.of(objFieldArg);
        }

        @Override
        public TestObjectField performFunction() {
            addArgumentMessage(sampleArgumentError(objFieldArg.path()));
            TestObjectField testObject = TestObjectField.createSampleTestObject();
            addResultMessage(sampleReturnValueError(testObject.path()));
            return testObject;
        }

        @Override
        public FunctionField<TestObjectField> newInstance() {
            return new SampleMutation();
        }
    }
}
