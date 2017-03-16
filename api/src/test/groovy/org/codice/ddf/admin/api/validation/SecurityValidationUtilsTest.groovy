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
 */

package org.codice.ddf.admin.api.validation

import spock.lang.Specification

import static org.codice.ddf.admin.api.validation.SecurityValidationUtils.*

class SecurityValidationUtilsTest extends Specification {
    def 'test authtype normalization no exceptions'() {
        expect:
        output == normalizeAuthType(input)

        where:
        input  | output
        'saml' | SAML
        'SAML' | SAML
        'sAmL' | SAML
        'idp'  | IDP_AUTH
        'IDP'  | IDP_AUTH
        'iDp'  | IDP_AUTH
    }

    def 'test authtype normalization with exceptions'() {
        when:
        normalizeAuthType(input)

        then:
        thrown(IllegalArgumentException)

        where:
        input << ['xyz', 'abc', 'def', 'unknown', 'samlx']
    }

    def 'test realm normalization no exceptions'() {
        expect:
        output == normalizeRealm(input)

        where:
        input   | output
        'karaf' | KARAF
        'KaRaF' | KARAF
        'KARAF' | KARAF
        'LdAp'  | LDAP
        'idp'   | IDP_REALM
        'IdP'   | IDP_REALM
    }

    def 'test realm normalization with exceptions'() {
        when:
        normalizeRealm(input)

        then:
        thrown(IllegalArgumentException)

        where:
        input << ['karafXXX', 'idpx', 'ldapppp', 'xyz']
    }
}
