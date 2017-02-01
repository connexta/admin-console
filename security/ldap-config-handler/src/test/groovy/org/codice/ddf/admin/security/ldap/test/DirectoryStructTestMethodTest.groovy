package org.codice.ddf.admin.security.ldap.test

import com.google.common.collect.ArrayListMultimap
import org.codice.ddf.admin.api.config.ldap.LdapConfiguration
import org.codice.ddf.admin.api.handler.report.Report
import org.forgerock.opendj.ldap.Attribute
import org.forgerock.opendj.ldap.Connection
import org.forgerock.opendj.ldap.SearchScope
import org.forgerock.opendj.ldap.responses.SearchResultEntry
import spock.lang.Specification

import static org.codice.ddf.admin.security.ldap.LdapConnectionResult.CANNOT_CONNECT
import static org.codice.ddf.admin.security.ldap.LdapConnectionResult.SUCCESSFUL_BIND

class DirectoryStructTestMethodTest extends Specification {
    private configuration
    private ldapTestingCommons
    private connection
    private connectionAttempt
    private tester

    def setup() {
        ldapTestingCommons = Mock(LdapTestingCommons)
        connection = Mock(Connection)
        connectionAttempt = Mock(LdapTestingCommons.LdapConnectionAttempt)
        tester = new DirectoryStructTestMethod(this.ldapTestingCommons)

        configuration = Mock(LdapConfiguration)
        configuration.baseUserDn() >> 'cn=users,ou=foo'
        configuration.baseGroupDn() >> 'cn=groups,ou=foo'
        configuration.userNameAttribute() >> 'userNameAttribute'
        configuration.groupObjectClass() >> 'groupObjectClass'
        configuration.groupAttributeHoldingMember() >> 'groupAttributeHoldingMember'
        configuration.memberAttributeReferencedInGroup() >> 'memberAttributeReferencedInGroup'
    }

    def 'fail due to failed connection'() {
        when:
        Report report = tester.test(configuration)

        then: 'fail due to failed connection'
        1 * ldapTestingCommons.bindUserToLdapConnection(configuration) >> connectionAttempt
        _ * connectionAttempt.result() >> CANNOT_CONNECT
        report.containsUnsuccessfulMessages()
    }

    def 'fail with no user base directory and no group base directory'() {
        when:
        Report report = tester.test(configuration)

        then:
        1 * ldapTestingCommons.bindUserToLdapConnection(configuration) >> connectionAttempt
        _ * connectionAttempt.result() >> SUCCESSFUL_BIND
        _ * connectionAttempt.connection() >> connection

        1 * ldapTestingCommons.getLdapQueryResults(connection, 'cn=users,ou=foo', (String) _, (SearchScope) _, 1) >> []
        1 * ldapTestingCommons.getLdapQueryResults(connection, 'cn=groups,ou=foo', (String) _, (SearchScope) _, 1) >> []
        report.containsUnsuccessfulMessages()
    }

    def 'fail with no users in found user base and group base directory'() {
        when:
        Report report = tester.test(configuration)

        then:
        1 * ldapTestingCommons.bindUserToLdapConnection(configuration) >> connectionAttempt
        _ * connectionAttempt.result() >> SUCCESSFUL_BIND
        _ * connectionAttempt.connection() >> connection

        1 * ldapTestingCommons.getLdapQueryResults(connection, 'cn=users,ou=foo', (String) _, (SearchScope) _, 1) >> [Mock(SearchResultEntry)]
        1 * ldapTestingCommons.getLdapQueryResults(connection, 'cn=users,ou=foo', (String) _, (SearchScope) _, 1) >> []

        1 * ldapTestingCommons.getLdapQueryResults(connection, 'cn=groups,ou=foo', (String) _, (SearchScope) _, 1) >> []

        report.containsUnsuccessfulMessages()
    }

