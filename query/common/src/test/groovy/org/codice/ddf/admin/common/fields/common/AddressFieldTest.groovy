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

class AddressFieldTest extends Specification {

    List<Object> FIELD_PATH = [AddressField.DEFAULT_FIELD_NAME]

    AddressField address

    static URL_FIELD_NAME = UrlField.DEFAULT_FIELD_NAME

    static HOST_FIELD_NAME = HostField.DEFAULT_FIELD_NAME

    static PORT_FIELD_NAME = PortField.DEFAULT_FIELD_NAME

    static HOSTNAME_FIELD_NAME = HostnameField.DEFAULT_FIELD_NAME

    def setup() {
        address = new AddressField()
        address.setPath(FIELD_PATH)
    }

    def 'Successful validation when address is required and URL is provided and the host is not'() {
        setup:
        address.isRequired(true)
        address.url('https://localhost:8993')

        when:
        def validationMsgs = address.validate()

        then:
        validationMsgs.isEmpty()
    }

    def 'Successful validation when address is required and host is provided but URL is not'() {
        setup:
        address.isRequired(true)
        address.hostname('localhost')
        address.port(8993)

        when:
        def validationMsgs = address.validate()

        then:
        validationMsgs.isEmpty()
    }

    def 'Missing required URL field when address is required and no values are provided'() {
        setup:
        address.isRequired(true)

        when:
        def validationMsgs = address.validate()

        then:
        validationMsgs.size() == 1
        validationMsgs.get(0).getCode() == DefaultMessages.MISSING_REQUIRED_FIELD
        validationMsgs.get(0).getPath() == FIELD_PATH +  URL_FIELD_NAME
    }

    def 'Missing required port field when address is required and hostname is provided but URL is not'() {
        setup:
        address.isRequired(true)
        address.hostname('localhost')

        when:
        def validationMsgs = address.validate()

        then:
        address.host().isRequired()
        validationMsgs.size() == 1
        validationMsgs.get(0).getCode() == DefaultMessages.MISSING_REQUIRED_FIELD
        validationMsgs.get(0).getPath() == FIELD_PATH + HOST_FIELD_NAME + PORT_FIELD_NAME
    }

    def 'Missing required hostname field when address is required and port is provided but URL is not'() {
        setup:
        address.isRequired(true)
        address.port(8993)

        when:
        def validationMsgs = address.validate()

        then:
        address.host().isRequired()
        validationMsgs.size() == 1
        validationMsgs.get(0).getCode() == DefaultMessages.MISSING_REQUIRED_FIELD
        validationMsgs.get(0).getPath() == FIELD_PATH + HOST_FIELD_NAME + HOSTNAME_FIELD_NAME
    }
}
