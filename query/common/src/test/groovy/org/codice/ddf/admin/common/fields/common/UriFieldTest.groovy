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

class UriFieldTest extends Specification {

    static URI_FIELD_PATH = [UriField.DEFAULT_FIELD_NAME]

    UriField uriField

    def setup() {
        uriField = new UriField()
        uriField.setPath(URI_FIELD_PATH)
    }

    def 'Valid URI field'() {
        setup:
        uriField.setValue(uri)

        expect:
        uriField.validate().isEmpty()

        where:
        uri << ['ftp',
                'docs/guide/collections/designfaq.html#28',
                '../../../demo/jfc/SwingSet2/src/SwingSet2.java',
                'file:///~/calendar',
                'mailto:java-net@java.sun.com',
                'news:comp.lang.java',
                'urn:metacard:1234',
                'http://www.ietf.org/rfc/rfc2396.txt',
                'ldap://[2001:db8::7]/c=GB?objectClass?one',
                'mailto:John.Doe@example.com',
                'news:comp.infosystems.www.servers.unix',
                'tel:+1-816-555-1212',
                'telnet://192.0.2.16:80/',
                'urn:oasis:names:specification:docbook:dtd:xml:4.1.2',
                'https://localhost:8993']
    }

    def 'Invalid URI field'() {
        setup:
        uriField.setValue(uri)

        when:
        def validationMsgs = uriField.validate()

        then:
        validationMsgs.size() == 1
        validationMsgs[0].getCode() == DefaultMessages.INVALID_URI
        validationMsgs[0].getPath() == URI_FIELD_PATH

        where:
        uri << ['42SchemaStartsWithNum:test',
                ':noscheme',
                ')(*&^%$#@!:InvalidSchemeChars',
                'emptyPathWithScheme:',
                'schemeWithEmptyPathCantStartWithDoubleSlash://']
    }

    def 'Returns all the possible error codes correctly'(){
        setup:
        UriField invalidUriField = new UriField()
        invalidUriField.setValue('Inv@!id:U&i')

        UriField emptyUriField = new UriField()
        emptyUriField.setValue('')

        UriField missingUriField = new UriField().isRequired(true)

        when:
        def errorCodes = uriField.getErrorCodes()
        def invalidUriValidation = invalidUriField.validate()
        def emptyUriValidation = emptyUriField.validate()
        def missingUriValidation= missingUriField.validate()

        then:
        errorCodes.size() == 3
        errorCodes.contains(invalidUriValidation.get(0).getCode())
        errorCodes.contains(emptyUriValidation.get(0).getCode())
        errorCodes.contains(missingUriValidation.get(0).getCode())
    }
}
