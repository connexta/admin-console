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
import org.codice.ddf.admin.api.fields.ObjectField
import org.codice.ddf.admin.common.fields.test.TestObjectField
import org.codice.ddf.admin.common.report.message.DefaultMessages
import spock.lang.Specification

class BaseObjectFieldTest extends Specification {

    TestObjectField topLevelField

    def setup() {
        topLevelField = new TestObjectField()
    }

    def 'ObjectFields inner fields correctly validated'() {
        setup:
        ((DataType) topLevelField.getInnerObjectField().getFields()[0]).isRequired(true)

        when:
        def validationMsgs = topLevelField.validate()

        then:
        validationMsgs.size() == 1
        validationMsgs.get(0).getCode() == DefaultMessages.MISSING_REQUIRED_FIELD
        validationMsgs.get(0).getPath() == [TestObjectField.FIELD_NAME, TestObjectField.INNER_OBJECT_FIELD_NAME, TestObjectField.SUB_FIELD_OF_INNER_FIELD_NAME]
    }

    def 'All inner fields requirements are true'() {
        when:
        topLevelField.allFieldsRequired(true)
        def innerField = topLevelField.getFields().find {
            (it.fieldName() == TestObjectField.INNER_OBJECT_FIELD_NAME)
        }
        def subFieldOfInnerField = ((ObjectField) innerField).getFields()[0]

        then:
        topLevelField.isRequired()
        innerField.isRequired()
        subFieldOfInnerField.isRequired()
    }

    def 'All inner fields requirements are false'() {
        when:
        topLevelField.isRequired(true)
        topLevelField.allFieldsRequired(false)
        def innerField = topLevelField.getFields().find {
            (it.fieldName() == TestObjectField.INNER_OBJECT_FIELD_NAME)
        }
        def subFieldOfInnerField = ((ObjectField) innerField).getFields()[0]

        then:
        topLevelField.isRequired()
        !innerField.isRequired()
        !subFieldOfInnerField.isRequired()
    }

    def 'Field paths of nested ObjectFields are correct order'() {
        when:
        def innerField = topLevelField.getFields().find {
            (it.fieldName() == TestObjectField.INNER_OBJECT_FIELD_NAME)
        }
        def subFieldOfInnerField = ((ObjectField) innerField).getFields()[0]

        List<String> topLevelFieldPath = topLevelField.path()
        List<String> innerFieldPath = innerField.path()
        List<String> subFieldOfInnerFieldPath = subFieldOfInnerField.path()

        then:
        topLevelFieldPath == [TestObjectField.FIELD_NAME]
        innerFieldPath == [topLevelFieldPath, TestObjectField.INNER_OBJECT_FIELD_NAME].flatten()
        subFieldOfInnerFieldPath == [innerFieldPath, TestObjectField.SUB_FIELD_OF_INNER_FIELD_NAME].flatten()
    }

    def 'Changing ObjectField name correctly updates inner fields paths'() {
        when:
        topLevelField.pathName('newFieldName')
        def innerField = topLevelField.getFields()[5]
        def subFieldOfInnerField = ((ObjectField) innerField).getFields()[0]

        def topLevelFieldPath = topLevelField.path()
        def innerFieldPath = innerField.path()
        def subFieldOfInnerFieldPath = subFieldOfInnerField.path()

        then:
        topLevelFieldPath == ['newFieldName']
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
}
