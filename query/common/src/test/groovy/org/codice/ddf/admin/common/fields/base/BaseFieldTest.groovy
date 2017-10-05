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

import org.codice.ddf.admin.common.fields.test.TestField
import org.codice.ddf.admin.common.fields.test.TestObjectField
import org.codice.ddf.admin.common.report.message.DefaultMessages
import spock.lang.Specification

class BaseFieldTest extends Specification {

    List<Object> FIELD_PATH = [TestField.FIELD_NAME]
    TestField testField

    def setup() {
        testField = new TestField()
        testField.setPath(FIELD_PATH)
    }

    def 'Missing required field when required value is not provided'() {
        setup:
        testField.isRequired(true)
        testField.setValue(null)

        when:
        def validationMsgs = testField.validate()

        then:
        validationMsgs.size() == 1
        validationMsgs.get(0).getCode() == DefaultMessages.MISSING_REQUIRED_FIELD
        validationMsgs.get(0).getPath() == FIELD_PATH
    }

    def 'Missing required field if value is List and is empty'() {
        setup:
        testField.isRequired(true)
        testField.setValue([])

        when:
        def validationMsgs = testField.validate()

        then:
        validationMsgs.size() == 1
        validationMsgs.get(0).getCode() == DefaultMessages.MISSING_REQUIRED_FIELD
        validationMsgs.get(0).getPath() == FIELD_PATH
    }

    def 'Returns all the possible error codes correctly'(){
        setup:
        testField.isRequired(true)

        when:
        def errorCodes = testField.getErrorCodes()
        def validationMsgs = testField.validate()

        then:
        errorCodes.size() == 1
        errorCodes.contains(validationMsgs.get(0).getCode())
    }
}
