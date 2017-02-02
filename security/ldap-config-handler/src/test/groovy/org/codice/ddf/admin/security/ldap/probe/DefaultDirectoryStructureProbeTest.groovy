package org.codice.ddf.admin.security.ldap.probe

import org.codice.ddf.admin.api.config.ldap.LdapConfiguration
import org.codice.ddf.admin.api.handler.report.ProbeReport
import org.codice.ddf.admin.security.ldap.LdapConnectionResult
import org.codice.ddf.admin.security.ldap.test.LdapTestingCommons
import spock.lang.Specification

/**
 * At present, it is only reasonable to test the failure cases because {@code ServerGuesser}
 * factory methods are static.
 */
class DefaultDirectoryStructureProbeTest extends Specification {
    def 'test stageprobe failure cases'() {
        setup:
        def configuration = Mock(LdapConfiguration)
        def ldapTestingCommons = Mock(LdapTestingCommons)
        def connectionAttempt = Mock(LdapTestingCommons.LdapConnectionAttempt)
        ldapTestingCommons.bindUserToLdapConnection(configuration) >> connectionAttempt
        def probe = new DefaultDirectoryStructureProbe(ldapTestingCommons)

        when:
        ProbeReport report = probe.probe(configuration)

        then: 'no response from failed bind'
        _ * connectionAttempt.result() >> LdapConnectionResult.CANNOT_CONNECT
        report.containsUnsuccessfulMessages()
    }
}
