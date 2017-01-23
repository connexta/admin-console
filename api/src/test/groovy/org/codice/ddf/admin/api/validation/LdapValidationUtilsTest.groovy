package org.codice.ddf.admin.api.validation

import spock.lang.Specification

class LdapValidationUtilsTest extends Specification {

    def 'test valid LDAP distinguished names'() {
        when:
        def errors = LdapValidationUtils.validateDn(validDn, "id")

        then:
        errors == []

        where:
        validDn                                               | _
        "CN=Jeff Smith"                                       | _
        "CN=Jeff Smith,OU=Sales"                              | _
        "CN=Jeff Smith,OU=Sales,DC=Fake"                      | _
        "CN=Jeff Smith,OU=Sales,DC=Fake,DC=COM"               | _
        "CN=Jeff Smith,OU=Sales,DC=Fake,DC=COM,DC=ANOTHERONE" | _
    }

    def 'test invalid LDAP distinguished names'() {
        when:
        def errors = LdapValidationUtils.validateDn(invalidDn, "id")

        then:
        errors != []

        where:
        invalidDn          | _
        "CN=Jeff Smith,,"  | _
        ",,DC=Fake"        | _
        "invalid"          | _
        "invalid,ou=Sales" | _
        "ou=Sales,invalid" | _
    }
}