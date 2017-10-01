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

import org.codice.ddf.admin.api.fields.ObjectField
import org.codice.ddf.admin.common.fields.base.scalar.StringField
import org.codice.ddf.admin.common.fields.test.TestObjectField
import org.codice.ddf.admin.common.report.message.DefaultMessages
import spock.lang.Specification

class BaseListFieldTest extends Specification {

    static final String TEST_LIST_FIELD_NAME = "testListFieldName"

    List<Object> LIST_FIELD_PATH = [TEST_LIST_FIELD_NAME]
    StringField.ListImpl listField

    def setup() {
        listField = new StringField.ListImpl(TEST_LIST_FIELD_NAME)
        listField.setPath(LIST_FIELD_PATH)
    }

    def 'The path of fields in ListFields are the ListFields path + their own'() {
        when:
        listField.add(new StringField())
        listField.add(new StringField())
        listField.setPath(LIST_FIELD_PATH)

        then:
        listField.getPath() == [TEST_LIST_FIELD_NAME]
        listField.getList().get(0).getPath() == [TEST_LIST_FIELD_NAME, 0]
        listField.getList().get(1).getPath() == [TEST_LIST_FIELD_NAME, 1]
    }

    def 'The path of ObjectFields and their inner fields in ListFields are correct'() {
        when:
        TestObjectField.ListImpl listField = new TestObjectField.ListImpl()
        listField.add(new TestObjectField())
        listField.setPath(LIST_FIELD_PATH)

        def innerObjectField = listField.getList().get(0).getFields().find {
            (it.getFieldName() == TestObjectField.INNER_OBJECT_FIELD_NAME)
        }

        List<String> parentPath = listField.getPath()
        List<String> objectFieldPath = listField.getList()[0].getPath()
        List<String> innerObjectFieldPath = innerObjectField.getPath()
        List<String> subFieldOfInnerObjectFieldPath = ((ObjectField) innerObjectField).getFields()[0].getPath()

        then:
        parentPath == [TEST_LIST_FIELD_NAME]
        objectFieldPath == [parentPath, 0].flatten()
        innerObjectFieldPath == [objectFieldPath, TestObjectField.INNER_OBJECT_FIELD_NAME].flatten()
        subFieldOfInnerObjectFieldPath == [innerObjectFieldPath, TestObjectField.SUB_FIELD_OF_INNER_FIELD_NAME].flatten()
    }

    def 'Newly added required elements match the requirement of the ListFields field type'() {
        setup:
        listField.useDefaultRequired()

        when:
        listField.add(new StringField())
        listField.setPath(LIST_FIELD_PATH)

        then:
        listField.getList().get(0).isRequired()
    }

    def 'Validation fails when list elements fail to validate'() {
        setup:
        listField = new StringField.ListImpl(TEST_LIST_FIELD_NAME).useDefaultRequired()
        def element1 = new StringField('element1')
        element1.setValue('')
        def element2 = new StringField('element2')
        element2.setValue('valid')
        def element3 = new StringField('element3')

        listField.addAll([element1, element2, element3])
        listField.setPath(LIST_FIELD_PATH)

        when:
        def validationMsgs = listField.validate()

        then:
        validationMsgs.size() == 2
        listField.getPath() == [TEST_LIST_FIELD_NAME]
        validationMsgs[0].getCode() == DefaultMessages.EMPTY_FIELD
        validationMsgs[0].getPath() == [TEST_LIST_FIELD_NAME, 0]
        validationMsgs[1].getCode() == DefaultMessages.MISSING_REQUIRED_FIELD
        validationMsgs[1].getPath() == [TEST_LIST_FIELD_NAME, 2]
    }

    def 'Setting null or empty list value clears the list'() {
        when:
        listField.add(new StringField())
        listField.setPath(LIST_FIELD_PATH)

        then:
        listField.getList().size() == 1

        when:
        listField.setValue(value)

        then:
        listField.getList().size() == 0

        where:
        value << [null, []]
    }

    def 'Set list values'() {
        when:
        listField.setValue(['string1', 'string2'])
        listField.setPath([TEST_LIST_FIELD_NAME])

        then:
        listField.getList()[0].getValue() == 'string1'
        listField.getList()[0].getPath() == [TEST_LIST_FIELD_NAME, 0]
        listField.getList()[1].getValue() == 'string2'
        listField.getList()[1].getPath() == [TEST_LIST_FIELD_NAME, 1]
    }

    def 'Returns all the possible error codes correctly'(){
        setup:
        def emptyFieldElement = new StringField('emptyFieldElement')
        emptyFieldElement.setValue('')

        def missingFieldElement = new StringField('missingFieldElement')

        listField.useDefaultRequired()
        listField.addAll([emptyFieldElement, missingFieldElement])
        listField.setPath(LIST_FIELD_PATH)

        when:
        def errorCodes = listField.getErrorCodes()
        def validationMsgs = listField.validate()

        then:
        errorCodes.size() == 2
        errorCodes.contains(validationMsgs[0].getCode())
        errorCodes.contains(validationMsgs[1].getCode())
    }
}
