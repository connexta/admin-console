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

class UrlFieldTest extends Specification {

    static URL_FIELD_PATH = [UrlField.DEFAULT_FIELD_NAME]

    UrlField urlField

    def setup() {
        urlField = new UrlField()
        urlField.setPath(URL_FIELD_PATH)
    }

    def 'Valid url'() {
        setup:
        urlField.setValue(url)

        expect:
        urlField.validate().isEmpty()

        where:
        url << ['http://foo.com/blah_blah',
                'http://foo.com/blah_blah/',
                'http://www.example.com/wpstyle/?p=364',
                'https://www.example.com/foo/?bar=baz&inga=42&quux',
                'http://142.42.1.1/',
                'http://142.42.1.1:8080/',
                'http://code.google.com/events/#&product=browser',
                'http://j.mp',
                'ftp://foo.bar/baz',
                'http://foo.bar/?q=Test%20URL-encoded%20stuff',
                'http://1337.net',
                'http://223.255.255.254',
                'ftp://ftp.is.co.za/rfc/rfc1808.txt',
                'http://localhost:8993',
        ]
    }

    def 'Invalid url'() {
        setup:
        urlField.setValue(url)

        when:
        def validationMsgs = urlField.validate()

        then:
        validationMsgs.size() == 1
        validationMsgs[0].getCode() == code
        validationMsgs[0].getPath() == URL_FIELD_PATH

        where:
        url                       | code
        'http://'                 | DefaultMessages.INVALID_URL
        'http://  test.com'       | DefaultMessages.INVALID_URL
        'htp://test.com'          | DefaultMessages.INVALID_URL
        'htps://test.com'         | DefaultMessages.INVALID_URL
        '://google.com'           | DefaultMessages.INVALID_URL
        'http://localhost:8993  ' | DefaultMessages.INVALID_URL
        '  '                      | DefaultMessages.INVALID_URL
    }

    def 'Returns all the possible error codes correctly'(){
        setup:
        UrlField invalidUrlField = new UrlField()
        invalidUrlField.setValue('http://')

        UrlField emptyUrlField = new UrlField()
        emptyUrlField.setValue('')

        UrlField missingUrlField = new UrlField().isRequired(true)

        when:
        def errorCodes = urlField.getErrorCodes()
        def invalidUrlValidation = invalidUrlField.validate()
        def emptyUrlValidation = emptyUrlField.validate()
        def missingUrlValidation = missingUrlField.validate()

        then:
        errorCodes.size() == 3
        errorCodes.contains(invalidUrlValidation.get(0).getCode())
        errorCodes.contains(emptyUrlValidation.get(0).getCode())
        errorCodes.contains(missingUrlValidation.get(0).getCode())
    }
}
