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

import com.google.common.collect.ImmutableSet
import org.codice.ddf.admin.api.Field
import org.codice.ddf.admin.api.fields.FunctionField
import org.codice.ddf.admin.common.fields.base.scalar.StringField
import org.codice.ddf.admin.common.fields.test.TestObjectField
import org.codice.ddf.admin.common.report.message.DefaultMessages
import spock.lang.Specification

class BaseFunctionFieldTest extends Specification {

    final List<Object> FUNCTION_PATH = [TestBaseFunctionField.DEFAULT_FIELD_NAME]

    TestBaseFunctionField functionField

    def setup() {
        functionField = new TestBaseFunctionField()
    }

    def 'Function field validates arguments'() {
        setup:
        functionField.failValidation(true)

        when:
        def report = functionField.execute(null, FUNCTION_PATH)

        then:
        report.getErrorMessages().size() == 1
        report.getErrorMessages()[0].getCode() == DefaultMessages.EMPTY_FIELD
        report.getErrorMessages()[0].getPath() == [
                TestBaseFunctionField.DEFAULT_FIELD_NAME, StringField.DEFAULT_STING_FIELD_NAME
        ]
    }

    def 'Setting function arguments populates argument values correctly'() {
        setup:
        functionField.failValidation(false)

        when:
        def value = [
                (StringField.DEFAULT_STING_FIELD_NAME): 'test1',
                (TestObjectField.FIELD_NAME)          : [
                        (StringField.DEFAULT_STING_FIELD_NAME)   : 'test2',
                        (TestObjectField.INNER_OBJECT_FIELD_NAME): [
                                (TestObjectField.SUB_FIELD_OF_INNER_FIELD_NAME): 'test3'
                        ]
                ]
        ]
        functionField.execute(value, FUNCTION_PATH)

        then:
        functionField.getStringArg().getValue() == 'test1'
        ((TestObjectField) functionField.getArguments()[1]).getStringField().getValue() == 'test2'
        ((TestObjectField) functionField.getArguments()[1]).getFields().find {
            (it.getFieldName() == TestObjectField.INNER_OBJECT_FIELD_NAME)
        }.getFields()[0].getValue() == 'test3'
    }

    def 'Returns all the possible error codes correctly from all arguments'(){
        setup:
        functionField.failValidation(false)

        when:
        def errorCodes = functionField.getErrorCodes()
        def arg1Errors = functionField.getArguments()[0].getErrorCodes()
        def arg2Errors = functionField.getArguments()[1].getErrorCodes()
        def functionError = functionField.getFunctionErrorCodes()

        then:
        errorCodes.size() == 6

        errorCodes.contains(DefaultMessages.EMPTY_FIELD)
        errorCodes.contains(DefaultMessages.UNSUPPORTED_ENUM)
        errorCodes.contains(DefaultMessages.MISSING_REQUIRED_FIELD)
        errorCodes.contains(TestBaseFunctionField.FUNCTION_TEST_ERROR)
        errorCodes.contains(TestObjectField.OBJECT_FIELD_TEST_ERROR)
        errorCodes.contains(TestObjectField.InnerTestObjectField.INNER_OBJECT_FIELD_TEST_ERROR)

        errorCodes.containsAll(arg1Errors)
        errorCodes.containsAll(arg2Errors)
        errorCodes.containsAll(functionError)
    }

    class TestBaseFunctionField extends BaseFunctionField<StringField> {

        static String DEFAULT_FIELD_NAME = 'testBaseFunctionField'

        static String ARG_VALUE = 'valid'

        static String FUNCTION_TEST_ERROR = 'FUNCTION_TEST_ERROR'

        public static final StringField RETURN_TYPE = new StringField()

        StringField stringArg

        TestObjectField testObjectField

        TestBaseFunctionField() {
            this(DEFAULT_FIELD_NAME)
        }

        TestBaseFunctionField(String functionName) {
            super(functionName, 'description')
            stringArg = new StringField()
            testObjectField = new TestObjectField()
        }

        void failValidation(boolean failValidation) {
            if (failValidation) {
                stringArg.setValue('')
            } else {
                stringArg.setValue(ARG_VALUE)
            }
        }

        @Override
        StringField getReturnType() {
            return RETURN_TYPE
        }

        @Override
        List<Field> getArguments() {
            return [stringArg, testObjectField]
        }

        @Override
        FunctionField<StringField> newInstance() {
            return new TestBaseFunctionField(DEFAULT_FIELD_NAME)
        }

        @Override
        StringField performFunction() {
            return new StringField('result')
        }

        @Override
        Set<String> getFunctionErrorCodes() {
            return new ImmutableSet.Builder<String>()
                    .add(FUNCTION_TEST_ERROR)
                    .build()
        }
    }
}
