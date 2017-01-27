package org.codice.ddf.admin.api.validation

import spock.lang.Specification
import spock.lang.Unroll

import static org.codice.ddf.admin.api.handler.ConfigurationMessage.INVALID_FIELD
import static org.codice.ddf.admin.api.handler.ConfigurationMessage.MISSING_REQUIRED_FIELD
import static org.codice.ddf.admin.api.validation.LdapValidationUtils.*

class LdapValidationUtilsTest extends Specification {
    def configFieldId = 'testField'

    @Unroll
    def 'test validate encryption methods'() {
        when:
        def errors = validateEncryptionMethod(input, configFieldId)

        then:
        errors.size() == errorSubtype.size()
        if (!errors.empty) {
            errors[0].configFieldId() == configFieldId
            errors*.subtype() == errorSubtype
        }

        where:
        input                   | errorSubtype
        null                    | [MISSING_REQUIRED_FIELD]
        ''                      | [MISSING_REQUIRED_FIELD]
        'foobar'                | [INVALID_FIELD]
        'xxldaps'               | [INVALID_FIELD]
        'ldapsxx'               | [INVALID_FIELD]
        'tls'                   | [INVALID_FIELD]
        LDAPS                   | []
        LDAPS.toLowerCase()     | []
        LDAPS.toUpperCase()     | []
        START_TLS               | []
        START_TLS.toLowerCase() | []
        START_TLS.toUpperCase() | []
        NONE                    | []
        NONE.toLowerCase()      | []
        NONE.toUpperCase()      | []
    }

    @Unroll
    def 'test validate dn'() {
        when:
        def errors = validateDn(input, configFieldId)

        then:
        errors.size() == errorSubtype.size()
        if (!errors.empty) {
            errors[0].configFieldId() == configFieldId
            errors*.subtype() == errorSubtype
        }

        where:
        input                            | errorSubtype
        null                             | [MISSING_REQUIRED_FIELD]
        ''                               | [MISSING_REQUIRED_FIELD]
        '   '                            | [MISSING_REQUIRED_FIELD]
        'xyz'                            | [INVALID_FIELD]
        '=xyz'                           | [INVALID_FIELD]
        ',xyz=zzz'                       | [INVALID_FIELD]
        ',xyz=zzz,'                      | [INVALID_FIELD]
        'xyz=zzz,a'                      | [INVALID_FIELD]
        'ou=users,dc=example,dc=org,x=,' | [INVALID_FIELD]
        'xyz=zzz,a=b'                    | []
        'xyz=====zzz'                    | []
        'xyz=zzz,'                       | []
        'ou=users,dc=example,dc='        | []
    }

    @Unroll
    def 'test validate bind user'() {
        when:
        def errors = validateBindUserMethod(input, configFieldId)

        then:
        errors.size() == errorSubtype.size()
        if (!errors.empty) {
            errors[0].configFieldId() == configFieldId
            errors*.subtype() == errorSubtype
        }

        where:
        input                         | errorSubtype
        ''                            | [MISSING_REQUIRED_FIELD]
        ' '                           | [MISSING_REQUIRED_FIELD]
        'x'                           | [INVALID_FIELD]
        DIGEST_MD5_SASL               | []
        DIGEST_MD5_SASL.toLowerCase() | []
        DIGEST_MD5_SASL.toUpperCase() | []
        SIMPLE                        | []
        SIMPLE.toLowerCase()          | []
        SIMPLE.toUpperCase()          | []
    }

    @Unroll
    def 'test ldap query'() {
        when:
        def errors = validateLdapQuery(input, configFieldId)

        then:
        errors.size() == errorSubtype.size()
        if (!errors.empty) {
            errors[0].configFieldId() == configFieldId
            errors*.subtype() == errorSubtype
        }

        where:
        input                       | errorSubtype
        ''                          | [MISSING_REQUIRED_FIELD]
        ' '                         | [MISSING_REQUIRED_FIELD]
        'hello'                     | [INVALID_FIELD]
        '{hello=foobar}'            | [INVALID_FIELD]
        '(hello=foobar'             | [INVALID_FIELD]
        '((hello=foobar)'           | [INVALID_FIELD]
        '(&hello=foobar)'           | [INVALID_FIELD]
        '(&(hello=foobar)'          | [INVALID_FIELD]
        'hello=foobar'              | []
        '(hello=foobar)'            | []
        '(&(hello=foobar)(x=y))'    | []
        '(&(!(hello=foobar))(x=y))' | []
        'hello=foobar)'             | []
        '(&(hello=foobar))'         | []
    }

    @Unroll
    def 'test validate ldap use case'() {
        when:
        def errors = validateLdapUseCase(input, configFieldId)

        then:
        errors.empty || errors.size() == errorSubtype.size()
        if (!errors.empty) {
            errors[0].configFieldId() == configFieldId
            errors*.subtype() == errorSubtype
        }

        where:
        input                                            | errorSubtype
        ''                                               | [MISSING_REQUIRED_FIELD]
        ' '                                              | [MISSING_REQUIRED_FIELD]
        'x'                                              | [INVALID_FIELD]
        AUTHENTICATION                                   | []
        AUTHENTICATION.toLowerCase()                     | []
        AUTHENTICATION.toUpperCase()                     | []
        ATTRIBUTE_STORE                                  | []
        ATTRIBUTE_STORE.toLowerCase()                    | []
        ATTRIBUTE_STORE.toUpperCase()                    | []
        AUTHENTICATION_AND_ATTRIBUTE_STORE               | []
        AUTHENTICATION_AND_ATTRIBUTE_STORE.toLowerCase() | []
        AUTHENTICATION_AND_ATTRIBUTE_STORE.toUpperCase() | []
    }

    @Unroll
    def 'test validate ldap type'() {
        when:
        def errors = validateLdapType(input, configFieldId)

        then:
        errors.size() == errorSubtype.size()
        if (!errors.empty) {
            errors[0].configFieldId() == configFieldId
            errors*.subtype() == errorSubtype
        }

        where:
        input                          | errorSubtype
        ''                             | [MISSING_REQUIRED_FIELD]
        ' '                            | [MISSING_REQUIRED_FIELD]
        'x'                            | [INVALID_FIELD]
        ACTIVE_DIRECTORY               | []
        ACTIVE_DIRECTORY.toLowerCase() | []
        ACTIVE_DIRECTORY.toUpperCase() | []
        OPEN_LDAP                      | []
        OPEN_LDAP.toLowerCase()        | []
        OPEN_LDAP.toUpperCase()        | []
        OPEN_DJ                        | []
        OPEN_DJ.toLowerCase()          | []
        OPEN_DJ.toUpperCase()          | []
        EMBEDDED                       | []
        EMBEDDED.toLowerCase()         | []
        EMBEDDED.toUpperCase()         | []
        UNKNOWN                        | []
        UNKNOWN.toLowerCase()          | []
        UNKNOWN.toUpperCase()          | []
    }
}
