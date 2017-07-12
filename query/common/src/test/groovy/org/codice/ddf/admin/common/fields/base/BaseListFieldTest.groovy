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

    def 'The path of fields in ListFields are the ListFields path + their own'() {
        when:
        StringField.ListImpl listField = new StringField.ListImpl(TEST_LIST_FIELD_NAME)
        listField.add(new StringField())
        listField.add(new StringField())

        then:
        listField.path() == [TEST_LIST_FIELD_NAME]
        listField.getList().get(0).path() == [TEST_LIST_FIELD_NAME, '0']
        listField.getList().get(1).path() == [TEST_LIST_FIELD_NAME, '1']
    }

    def 'The path of ObjectFields and their inner fields in ListFields are correct'() {
        when:
        TestObjectField.ListImpl listField = new TestObjectField.ListImpl(TEST_LIST_FIELD_NAME)
        listField.add(new TestObjectField())

        def innerObjectField = listField.getList()[0].getFields().find {
            (it.fieldName() == TestObjectField.INNER_OBJECT_FIELD_NAME)
        }

        List<String> parentPath = listField.path()
        List<String> objectFieldPath = listField.getList()[0].path()
        List<String> innerObjectFieldPath = innerObjectField.path()
        List<String> subFieldOfInnerObjectFieldPath = ((ObjectField) innerObjectField).getFields()[0].path()

        then:
        parentPath == [TEST_LIST_FIELD_NAME]
        objectFieldPath == [parentPath, '0'].flatten()
        innerObjectFieldPath == [objectFieldPath, TestObjectField.INNER_OBJECT_FIELD_NAME].flatten()
        subFieldOfInnerObjectFieldPath == [innerObjectFieldPath, TestObjectField.SUB_FIELD_OF_INNER_FIELD_NAME].flatten()
    }

    def 'Newly added required elements match the requirement of the ListFields field type'() {
        setup:
        StringField.ListImpl listField = new StringField.ListImpl(TEST_LIST_FIELD_NAME).useDefaultRequired()

        when:
        listField.add(new StringField())

        then:
        listField.getList().get(0).isRequired()
    }

    def 'Validation fails when list elements fail to validate'() {
        setup:
        StringField.ListImpl listField = new StringField.ListImpl(TEST_LIST_FIELD_NAME).useDefaultRequired()
        def element1 = new StringField('element1')
        element1.setValue('')
        def element2 = new StringField('element2')
        element2.setValue('valid')
        def element3 = new StringField('element3')

        listField.addAll([element1, element2, element3])

        when:
        def validationMsgs = listField.validate()

        then:
        validationMsgs.size() == 2
        listField.path() == [TEST_LIST_FIELD_NAME]
        validationMsgs[0].getCode() == DefaultMessages.EMPTY_FIELD
        validationMsgs[0].getPath() == [TEST_LIST_FIELD_NAME, '0']
        validationMsgs[1].getCode() == DefaultMessages.MISSING_REQUIRED_FIELD
        validationMsgs[1].getPath() == [TEST_LIST_FIELD_NAME, '2']
    }

    def 'Setting null or empty list value clears the list'() {
        setup:
        StringField.ListImpl listField = new StringField.ListImpl(TEST_LIST_FIELD_NAME)

        when:
        listField.add(new StringField())

        then:
        listField.getList().size() == 1

        when:
        listField.setValue(value)

        then:
        listField.getList().size() == 0

        where:
        value << [null, []]
    }

    def 'Updating list field name updates list elements paths'() {
        setup:
        StringField.ListImpl listField = new StringField.ListImpl(TEST_LIST_FIELD_NAME)
        listField.add(new StringField())

        expect:
        listField.path() == [TEST_LIST_FIELD_NAME]
        listField.getList()[0].path() == [TEST_LIST_FIELD_NAME, '0']

        when:
        listField.pathName('newName')

        then:
        listField.path() == ['newName']
        listField.getList()[0].path() == ['newName', '0']
    }

    def 'Set list values'() {
        setup:
        StringField.ListImpl listField = new StringField.ListImpl(TEST_LIST_FIELD_NAME)

        when:
        listField.setValue(['string1', 'string2'])

        then:
        listField.getList()[0].getValue() == 'string1'
        listField.getList()[0].path() == [TEST_LIST_FIELD_NAME, '0']
        listField.getList()[1].getValue() == 'string2'
        listField.getList()[1].path() == [TEST_LIST_FIELD_NAME, '1']
    }

    def 'Returns all the possible error codes correctly'(){
        setup:
        def emptyFieldElement = new StringField('emptyFieldElement')
        emptyFieldElement.setValue('')

        def missingFieldElement = new StringField('missingFieldElement')

        StringField.ListImpl listField = new StringField.ListImpl(TEST_LIST_FIELD_NAME).useDefaultRequired()
        listField.addAll([emptyFieldElement, missingFieldElement])

        when:
        def errorCodes = listField.getErrorCodes()
        def validationMsgs = listField.validate()

        then:
        errorCodes.size() == 2
        errorCodes.contains(validationMsgs[0].getCode())
        errorCodes.contains(validationMsgs[1].getCode())
    }
}