    def 'fail users in found user base no group in group base directory'() {
        when:
        Report report = tester.test(configuration)

        then:
        1 * ldapTestingCommons.bindUserToLdapConnection(configuration) >> connectionAttempt
        _ * connectionAttempt.result() >> SUCCESSFUL_BIND
        _ * connectionAttempt.connection() >> connection

        1 * ldapTestingCommons.getLdapQueryResults(connection, 'cn=users,ou=foo', (String) _, (SearchScope) _, 1) >> [Mock(SearchResultEntry)]
        1 * ldapTestingCommons.getLdapQueryResults(connection, 'cn=users,ou=foo', (String) _, (SearchScope) _, 1) >> []

        2 * ldapTestingCommons.getLdapQueryResults(connection, 'cn=groups,ou=foo', (String) _, (SearchScope) _, 1) >> [Mock(SearchResultEntry)]
        1 * ldapTestingCommons.getLdapQueryResults(connection, 'cn=groups,ou=foo', (String) _, (SearchScope) _, 1) >> []

        report.containsUnsuccessfulMessages()
    }

    def 'fail users in found user base, group in group base, no referenced user in group'() {
        setup:
        def group = Mock(SearchResultEntry)
        def groupAttrHoldingMember = Mock(Attribute)
        group.getAttribute('groupAttributeHoldingMember') >> groupAttrHoldingMember
        groupAttrHoldingMember.firstValueAsString() >> 'memberAttributeReferencedInGroup=joe,cn=users,ou=foo'

        when:
        Report report = tester.test(configuration)

        then: 'connection validation'
        1 * ldapTestingCommons.bindUserToLdapConnection(configuration) >> connectionAttempt
        _ * connectionAttempt.result() >> SUCCESSFUL_BIND
        _ * connectionAttempt.connection() >> connection

        then: 'userdir validation'
        1 * ldapTestingCommons.getLdapQueryResults(connection, 'cn=users,ou=foo', (String) _, (SearchScope) _, 1) >> [Mock(SearchResultEntry)]
        1 * ldapTestingCommons.getLdapQueryResults(connection, 'cn=users,ou=foo', (String) _, (SearchScope) _, 1) >> [Mock(SearchResultEntry)]

        then: 'group dir'
        2 * ldapTestingCommons.getLdapQueryResults(connection, 'cn=groups,ou=foo', (String) _, (SearchScope) _, 1) >> [Mock(SearchResultEntry)]

        then: 'check group objectClass, no referenced user'
        1 * ldapTestingCommons.getLdapQueryResults(connection, 'cn=groups,ou=foo', (String) _, (SearchScope) _, 1) >> [group]
        1 * groupAttrHoldingMember.firstValueAsString() >> 'memberAttributeReferencedInGroup=joe,cn=users,ou=foo'
        1 * ldapTestingCommons.getLdapQueryResults(connection, 'cn=users,ou=foo', (String) _, (SearchScope) _, 1) >> []
        report.containsUnsuccessfulMessages()
    }

    def 'pass users found in group'() {
        setup:
        def group = Mock(SearchResultEntry)
        def groupAttrHoldingMember = Mock(Attribute)
        group.getAttribute('groupAttributeHoldingMember') >> groupAttrHoldingMember
        groupAttrHoldingMember.firstValueAsString() >> 'memberAttributeReferencedInGroup=joe,cn=users,ou=foo'

        when:
        Report report = tester.test(configuration)

        then: 'connection validation'
        1 * ldapTestingCommons.bindUserToLdapConnection(configuration) >> connectionAttempt
        _ * connectionAttempt.result() >> SUCCESSFUL_BIND
        _ * connectionAttempt.connection() >> connection

        then: 'userdir validation'
        1 * ldapTestingCommons.getLdapQueryResults(connection, 'cn=users,ou=foo', (String) _, (SearchScope) _, 1) >> [Mock(SearchResultEntry)]
        1 * ldapTestingCommons.getLdapQueryResults(connection, 'cn=users,ou=foo', (String) _, (SearchScope) _, 1) >> [Mock(SearchResultEntry)]

        then: 'group dir'
        2 * ldapTestingCommons.getLdapQueryResults(connection, 'cn=groups,ou=foo', (String) _, (SearchScope) _, 1) >> [Mock(SearchResultEntry)]

        then: 'check group objectClass, no referenced user'
        1 * ldapTestingCommons.getLdapQueryResults(connection, 'cn=groups,ou=foo', (String) _, (SearchScope) _, 1) >> [group]
        1 * groupAttrHoldingMember.firstValueAsString() >> 'memberAttributeReferencedInGroup=joe,cn=users,ou=foo'
        1 * ldapTestingCommons.getLdapQueryResults(connection, 'cn=users,ou=foo', (String) _, (SearchScope) _, 1) >> [Mock(SearchResultEntry)]
        !report.containsUnsuccessfulMessages()
    }

