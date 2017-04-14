package org.codice.ddf.admin.security.ldap.probe

import org.codice.ddf.admin.api.config.ldap.LdapConfiguration
import org.codice.ddf.admin.api.handler.report.ProbeReport
import org.codice.ddf.admin.configurator.ConfigReader
import org.codice.ddf.admin.security.ldap.LdapConnectionResult
import org.codice.ddf.admin.security.ldap.test.LdapTestingCommons
import org.forgerock.opendj.ldap.SearchResultReferenceIOException
import org.forgerock.opendj.ldap.responses.SearchResultReference
import spock.lang.Specification

import static org.codice.ddf.admin.api.services.PolicyManagerServiceProperties.STS_CLAIMS_CONFIGURATION_CONFIG_ID
import static org.codice.ddf.admin.api.services.PolicyManagerServiceProperties.STS_CLAIMS_PROPS_KEY_CLAIMS

/**
 * At present, it is only reasonable to test the failure cases because {@code ServerGuesser}
 * factory methods are static.
 */
class SubjectAttributeProbeTest extends Specification {
    def 'test stageprobe failure cases'() {
        setup:
        def configuration = Mock(LdapConfiguration)
        def ldapTestingCommons = Mock(LdapTestingCommons)
        def connectionAttempt = Mock(LdapTestingCommons.LdapConnectionAttempt)
        ldapTestingCommons.bindUserToLdapConnection(configuration) >> connectionAttempt
        def configurator = Mock(ConfigReader)
        def subjectClaims = ['one', 'two', 'three']
        configurator.getConfig(STS_CLAIMS_CONFIGURATION_CONFIG_ID) >>
                [(STS_CLAIMS_PROPS_KEY_CLAIMS): subjectClaims]
        def probe = new SubjectAttributeProbe(ldapTestingCommons, configurator)

        when:
        ProbeReport report = probe.probe(configuration)

        then: 'no response from failed bind'
        1 * connectionAttempt.result() >> LdapConnectionResult.CANNOT_CONNECT
        report.probeResults().get("subjectClaims") == subjectClaims
        !report.probeResults().containsKey("userAttributes")

        when:
        report = probe.probe(configuration)

        then: 'no response from exceptional condition'
        1 * connectionAttempt.result() >> LdapConnectionResult.SUCCESSFUL_BIND
        // Fake this exception because we have no access to the static ServerGuesser methods
        1 * configuration.ldapType() >> {
            throw new SearchResultReferenceIOException(Mock(SearchResultReference))
        }
        1 * configuration.baseGroupDn()
        report.probeResults().get("subjectClaims") == subjectClaims
        !report.probeResults().containsKey("userAttributes")
    }
}
