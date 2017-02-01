package org.codice.ddf.admin.security.ldap.test

import org.codice.ddf.admin.api.config.ldap.LdapConfiguration
import org.codice.ddf.admin.api.handler.report.Report
import org.codice.ddf.admin.security.ldap.LdapConnectionResult
import spock.lang.Specification

class ConnectTestMethodTest extends Specification {
    def 'test stagetest'() {
        setup:
        def configuration = Mock(LdapConfiguration)
        def ldapTestingCommons = Mock(LdapTestingCommons)

        def connectionAttempt = Mock(LdapTestingCommons.LdapConnectionAttempt)
        def tester = new ConnectTestMethod(ldapTestingCommons)

        when:
        Report report = tester.test(configuration)

        then:
        1 * ldapTestingCommons.getLdapConnection(configuration) >> connectionAttempt
        1 * connectionAttempt.result() >> LdapConnectionResult.SUCCESSFUL_CONNECTION
        !report.containsFailureMessages()

        when:
        report = tester.test(configuration)

        then:
        1 * ldapTestingCommons.getLdapConnection(configuration) >> connectionAttempt
        1 * connectionAttempt.result() >> LdapConnectionResult.CANNOT_CONNECT
        report.containsFailureMessages()
    }
}
