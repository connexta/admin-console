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
package org.codice.ddf.admin.ldap.discover

import org.codice.ddf.admin.ldap.commons.LdapMessages
import org.codice.ddf.admin.ldap.fields.LdapAttributeName
import spock.lang.Specification

class LdapAttributeNameSpec extends Specification {
    LdapAttributeName ldapAttributeName

    def setup() {
        ldapAttributeName = new LdapAttributeName()
    }

    def 'Valid attribute names'() {
        setup:
        ldapAttributeName.setValue(attribute)

        expect:
        ldapAttributeName.validate().isEmpty()

        where:
        attribute << ['correctFormat',
                'ItIs2017',
                'lettersAnd1234',
                'correct-format',
                'still-correct-2015',
                'can-end-with-dash-',
                'CanStartWithUpperCase'
        ]
    }

    def 'Invalid attribute names'() {
        setup:
        ldapAttributeName.setValue(attribute)
        ldapAttributeName.setPath([LdapAttributeName.DEFAULT_FIELD_NAME])

        when:
        def errors = ldapAttributeName.validate()

        then:
        errors.size() == 1
        errors[0].getCode() == LdapMessages.INVALID_USER_ATTRIBUTE;
        errors[0].getPath() == [LdapAttributeName.DEFAULT_FIELD_NAME]

        where:
        attribute << ['no space',
                '-can-not-start-with-dash',
                'speci@! c&@r@cters',
                'no.dots.or_underscores',
                '2017'
        ]
    }

    def 'Returns all the possible error codes correctly'(){
        setup:
        LdapAttributeName invalidAttributeName = new LdapAttributeName()
        invalidAttributeName.setValue('no space')

        LdapAttributeName emptyAttributeName = new LdapAttributeName()
        emptyAttributeName.setValue('')

        LdapAttributeName missingAttributeName = new LdapAttributeName().isRequired(true)

        when:
        def errorCodes = ldapAttributeName.getErrorCodes()
        def invalidAttributeReport = invalidAttributeName.validate()
        def emptyAttributeReport = emptyAttributeName.validate()
        def missingAttributeReport = missingAttributeName.validate()

        then:
        errorCodes.size() == 3
        errorCodes.contains(invalidAttributeReport[0].getCode())
        errorCodes.contains(emptyAttributeReport[0].getCode())
        errorCodes.contains(missingAttributeReport[0].getCode())
    }
}
