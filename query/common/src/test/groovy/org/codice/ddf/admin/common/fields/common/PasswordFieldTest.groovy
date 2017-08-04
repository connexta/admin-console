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

import spock.lang.Specification

class PasswordFieldTest extends Specification{

    static String REAL_PASSWORD = "admin"

    PasswordField passwordField

    def setup(){
        passwordField = new PasswordField()
    }

    def 'Real password is not returned when getValue() is called and is not markAsInternalProcess'(){
        setup:
        passwordField.setValue(REAL_PASSWORD)

        when:
        String password = passwordField.getValue()

        then:
        password != REAL_PASSWORD
    }

    def 'Real password is returned when getRealPassword() is called'(){
        setup:
        passwordField.setValue(REAL_PASSWORD)

        when:
        String password = passwordField.getRealPassword()

        then:
        password == REAL_PASSWORD
    }

    def 'Real password is returned when password is markAsInternalProcess'(){
        setup:
        passwordField.setValue(REAL_PASSWORD)
        passwordField.alwaysReturnPassword()

        when:
        String password = passwordField.getValue()

        then:
        password == REAL_PASSWORD
    }
}
