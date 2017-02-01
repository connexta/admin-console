package org.codice.ddf.admin.security.ldap.test

import org.codice.ddf.admin.api.config.ldap.LdapConfiguration
import org.codice.ddf.admin.api.configurator.Configurator
import org.codice.ddf.admin.api.handler.report.Report
import spock.lang.Specification

import static org.codice.ddf.admin.api.services.PolicyManagerServiceProperties.STS_CLAIMS_CONFIGURATION_CONFIG_ID
import static org.codice.ddf.admin.api.services.PolicyManagerServiceProperties.STS_CLAIMS_PROPS_KEY_CLAIMS

class AttributeMappingTestMethodTest extends Specification {
    def 'test stagetest'() {
        setup:
        def configurator = Mock(Configurator)
        configurator.getConfig(STS_CLAIMS_CONFIGURATION_CONFIG_ID) >>
                [(STS_CLAIMS_PROPS_KEY_CLAIMS): ['a', 'b', 'c'] as String[]]
        def attributeMappingTestMethod = new AttributeMappingTestMethod(configurator)
        def configuration = Mock(LdapConfiguration)

        when:
        Report report = attributeMappingTestMethod.test(configuration)

        then: 'fail to find claim'
        1 * configuration.attributeMappings() >> [a: 'a', b: 'b', unk: 'z']
        report.containsFailureMessages()

        when:
        report = attributeMappingTestMethod.test(configuration)

        then: 'find claim'
        1 * configuration.attributeMappings() >> [a: 'a', b: 'b']
        !report.containsFailureMessages()
    }
}
