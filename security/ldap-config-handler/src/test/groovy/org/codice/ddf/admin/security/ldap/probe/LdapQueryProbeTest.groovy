package org.codice.ddf.admin.security.ldap.probe

import org.codice.ddf.admin.api.config.ldap.LdapConfiguration
import org.codice.ddf.admin.api.handler.report.ProbeReport
import org.codice.ddf.admin.security.ldap.LdapConnectionResult
import org.codice.ddf.admin.security.ldap.test.LdapTestingCommons
import org.forgerock.opendj.ldap.Attribute
import org.forgerock.opendj.ldap.ByteString
import org.forgerock.opendj.ldap.Connection
import org.forgerock.opendj.ldap.DN
import org.forgerock.opendj.ldap.SearchScope
import org.forgerock.opendj.ldap.responses.SearchResultEntry
import spock.lang.Specification

class LdapQueryProbeTest extends Specification {
    def 'test stageprobe failure cases'() {
        setup:
        def configuration = Mock(LdapConfiguration)
        configuration.queryBase() >> 'base'
        configuration.query() >> 'query'
        def ldapTestingCommons = Mock(LdapTestingCommons)
        def connection = Mock(Connection)
        def connectionAttempt = Mock(LdapTestingCommons.LdapConnectionAttempt)
        connectionAttempt.connection() >> connection

        and:
        ldapTestingCommons.bindUserToLdapConnection(configuration) >> connectionAttempt
        ldapTestingCommons.getLdapQueryResults(connection, 'base', 'query', _ as SearchScope, 50) >> []

        def probe = new LdapQueryProbe(ldapTestingCommons)

        when:
        ProbeReport report = probe.probe(configuration)

        then: 'no response from failed bind'
        _ * connectionAttempt.result() >> LdapConnectionResult.CANNOT_CONNECT
        report.containsUnsuccessfulMessages()

        when:
        report = probe.probe(configuration)

        then: 'no response from no query results'
        1 * connectionAttempt.result() >> LdapConnectionResult.SUCCESSFUL_BIND
        !report.containsUnsuccessfulMessages()
        report.probeResults().get('ldapQueryResults') == []
    }

    def 'test stageprobe success'() {
        setup:
        def configuration = Mock(LdapConfiguration)
        configuration.queryBase() >> 'base'
        configuration.query() >> 'query'
        def ldapTestingCommons = Mock(LdapTestingCommons)
        def connection = Mock(Connection)
        def connectionAttempt = Mock(LdapTestingCommons.LdapConnectionAttempt)
        connectionAttempt.connection() >> connection

        and:
        ldapTestingCommons.bindUserToLdapConnection(configuration) >> connectionAttempt

        and:
        def attribute1 = Mock(Attribute)
        def attribute2 = Mock(Attribute)
        def attribute3 = Mock(Attribute)
        attribute1.getAttributeDescriptionAsString() >> 'password'
        attribute2.getAttributeDescriptionAsString() >> 'foo'
        attribute2.parallelStream() >> {
            [ByteString.valueOf('foo1'), ByteString.valueOf('foo2')].parallelStream()
        }
        attribute3.getAttributeDescriptionAsString() >> 'bar'
        attribute3.parallelStream() >> {
            [ByteString.valueOf('bar1')].parallelStream()
        }

        def name1 = DN.valueOf('cn=name1')
        def name2 = DN.valueOf('cn=name2')

        def result1 = Mock(SearchResultEntry)
        def result2 = Mock(SearchResultEntry)
        result1.getAllAttributes() >> [attribute1, attribute2]
        result1.getName() >> name1
        result2.getAllAttributes() >> [attribute3]
        result2.getName() >> name2

        def probe = new LdapQueryProbe(ldapTestingCommons)

        when:
        ProbeReport report = probe.probe(configuration)
        List<Map<String, String>> queryResults = report.probeResults().get('ldapQueryResults') as List

        then:
        1 * connectionAttempt.result() >> LdapConnectionResult.SUCCESSFUL_BIND
        1 * ldapTestingCommons.getLdapQueryResults(connection, 'base', 'query', _ as SearchScope, 50) >>
                [result1, result2]

        !report.containsUnsuccessfulMessages()
        queryResults.size() == 2
        queryResults*.get('name') as Set == ['cn=name1', 'cn=name2'] as Set
        for (Map<String, String> result : queryResults) {
            if (result.name == 'cn=name1') {
                result.foo as Set == ['foo1', 'foo2'] as Set
            } else {
                result.bar == 'bar1'
            }
        }
    }
}
