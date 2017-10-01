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
import org.codice.ddf.admin.common.fields.test.TestEnumField
import org.codice.ddf.admin.common.report.message.DefaultMessages
import spock.lang.Specification

class BaseEnumFieldTest extends Specification {

    Field enumField

    def setup() {
        enumField = new TestEnumField()
        enumField.setPath([TestEnumField.DEFAULT_FIELD_NAME])
    }

    def 'Validation success'() {
        setup:
        enumField.setValue(TestEnumField.EnumA.ENUM_A)

        when:
        def validationmsgs = enumField.validate()

        then:
        validationmsgs.size() == 0
    }

    def 'Invalid enum value gives unsupported enum error when validated'() {
        setup:
        enumField.setValue('notValidEnum')

        when:
        def validationMsgs = enumField.validate()

        then:
        validationMsgs.size() == 1
        validationMsgs.get(0).getCode() == DefaultMessages.UNSUPPORTED_ENUM
        validationMsgs.get(0).getPath() == [TestEnumField.DEFAULT_FIELD_NAME]
    }

    def 'Returns all the possible error codes correctly'(){
        setup:
        Field invalidEnumField = new TestEnumField()
        invalidEnumField.setValue('notValidEnum')

        Field missingEnumField = new TestEnumField()
        missingEnumField.isRequired(true)

        when:
        def errorCodes = invalidEnumField.getErrorCodes()
        def invalidEnumFieldValidation = invalidEnumField.validate()
        def missingEnumFieldValidation = missingEnumField.validate()

        then:
        errorCodes.size() == 2
        errorCodes.contains(invalidEnumFieldValidation.get(0).getCode())
        errorCodes.contains(missingEnumFieldValidation.get(0).getCode())
    }
}
