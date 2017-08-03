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
import spock.lang.Specification

class CredentialsFieldTest extends Specification {

    CredentialsField credentialsField

    static USERNAME_FIELD_PATH = [CredentialsField.DEFAULT_FIELD_NAME, CredentialsField.USERNAME_FIELD_NAME]

    static PASSWORD_FIELD_PATH = [CredentialsField.DEFAULT_FIELD_NAME, PasswordField.DEFAULT_FIELD_NAME]

    static String REAL_PASSWORD = "admin"

    def setup() {
        credentialsField = new CredentialsField()
    }

    def 'Fail validation when missing required fields'() {
        setup:
        credentialsField.isRequired(true)
        credentialsField.useDefaultRequiredFields()

        when:
        def validationMsgs = credentialsField.validate()

        then:
        validationMsgs.size() == 2
        validationMsgs.count {
            it.getCode() == DefaultMessages.MISSING_REQUIRED_FIELD
        } == 2
        validationMsgs*.getPath() == [USERNAME_FIELD_PATH, PASSWORD_FIELD_PATH]
    }

    def 'Missing required password field when credentials are required and username is provided'() {
        setup:
        credentialsField.useDefaultRequiredFields()
        credentialsField.username('admin')

        when:
        def validationMsgs = credentialsField.validate()

        then:
        validationMsgs.size() == 1
        validationMsgs.get(0).getCode() == DefaultMessages.MISSING_REQUIRED_FIELD
        validationMsgs.get(0).getPath() == PASSWORD_FIELD_PATH
    }

    def 'Missing required username field when credentials are required and password is provided'() {
        setup:
        credentialsField.useDefaultRequiredFields()
        credentialsField.password('admin')

        when:
        def validationMsgs = credentialsField.validate()

        then:
        validationMsgs.size() == 1
        validationMsgs.get(0).getCode() == DefaultMessages.MISSING_REQUIRED_FIELD
        validationMsgs.get(0).getPath() == USERNAME_FIELD_PATH
    }

    def 'Verify if the set password is returned correctly'(){
        setup:
        credentialsField.password(REAL_PASSWORD)

        when:
        String password = credentialsField.realPassword()

        then:
        password == REAL_PASSWORD
    }
}
