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

import org.codice.ddf.admin.api.Field
import org.codice.ddf.admin.api.fields.ObjectField
import org.codice.ddf.admin.common.fields.test.TestHiddenField
import org.codice.ddf.admin.common.fields.test.TestObjectField
import org.codice.ddf.admin.common.report.message.DefaultMessages
import spock.lang.Specification

class BaseObjectFieldTest extends Specification {

    private static String REAL_VALUE = "admin"

    TestObjectField topLevelField

    def setup() {
        topLevelField = new TestObjectField()
        topLevelField.setPath([TestObjectField.FIELD_NAME])
    }

    def 'ObjectFields inner fields correctly validated'() {
        setup:
        ((Field) topLevelField.getInnerObjectField().getFields()[0]).isRequired(true)

        when:
        def validationMsgs = topLevelField.validate()

        then:
        validationMsgs.size() == 1
        validationMsgs.get(0).getCode() == DefaultMessages.MISSING_REQUIRED_FIELD
        validationMsgs.get(0).getPath() == [TestObjectField.FIELD_NAME, TestObjectField.INNER_OBJECT_FIELD_NAME, TestObjectField.SUB_FIELD_OF_INNER_FIELD_NAME]
    }

    def 'Field paths of nested ObjectFields are correct order'() {
        when:
        def innerField = topLevelField.getFields().find {
            (it.getFieldName() == TestObjectField.INNER_OBJECT_FIELD_NAME)
        }
        def subFieldOfInnerField = ((ObjectField) innerField).getFields()[0]

        List<String> topLevelFieldPath = topLevelField.getPath()
        List<String> innerFieldPath = innerField.getPath()
        List<String> subFieldOfInnerFieldPath = subFieldOfInnerField.getPath()

        then:
        topLevelFieldPath == [TestObjectField.FIELD_NAME]
        innerFieldPath == [topLevelFieldPath, TestObjectField.INNER_OBJECT_FIELD_NAME].flatten()
        subFieldOfInnerFieldPath == [innerFieldPath, TestObjectField.SUB_FIELD_OF_INNER_FIELD_NAME].flatten()
    }

    def 'Setting values of nested object fields'() {
        setup:
        def subFieldOfInnerField = ((ObjectField) topLevelField.getFields()[5]).getFields()[0]

        expect:
        subFieldOfInnerField.getValue() == null

        when:
        def value = [
                (TestObjectField.INNER_OBJECT_FIELD_NAME): [(TestObjectField.SUB_FIELD_OF_INNER_FIELD_NAME): 'valueChange']
        ]
        topLevelField.setValue(value)

        then:
        subFieldOfInnerField.getValue() == 'valueChange'
    }

    def 'Returns all the possible error codes correctly from inner fields'(){
        when:
        def errorCodes = topLevelField.getErrorCodes()
        def field1 = topLevelField.getFields()[0].getErrorCodes()
        def field2 = topLevelField.getFields()[1].getErrorCodes()
        def field3 = topLevelField.getFields()[2].getErrorCodes()
        def field4 = topLevelField.getFields()[3].getErrorCodes()
        def field5 = topLevelField.getFields()[4].getErrorCodes()
        def field6 = topLevelField.getFields()[5].getErrorCodes()

        then:
        errorCodes.size() == 5

        errorCodes.contains(DefaultMessages.EMPTY_FIELD)
        errorCodes.contains(DefaultMessages.UNSUPPORTED_ENUM)
        errorCodes.contains(DefaultMessages.MISSING_REQUIRED_FIELD)
        errorCodes.contains(TestObjectField.OBJECT_FIELD_TEST_ERROR)
        errorCodes.contains(TestObjectField.InnerTestObjectField.INNER_OBJECT_FIELD_TEST_ERROR)

        errorCodes.containsAll(field1)
        errorCodes.containsAll(field2)
        errorCodes.containsAll(field3)
        errorCodes.containsAll(field4)
        errorCodes.containsAll(field5)
        errorCodes.containsAll(field6)
    }

    def 'Successfully masks hidden fields'() {
        when:
        topLevelField.setHiddenField(REAL_VALUE)

        then:
        topLevelField.getSanitizedValue().find {
            (it.getKey() == TestHiddenField.HIDDEN_FIELD_NAME)
        }.value == TestHiddenField.HIDDEN_FLAG
    }

    def 'Successfully masks inner hidden fields'() {
        when:
        TestObjectField.InnerTestObjectField innerField = topLevelField.getFields().find {
            (it.getFieldName() == TestObjectField.INNER_OBJECT_FIELD_NAME)
        }
        innerField.setHiddenField(REAL_VALUE)

        then:
        innerField.getSanitizedValue().find {
            (it.getKey() == TestHiddenField.HIDDEN_FIELD_NAME)
        }.value == TestHiddenField.HIDDEN_FLAG
    }
}
