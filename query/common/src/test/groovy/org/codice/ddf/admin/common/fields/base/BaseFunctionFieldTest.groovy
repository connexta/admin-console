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

import org.codice.ddf.admin.api.DataType
import org.codice.ddf.admin.api.fields.FunctionField
import org.codice.ddf.admin.common.fields.base.scalar.StringField
import org.codice.ddf.admin.common.fields.test.TestObjectField
import org.codice.ddf.admin.common.report.message.DefaultMessages
import org.codice.ddf.admin.common.report.message.ErrorMessageImpl
import spock.lang.Specification

class BaseFunctionFieldTest extends Specification {

    BaseFunctionField functionField

    def 'Argument paths are updated when function field name is updated'() {
        setup:
        functionField = new TestBaseFunctionField(true)

        expect:
        functionField.path() == [TestBaseFunctionField.DEFAULT_NAME]
        functionField.getArguments()[0].path() == [TestBaseFunctionField.DEFAULT_NAME, BaseFunctionField.ARGUMENT, StringField.DEFAULT_FIELD_NAME]

        when:
        functionField.pathName('newName')

        then:
        functionField.path() == ['newName']
        functionField.getArguments()[0].path() == ['newName', BaseFunctionField.ARGUMENT, StringField.DEFAULT_FIELD_NAME]

    }

    def 'Function field validates arguments'() {
        setup:
        functionField = new TestBaseFunctionField(true)

        when:
        def report = functionField.getValue()

        then:
        report.messages().size() == 1
        report.messages()[0].getCode() == DefaultMessages.EMPTY_FIELD
        report.messages()[0].getPath() == [TestBaseFunctionField.DEFAULT_NAME, BaseFunctionField.ARGUMENT, StringField.DEFAULT_FIELD_NAME]
    }

    def 'Adding result message has correct path on added message'() {
        setup:
        functionField = new TestBaseFunctionField(false)
        functionField.addResultMessage(new ErrorMessageImpl('test'))

        when:
        def report = functionField.getValue()

        then:
        report.messages().size() == 1
        report.messages()[0].getPath() == [TestBaseFunctionField.DEFAULT_NAME]
    }

    def 'Setting function arguments populates argument values correctly'() {
        setup:
        functionField = new TestBaseFunctionField(false)

        when:
        def value = [
                (StringField.DEFAULT_FIELD_NAME): 'test1',
                (TestObjectField.FIELD_NAME)    : [(StringField.DEFAULT_FIELD_NAME): 'test2', (TestObjectField.INNER_OBJECT_FIELD_NAME): [(TestObjectField.SUB_FIELD_OF_INNER_FIELD_NAME): 'test3']]
        ]
        functionField.setValue(value)

        then:
        functionField.getStringArg().getValue() == 'test1'
        ((TestObjectField) functionField.getArguments()[1]).getStringField().getValue() == 'test2'
        ((TestObjectField) functionField.getArguments()[1]).getFields().find {
            (it.fieldName() == TestObjectField.INNER_OBJECT_FIELD_NAME)
        }.getFields()[0].getValue() == 'test3'
    }

    def 'Updating path updates arguments paths'() {
        setup:
        functionField = new TestBaseFunctionField(false)

        when:
        functionField.updatePath(['test'])

        then:
        functionField.path() == ['test', TestBaseFunctionField.DEFAULT_NAME]
        functionField.getArguments()[0].path() == ['test', TestBaseFunctionField.DEFAULT_NAME, FunctionField.ARGUMENT, StringField.DEFAULT_FIELD_NAME]
    }

    def 'Returns all the possible error codes correctly from all arguments'(){
        setup:
        functionField = new TestBaseFunctionField(false)

        when:
        def errorCodes = functionField.getErrorCodes()
        def arg1Errors = functionField.getArguments()[0].getErrorCodes()
        def arg2Errors = functionField.getArguments()[1].getErrorCodes()
        def functionError = functionField.getFunctionErrorCodes()

        then:
        errorCodes.size() == 6
        errorCodes.containsAll(arg1Errors)
        errorCodes.containsAll(arg2Errors)
        errorCodes.containsAll(functionError)
    }

    class TestBaseFunctionField extends BaseFunctionField<StringField> {

        static String DEFAULT_NAME = 'testBaseFunctionField'

        static String ARG_VALUE = 'valid'

        static String FUNCTION_TEST_ERROR = 'FUNCTION_TEST_ERROR'

        public static final StringField RETURN_TYPE = new StringField()

        StringField stringArg

        TestObjectField testObjectField

        TestBaseFunctionField(boolean failValidation) {
            this(DEFAULT_NAME, failValidation)
        }

        TestBaseFunctionField(String functionName, boolean failValidation) {
            super(functionName, 'description')
            stringArg = new StringField()
            testObjectField = new TestObjectField()
            if (failValidation) {
                stringArg.setValue('')
            } else {
                stringArg.setValue(ARG_VALUE)
            }
            updateArgumentPaths()
        }

        @Override
        StringField getReturnType() {
            return RETURN_TYPE
        }

        @Override
        List<DataType> getArguments() {
            return [stringArg, testObjectField]
        }

        @Override
        FunctionField<StringField> newInstance() {
            return new TestBaseFunctionField(DEFAULT_NAME)
        }

        @Override
        StringField performFunction() {
            return new StringField('result')
        }

        @Override
        Set<String> getFunctionErrorCodes() {
            Set<String> errorCodes = super.getFunctionErrorCodes()
            errorCodes.add(FUNCTION_TEST_ERROR)
            return errorCodes
        }
    }
}
