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

class HostnameFieldTest extends Specification {

    HostnameField hostnameField

    static HOSTNAME_FIELD_PATH = [HostnameField.DEFAULT_FIELD_NAME]

    def setup() {
        hostnameField = new HostnameField()
        hostnameField.setPath(HOSTNAME_FIELD_PATH)
    }

    def 'Valid hostnames'() {
        setup:
        hostnameField.setValue(host)

        when:
        def validationMsgs = hostnameField.validate()

        then:
        validationMsgs.isEmpty()

        where:
        host << ['localhost',
                 'localhost.org',
                 'www.localhost.com',
                 'localhost-host.com',
                 'local---host.com',
                 'localhost-h-o-st.com',
                 'as.uk',
                 'LoCaLhOsT.CoM',
                 'z.com',
                 'im.an-example.host.name']
    }

    def 'Invalid hostnames'() {
        setup:
        hostnameField.setValue(host)

        when:
        def validationMsgs = hostnameField.validate()

        then:
        !validationMsgs.isEmpty()
        validationMsgs.size() == 1
        validationMsgs.get(0).getCode() == errorCode
        validationMsgs.get(0).getPath() == fieldPath

        where:
        host                      | fieldPath           | errorCode
        'local host'              | HOSTNAME_FIELD_PATH | DefaultMessages.INVALID_HOSTNAME
        '.localhost'              | HOSTNAME_FIELD_PATH | DefaultMessages.INVALID_HOSTNAME
        'local host.com'          | HOSTNAME_FIELD_PATH | DefaultMessages.INVALID_HOSTNAME
        'http://www.apache.org'   | HOSTNAME_FIELD_PATH | DefaultMessages.INVALID_HOSTNAME
        'localhost  '             | HOSTNAME_FIELD_PATH | DefaultMessages.INVALID_HOSTNAME
    }

    def 'Empty field when hostname provided but empty'() {
        setup:
        hostnameField.setValue('')

        when:
        def validationMsgs = hostnameField.validate()

        then:
        validationMsgs.size() == 1
        validationMsgs.get(0).getCode() == DefaultMessages.EMPTY_FIELD
        validationMsgs.get(0).getPath() == HOSTNAME_FIELD_PATH
    }

    def 'Fail when missing required field'() {
        setup:
        hostnameField.isRequired(true)

        when:
        def validationMsgs = hostnameField.validate()

        then:
        validationMsgs.size() == 1
        validationMsgs.get(0).getCode() == DefaultMessages.MISSING_REQUIRED_FIELD
        validationMsgs.get(0).getPath() == HOSTNAME_FIELD_PATH
    }

    def 'Returns all the possible error codes correctly'(){
        setup:
        HostnameField emptyHostnameField = new HostnameField()
        emptyHostnameField.setValue('')

        HostnameField missingHostnameField = new HostnameField()
        missingHostnameField.isRequired(true)

        HostnameField invalidHostnameField = new HostnameField()
        invalidHostnameField.setValue('invalid host')

        when:
        def errorCodes = hostnameField.getErrorCodes()
        def emptyHostnameFieldValidation = emptyHostnameField.validate()
        def missingHostnameFieldValidation = missingHostnameField.validate()
        def invalidHostnameFieldValidation = invalidHostnameField.validate()

        then:
        errorCodes.size() == 3
        errorCodes.contains(emptyHostnameFieldValidation.get(0).getCode())
        errorCodes.contains(missingHostnameFieldValidation.get(0).getCode())
        errorCodes.contains(invalidHostnameFieldValidation.get(0).getCode())
    }
}
