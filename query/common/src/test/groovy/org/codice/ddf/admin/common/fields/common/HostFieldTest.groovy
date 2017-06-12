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

class HostFieldTest extends Specification {

    HostField hostField

    static PORT_FIELD_PATH = [HostField.DEFAULT_FIELD_NAME, PortField.DEFAULT_FIELD_NAME]

    static HOSTFIELD_FIELD_PATH = [HostField.DEFAULT_FIELD_NAME, HostnameField.DEFAULT_FIELD_NAME]

    def setup() {
        hostField = new HostField()
    }

    def 'Fail validation when missing required fields'() {
        setup:
        hostField.isRequired(true)

        when:
        def validationMsgs = hostField.validate()

        then:
        validationMsgs.size() == 2
        validationMsgs.count {
            it.getCode() == DefaultMessages.MISSING_REQUIRED_FIELD
        } == 2
        validationMsgs*.getPath() == [HOSTFIELD_FIELD_PATH, PORT_FIELD_PATH]
    }

    def 'Missing required port field when host is required and hostname is provided'() {
        setup:
        hostField.isRequired(true)
        hostField.hostname('localhost')

        when:
        def validationMsgs = hostField.validate()

        then:
        validationMsgs.size() == 1
        validationMsgs.get(0).getCode() == DefaultMessages.MISSING_REQUIRED_FIELD
        validationMsgs.get(0).getPath() == PORT_FIELD_PATH
    }

    def 'Missing required hostname field when host is required and port is provided'() {
        setup:
        hostField.isRequired(true)
        hostField.port(8993)

        when:
        def validationMsgs = hostField.validate()

        then:
        validationMsgs.size() == 1
        validationMsgs.get(0).getCode() == DefaultMessages.MISSING_REQUIRED_FIELD
        validationMsgs.get(0).getPath() == HOSTFIELD_FIELD_PATH
    }
}
