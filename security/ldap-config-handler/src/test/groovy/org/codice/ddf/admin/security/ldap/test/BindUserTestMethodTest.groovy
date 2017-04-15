package org.codice.ddf.admin.security.ldap.test

import org.codice.ddf.admin.api.config.ldap.LdapConfiguration
import org.codice.ddf.admin.api.handler.ConfigurationMessage
import org.codice.ddf.admin.api.handler.report.Report
import org.codice.ddf.admin.security.ldap.LdapConnectionResult
import org.forgerock.opendj.ldap.Connection
import spock.lang.Specification

class BindUserTestMethodTest extends Specification {
    def 'test stagetest with simple bindings'() {
        setup:
        def configuration = Mock(LdapConfiguration)
        configuration.bindUserMethod() >> "Simple"
        def ldapTestingCommons = Mock(LdapTestingCommons)
        def connectionAttempt = Mock(LdapTestingCommons.LdapConnectionAttempt)

        connectionAttempt.connection() >> Mock(Connection)
        ldapTestingCommons.bindUserToLdapConnection(configuration) >> connectionAttempt

        def tester = new BindUserTestMethod(ldapTestingCommons)

        when:
        Report report = tester.test(configuration)

        then:
        _ * connectionAttempt.result() >> LdapConnectionResult.SUCCESSFUL_BIND
        !report.containsFailureMessages()

        when:
        report = tester.test(configuration)

        then:
        _ * connectionAttempt.result() >> LdapConnectionResult.CANNOT_BIND
        report.containsFailureMessages()
    }

    def 'test stagetest with md5 bindings'() {
        setup:
        def configuration = Mock(LdapConfiguration)
        configuration.bindUserMethod() >> "Digest MD5 SASL"
        def ldapTestingCommons = Mock(LdapTestingCommons)
        def message = Mock(ConfigurationMessage)
        message.type() >> ConfigurationMessage.MessageType.FAILURE

        def connectionAttempt = Mock(LdapTestingCommons.LdapConnectionAttempt)

        connectionAttempt.connection() >> Mock(Connection)
        ldapTestingCommons.bindUserToLdapConnection(configuration) >> connectionAttempt

        def tester = new BindUserTestMethod(ldapTestingCommons)

        when:
        Report report = tester.test(configuration)

        then: 'pass'
        _ * connectionAttempt.result() >> LdapConnectionResult.SUCCESSFUL_BIND
        !report.containsFailureMessages()

        when:
        report = tester.test(configuration)

        then: 'fail binding'
        _ * connectionAttempt.result() >> LdapConnectionResult.CANNOT_BIND
        report.containsFailureMessages()
    }
}