    def 'test checkUsersInDir'() {
        setup:
        def results = ArrayListMultimap.create()

        when:
        tester.checkUsersInDir(configuration, results, connection)

        then: 'find user'
        1 * ldapTestingCommons.getLdapQueryResults(connection, 'cn=users,ou=foo', (String) _, (SearchScope) _, 1) >> [Mock(SearchResultEntry)]
        results.isEmpty()

        when:
        tester.checkUsersInDir(configuration, results, connection)

        then: 'find no user'
        1 * ldapTestingCommons.getLdapQueryResults(connection, 'cn=users,ou=foo', (String) _, (SearchScope) _, 1) >> []
        !results.isEmpty()
    }

    def 'test checkGroupObjectClass'() {
        setup:
        def results = ArrayListMultimap.create()

        when:
        tester.checkGroupObjectClass(configuration, results, connection)

        then: 'find group with objectClass'
        1 * ldapTestingCommons.getLdapQueryResults(connection, 'cn=groups,ou=foo', (String) _, (SearchScope) _, 1) >> [Mock(SearchResultEntry)]
        results.isEmpty()

        when:
        tester.checkGroupObjectClass(configuration, results, connection)

        then: 'find no group with objectClass'
        1 * ldapTestingCommons.getLdapQueryResults(connection, 'cn=groups,ou=foo', (String) _, (SearchScope) _, 1) >> []
        !results.isEmpty()
    }

    def 'test checkReferencedUser'() {
        setup:
        def results = ArrayListMultimap.create()
        def group = Mock(SearchResultEntry)
        def groupAttrHoldingMember = Mock(Attribute)
        group.getAttribute('groupAttributeHoldingMember') >> groupAttrHoldingMember

        when:
        tester.checkReferencedUser(configuration, results, connection, group)

        then: 'user base does not match value in group'
        1 * groupAttrHoldingMember.firstValueAsString() >> 'memberAttributeReferencedInGroup=joe,cn=nomatch,ou=foo'
        // Return a value so its absence doesn't cause failure; we really care about the above user dn not matching
        1 * ldapTestingCommons.getLdapQueryResults(connection, 'cn=users,ou=foo', (String) _, (SearchScope) _, 1) >> [Mock(SearchResultEntry)]
        !results.isEmpty()

        when:
        results.clear()
        tester.checkReferencedUser(configuration, results, connection, group)

        then: 'member attribute does not match'
        1 * groupAttrHoldingMember.firstValueAsString() >> 'nomatch=joe,cn=users,ou=foo'
        // Return a value so its absence doesn't cause failure; we really care about the above user dn not matching
        1 * ldapTestingCommons.getLdapQueryResults(connection, 'cn=users,ou=foo', (String) _, (SearchScope) _, 1) >> [Mock(SearchResultEntry)]
        !results.isEmpty()

        when:
        results.clear()
        tester.checkReferencedUser(configuration, results, connection, group)

        then: 'member not found'
        1 * groupAttrHoldingMember.firstValueAsString() >> 'memberAttributeReferencedInGroup=joe,cn=users,ou=foo'
        1 * ldapTestingCommons.getLdapQueryResults(connection, 'cn=users,ou=foo', (String) _, (SearchScope) _, 1) >> []
        !results.isEmpty()

        when:
        results.clear()
        tester.checkReferencedUser(configuration, results, connection, group)

        then: 'test passes'
        1 * groupAttrHoldingMember.firstValueAsString() >> 'memberAttributeReferencedInGroup=joe,cn=users,ou=foo'
        // Return a value so its absence doesn't cause failure; we really care about the above user dn not matching
        1 * ldapTestingCommons.getLdapQueryResults(connection, 'cn=users,ou=foo', (String) _, (SearchScope) _, 1) >> [Mock(SearchResultEntry)]
        results.isEmpty()
    }
}