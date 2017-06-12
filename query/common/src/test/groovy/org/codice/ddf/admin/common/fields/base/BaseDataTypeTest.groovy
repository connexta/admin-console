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

import org.codice.ddf.admin.common.fields.test.TestDataType
import org.codice.ddf.admin.common.report.message.DefaultMessages
import spock.lang.Specification

class BaseDataTypeTest extends Specification {

    BaseField testField

    def 'Missing required field when required value is not provided'() {
        setup:
        testField = new TestDataType<String>()
        testField.isRequired(true)
        testField.setValue(null)

        when:
        def validationMsgs = testField.validate()

        then:
        validationMsgs.size() == 1
        validationMsgs.get(0).getCode() == DefaultMessages.MISSING_REQUIRED_FIELD
        validationMsgs.get(0).getPath() == [TestDataType.FIELD_NAME]
    }

    def 'Missing required field if value is List and is empty'() {
        setup:
        testField = new TestDataType<List>()
        testField.isRequired(true)
        testField.setValue([])

        when:
        def validationMsgs = testField.validate()

        then:
        validationMsgs.size() == 1
        validationMsgs.get(0).getCode() == DefaultMessages.MISSING_REQUIRED_FIELD
        validationMsgs.get(0).getPath() == [TestDataType.FIELD_NAME]
    }

    def 'Updating field name updates path'() {
        setup:
        testField = new TestDataType<String>()

        expect:
        testField.path() == [TestDataType.FIELD_NAME]

        when:
        testField.fieldName('updatedName')

        then:
        testField.path() == ['updatedName']
    }
}
