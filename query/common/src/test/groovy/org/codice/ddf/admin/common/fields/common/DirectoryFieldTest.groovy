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
package org.codice.ddf.admin.common.fields.common

import org.codice.ddf.admin.common.report.message.DefaultMessages
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class DirectoryFieldTest extends Specification {

    DirectoryField dirField

    @Rule
    TemporaryFolder tempFolder
    def dir
    static DIRECTORY_FIELD_PATH = [DirectoryField.DEFAULT_FIELD_NAME]

    def setup() {
        dirField = new DirectoryField()
        dirField.setPath(DIRECTORY_FIELD_PATH)
    }

    def 'Validate directory exists'() {
        setup:
        dir = tempFolder.newFolder()
        dirField.setValue(dir.getPath())
        dirField.validateDirectoryExists()

        when:
        def validationMsgs = dirField.validate()

        then:
        validationMsgs.size() == 0
    }

    def 'Validate directory does not exist'() {
        setup:
        dirField.setValue("/some/random/dir")
        dirField.validateDirectoryExists()

        when:
        def validationMsgs = dirField.validate()

        then:
        validationMsgs.size() == 1
        validationMsgs[0].code == DefaultMessages.DIRECTORY_DOES_NOT_EXIST
        validationMsgs[0].getPath() == DIRECTORY_FIELD_PATH
    }

    def 'Validate empty value when field is required'() {
        setup:
        dirField.isRequired(true)
        dirField.setValue("")

        when:
        def validationMsgs = dirField.validate()

        then:
        validationMsgs.size() == 1
        validationMsgs[0].code == DefaultMessages.EMPTY_FIELD
        validationMsgs[0].getPath() == DIRECTORY_FIELD_PATH
    }

    def 'Validate null value when field is required'() {
        setup:
        dirField.isRequired(true)

        when:
        def validationMsgs = dirField.validate()

        then:
        validationMsgs.size() == 1
        validationMsgs[0].code == DefaultMessages.MISSING_REQUIRED_FIELD
        validationMsgs[0].getPath() == DIRECTORY_FIELD_PATH
    }

    def 'Validate all error codes being returned' () {
        when:
        def allErrorCodes = dirField.getErrorCodes()

        then:
        allErrorCodes.size() == 3
    }
}
