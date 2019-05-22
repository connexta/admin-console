/**
 * Copyright (c) Codice Foundation
 *
 * <p>This is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version.
 *
 * <p>This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details. A copy of the GNU Lesser General Public
 * License is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 */
package org.codice.ddf.admin.common.fields.test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.api.fields.ListField;
import org.codice.ddf.admin.api.fields.ObjectField;
import org.codice.ddf.admin.common.fields.base.BaseFunctionField;
import org.codice.ddf.admin.common.fields.base.BaseObjectField;
import org.codice.ddf.admin.common.fields.base.function.BaseFieldProvider;
import org.codice.ddf.admin.common.fields.base.function.GetFunctionField;
import org.codice.ddf.admin.common.fields.base.scalar.BooleanField;
import org.codice.ddf.admin.common.fields.base.scalar.IntegerField;
import org.codice.ddf.admin.common.fields.base.scalar.LongField;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;
import org.codice.ddf.admin.common.report.message.ErrorMessageImpl;

public class TestFieldProvider extends BaseFieldProvider {

  public static final String LIST_FIELD_NAME = "list";

  public static final String TEST_FUNCTION_NAME = "testing";

  public static final String REQUIRED_ARG_FUNCTION_NAME = "requiredArg";

  public static final String MULTIPLE_ARGS_FUNCTION_NAME = "multipleArgs";

  public static final String RETURN_ERRORS_FUNCTION_NAME = "returnErrors";

  public static final String GET_INT_FUNCTION_NAME = "getInteger";

  public static final String GET_LONG_FUNCTION_NAME = "getLong";

  public static final String GET_BOOL_FUNCTION_NAME = "getBoolean";

  public static final String GET_STRING_FUNCTION_NAME = "getString";

  public static final String GET_LIST_FUNCTION_NAME = "getList";

  public static final String GET_ENUM_FUNCTION_NAME = "getEnum";

  public static final String PATH_1 = "path";

  public static final String PATH_2 = "path2";

  public static final String PATH_3 = "path3";

  public static final String ERROR_MSG = "ERROR_MSG";

  private GetInt getInt;

