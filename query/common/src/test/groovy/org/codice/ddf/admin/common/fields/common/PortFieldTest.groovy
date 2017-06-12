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

class PortFieldTest extends Specification {

    PortField portField

    def setup() {
        portField = new PortField()
    }

    def 'Valid port range'() {
        setup:
        portField.setValue(port)

        when:
        def validationMsgs = portField.validate()

        then:
        validationMsgs.size() == 0

        where:
        port << [1, 65535]
    }

    def 'Invalid port range'() {
        setup:
        portField.setValue(port)

        when:
        def validationMsgs = portField.validate()

        then:
        validationMsgs.size() == 1
        validationMsgs[0].getCode() == DefaultMessages.INVALID_PORT_RANGE
        validationMsgs[0].getPath() == [PortField.DEFAULT_FIELD_NAME]

        where:
        port << [0, 65536]

    }
}
