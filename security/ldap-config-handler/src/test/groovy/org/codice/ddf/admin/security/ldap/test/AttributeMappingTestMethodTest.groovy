package org.codice.ddf.admin.security.ldap.test

import org.codice.ddf.admin.api.config.ldap.LdapConfiguration
import org.codice.ddf.admin.api.handler.report.Report
import org.codice.ddf.admin.configurator.ConfigReader
import org.forgerock.opendj.ldap.Connection
import spock.lang.Specification

import static org.codice.ddf.admin.api.services.PolicyManagerServiceProperties.STS_CLAIMS_CONFIGURATION_CONFIG_ID
import static org.codice.ddf.admin.api.services.PolicyManagerServiceProperties.STS_CLAIMS_PROPS_KEY_CLAIMS
import static org.codice.ddf.admin.security.ldap.LdapConnectionResult.CANNOT_CONNECT

class AttributeMappingTestMethodTest extends Specification {
    private ldapTestingCommons
    private connection
    private connectionAttempt
    private configReader
    private tester

    def setup() {
        ldapTestingCommons = Mock(LdapTestingCommons)
        connection = Mock(Connection)
        connectionAttempt = Mock(LdapTestingCommons.LdapConnectionAttempt)

        configReader = Mock(ConfigReader)
        configReader.getConfig(STS_CLAIMS_CONFIGURATION_CONFIG_ID) >>
                [(STS_CLAIMS_PROPS_KEY_CLAIMS): ['a', 'b', 'c'] as String[]]
        tester = new AttributeMappingTestMethod(ldapTestingCommons, configReader)
    }

    def 'fail due to failed connection'() {
        setup:
        def configuration = Mock(LdapConfiguration)

        when:
        Report report = tester.test(configuration)

        then: 'fail due to failed connection'
        1 * configuration.attributeMappings() >> [a: 'a', b: 'b']
        1 * ldapTestingCommons.bindUserToLdapConnection(configuration) >> connectionAttempt
        _ * connectionAttempt.result() >> CANNOT_CONNECT
        report.containsUnsuccessfulMessages()
    }


    def 'test stagetest'() {
        setup:
        def configuration = Mock(LdapConfiguration)

        when:
        Report report = tester.test(configuration)

        then: 'fail to find claim'
        _ * configuration.attributeMappings() >> [a: 'a', b: 'b', unk: 'z']
        report.containsFailureMessages()

        // Mocking out LDAP functionality is challenging and will be skiped at this time
    }
}