  private GetLong getLong;

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
    getLong = new GetLong();
    getBoolean = new GetBoolean();
    getString = new GetString();
    getList = new GetList();
    getEnum = new GetEnum();
    errorsPath = new PathField();
    multiArgFunc = new MultiArgFunction();
    reqArgFunc = new RequiredArgsFunction();
    sampleMutation = new SampleMutation();
  }

  @Override
  public List<FunctionField> getDiscoveryFunctions() {
    return ImmutableList.of(
        getInt, getLong, getBoolean, getString, getList, getEnum, multiArgFunc, reqArgFunc);
  }

  @Override
  public List<FunctionField> getMutationFunctions() {
    return ImmutableList.of(sampleMutation);
  }

  @Override
  public List<Field> getFields() {
    return ImmutableList.of(errorsPath);
  }

  public static class GetInt extends GetFunctionField<IntegerField> {

    public static final int GET_INT_VALUE = 999;

    public static final IntegerField RETURN_TYPE = new IntegerField();

    public GetInt() {
      super(GET_INT_FUNCTION_NAME, "Returns a sample integer.");
    }

    @Override
    public IntegerField performFunction() {
      IntegerField integerField = new IntegerField();
      integerField.setValue(GET_INT_VALUE);
      return integerField;
    }

    @Override
    public IntegerField getReturnType() {
      return RETURN_TYPE;
    }

    @Override
    public FunctionField<IntegerField> newInstance() {
      return new GetInt();
    }

    @Override
    public Set<String> getFunctionErrorCodes() {
      return ImmutableSet.of();
    }
  }

  public static class GetLong extends GetFunctionField<LongField> {

    public static final long GET_LONG_VALUE = 999L;

    public static final LongField RETURN_TYPE = new LongField();

    public GetLong() {
      super(GET_LONG_FUNCTION_NAME, "Returns a sample long.");
    }

    @Override
    public LongField performFunction() {
      LongField longField = new LongField();
      longField.setValue(GET_LONG_VALUE);
      return longField;
    }

    @Override
    public LongField getReturnType() {
      return RETURN_TYPE;
    }

    @Override
    public FunctionField<LongField> newInstance() {
      return new GetLong();
    }

    @Override
    public Set<String> getFunctionErrorCodes() {
      return ImmutableSet.of();
    }
  }

  public static class GetBoolean extends GetFunctionField<BooleanField> {

    public static final boolean GET_BOOLEAN_VALUE = true;
    public static final BooleanField RETURN_TYPE = new BooleanField();

    public GetBoolean() {
      super(GET_BOOL_FUNCTION_NAME, "Returns a sample boolean.");
    }

    @Override
    public BooleanField performFunction() {
      BooleanField booleanField = new BooleanField();
      booleanField.setValue(GET_BOOLEAN_VALUE);
      return booleanField;
    }

    @Override
    public BooleanField getReturnType() {
      return RETURN_TYPE;
    }

    @Override
    public FunctionField<BooleanField> newInstance() {
      return new GetBoolean();
    }

    @Override
    public Set<String> getFunctionErrorCodes() {
      return ImmutableSet.of();
    }
  }

  public static class GetString extends GetFunctionField<StringField> {

    public static final String GET_STRING_VALUE = "SAMPLE_STRING";
    public static final StringField RETURN_TYPE = new StringField();

    public GetString() {
      super(GET_STRING_FUNCTION_NAME, "Returns a sample string.");
    }

    @Override
    public StringField performFunction() {
      StringField strField = new StringField();
      strField.setValue(GET_STRING_VALUE);
      return strField;
    }

    @Override
    public StringField getReturnType() {
      return RETURN_TYPE;
    }

    @Override
    public FunctionField<StringField> newInstance() {
      return new GetString();
    }

    @Override
    public Set<String> getFunctionErrorCodes() {
      return ImmutableSet.of();
    }
  }

  public static class GetList extends GetFunctionField<ListField<TestObjectField>> {

    public static final TestObjectField ENTRY_1_VALUE = TestObjectField.createSampleTestObject();

    public static final TestObjectField ENTRY_2_VALUE = TestObjectField.createSampleTestObject();

    public static final TestObjectField ENTRY_3_VALUE = TestObjectField.createSampleTestObject();

    public static final TestObjectField.ListImpl RETURN_TYPE = new TestObjectField.ListImpl();

    public GetList() {
      super(GET_LIST_FUNCTION_NAME, "Returns a sample list of sample objects.");
    }

    @Override
    public ListField performFunction() {
      TestObjectField.ListImpl list = new TestObjectField.ListImpl();
      list.addAll(Arrays.asList(ENTRY_1_VALUE, ENTRY_2_VALUE, ENTRY_3_VALUE));
      return list;
    }

    @Override
    public ListField<TestObjectField> getReturnType() {
      return RETURN_TYPE;
    }

    @Override
    public FunctionField<ListField<TestObjectField>> newInstance() {
      return new GetList();
    }

    @Override
    public Set<String> getFunctionErrorCodes() {
      return ImmutableSet.of();
    }
  }

  public static class GetEnum extends GetFunctionField<TestEnumField> {

    public static final String GET_ENUM_VALUE = TestEnumField.EnumA.ENUM_A;

    public GetEnum() {
      super(GET_ENUM_FUNCTION_NAME, "Returns a sample enumerated value.");
    }

    @Override
    public TestEnumField performFunction() {
      TestEnumField enumField = new TestEnumField();
      enumField.setValue(GET_ENUM_VALUE);
      return enumField;
    }

    @Override
    public TestEnumField getReturnType() {
      return new TestEnumField();
    }

    @Override
    public FunctionField<TestEnumField> newInstance() {
      return new GetEnum();
    }

    @Override
    public Set<String> getFunctionErrorCodes() {
      return ImmutableSet.of();
    }
  }

  public static class RequiredArgsFunction extends BaseFunctionField<TestObjectField> {

    public static final TestObjectField RETURN_TYPE = new TestObjectField();

    private TestObjectField objArg;

    public RequiredArgsFunction() {
      super(REQUIRED_ARG_FUNCTION_NAME, "Returns the object passed as an argument");
      objArg = new TestObjectField();
      allFieldsRequired(true);
    }

    /** Updating all field's requirement */
    private void allFieldsRequired(boolean required) {
      objArg.isRequired(required);
      objArg.getFields().forEach(field -> field.isRequired(required));
      List<Object> objectFields =
          objArg
              .getFields()
              .stream()
              .filter(field -> field instanceof ObjectField)
              .collect(Collectors.toList());

      objectFields.forEach(
          o -> ((ObjectField) o).getFields().forEach(field -> field.isRequired(required)));
    }

    @Override
    public TestObjectField getReturnType() {
      return RETURN_TYPE;
    }

    @Override
    public List<Field> getArguments() {
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

    @Override
    public Set<String> getFunctionErrorCodes() {
      return ImmutableSet.of();
    }
  }

  public static class MultiArgFunction extends BaseFunctionField<TestObjectField> {

    public static final TestObjectField RETURN_TYPE = new TestObjectField();

    private StringField stringArg;

    private IntegerField integerArg;

    private LongField longArg;

    private BooleanField booleanArg;

    private StringField.ListImpl listArg;

    private TestEnumField enumArg;

    public MultiArgFunction() {
      super(
          MULTIPLE_ARGS_FUNCTION_NAME,
          "Generates a TestObjectField using the various args passed.");
      stringArg = new StringField();
      integerArg = new IntegerField();
      longArg = new LongField();
      booleanArg = new BooleanField();
      listArg = new StringField.ListImpl(LIST_FIELD_NAME);
      enumArg = new TestEnumField();
    }

    @Override
    public TestObjectField getReturnType() {
      return RETURN_TYPE;
    }

    @Override
    public List<Field> getArguments() {
      return ImmutableList.of(stringArg, integerArg, longArg, booleanArg, listArg, enumArg);
    }

    @Override
    public TestObjectField performFunction() {
      return new TestObjectField()
          .setString(stringArg.getValue())
          .setInteger(integerArg.getValue())
          .setLong(longArg.getValue())
          .setBoolean(booleanArg.getValue())
          .setList(listArg.getValue())
          .setEnum(enumArg.getValue());
    }

    @Override
    public FunctionField<TestObjectField> newInstance() {
      return new MultiArgFunction();
    }

    @Override
    public Set<String> getFunctionErrorCodes() {
      return ImmutableSet.of();
    }
  }

  public static class PathField extends BaseObjectField {

    private PathField2 subPath;

    public PathField() {
      super(PATH_1, "Path", "Sample path");
      subPath = new PathField2();
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
    }

    @Override
    public List<Field> getFields() {
      return ImmutableList.of(subPath);
    }
  }

  public static class PathField3 extends BaseFieldProvider {

    private ReturnErrorsFunction returnErrorsFunction;

    public PathField3() {
      super(PATH_3, "Path3", "Sample path");
      returnErrorsFunction = new ReturnErrorsFunction();
    }

    @Override
    public List<FunctionField> getDiscoveryFunctions() {
      return ImmutableList.of(returnErrorsFunction);
    }

    @Override
    public List<FunctionField> getMutationFunctions() {
      return ImmutableList.of();
    }
  }

  public static class ReturnErrorsFunction extends BaseFunctionField<TestObjectField> {

    public static final TestObjectField RETURN_TYPE = new TestObjectField();

    private TestObjectField objectFieldArg;

    public ReturnErrorsFunction() {
      super(
          RETURN_ERRORS_FUNCTION_NAME,
          "Return a set of errors about the argument, return value, and general errors.");
      objectFieldArg = new TestObjectField();
    }

    @Override
    public TestObjectField getReturnType() {
      return RETURN_TYPE;
    }

    @Override
    public List<Field> getArguments() {
      return ImmutableList.of(objectFieldArg);
    }

    @Override
    public TestObjectField performFunction() {
      addErrorMessage(sampleError(objectFieldArg.getPath()));
      addErrorMessage(sampleError(objectFieldArg.getIntegerField().getPath()));
      addErrorMessage(sampleError(objectFieldArg.getLongField().getPath()));
      addErrorMessage(sampleError(objectFieldArg.getBooleanField().getPath()));
      addErrorMessage(sampleError(objectFieldArg.getStringField().getPath()));
      addErrorMessage(sampleError(objectFieldArg.getListField().getPath()));
      addErrorMessage(sampleError(objectFieldArg.getListField().getList().get(0).getPath()));
      addErrorMessage(sampleError(objectFieldArg.getEnumField().getPath()));
      return objectFieldArg;
    }

    @Override
    public FunctionField<TestObjectField> newInstance() {
      return new ReturnErrorsFunction();
    }

    @Override
    public Set<String> getFunctionErrorCodes() {
      return ImmutableSet.of(ERROR_MSG);
    }
  }

  public static ErrorMessageImpl sampleError(List<Object> path) {
    return new ErrorMessageImpl(ERROR_MSG, path);
  }

  public static class SampleMutation extends BaseFunctionField<TestObjectField> {

    public static final TestObjectField RETURN_TYPE = new TestObjectField();

    private TestObjectField objFieldArg;

    public SampleMutation() {
      super(
          "sampleMutation",
          "Returns the argument. Also returns an error about the argument and an error about the return value.");
      objFieldArg = new TestObjectField();
    }

    @Override
    public TestObjectField getReturnType() {
      return RETURN_TYPE;
    }

    @Override
    public List<Field> getArguments() {
      return ImmutableList.of(objFieldArg);
    }

    @Override
    public TestObjectField performFunction() {
      addErrorMessage(sampleError(objFieldArg.getPath()));
      return objFieldArg;
    }

    @Override
    public FunctionField<TestObjectField> newInstance() {
      return new SampleMutation();
    }

    @Override
    public Set<String> getFunctionErrorCodes() {
      return ImmutableSet.of(ERROR_MSG);
    }
  }
}
