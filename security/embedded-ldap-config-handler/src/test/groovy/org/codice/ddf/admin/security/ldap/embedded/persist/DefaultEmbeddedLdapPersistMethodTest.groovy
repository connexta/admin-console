/**
 * Copyright (c) Codice Foundation
 * <p>
 * This is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details. A copy of the GNU Lesser General Public License
 * is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 */
package org.codice.ddf.admin.security.ldap.embedded.persist

import org.codice.ddf.admin.api.config.ldap.EmbeddedLdapConfiguration
import org.codice.ddf.admin.api.configurator.Configurator
import org.codice.ddf.admin.api.configurator.OperationReport
import org.codice.ddf.admin.api.services.LdapClaimsHandlerServiceProperties
import org.codice.ddf.admin.api.validation.LdapValidationUtils
import spock.lang.Specification

import static org.codice.ddf.admin.api.services.EmbeddedLdapServiceProperties.*
import static org.codice.ddf.admin.api.services.LdapLoginServiceProperties.LDAP_LOGIN_FEATURE

class DefaultEmbeddedLdapPersistMethodTest extends Specification {

    DefaultEmbeddedLdapPersistMethod defaultEmbeddedLdapPersistMethod

    EmbeddedLdapConfiguration embeddedLdapConfiguration

    Configurator configurator

    def setup() {
        configurator = Mock(Configurator) {
            startFeature(_ as String) >> ""
        }

        defaultEmbeddedLdapPersistMethod = new DefaultEmbeddedLdapPersistMethod()
        defaultEmbeddedLdapPersistMethod.setConfigurator(configurator)

        embeddedLdapConfiguration = new EmbeddedLdapConfiguration()
    }

    def 'test DefaultEmbeddedLdapPersistMethod()'() {
        expect:
        defaultEmbeddedLdapPersistMethod.getId() == DefaultEmbeddedLdapPersistMethod.DEFAULT_CONFIGURATIONS_ID
        defaultEmbeddedLdapPersistMethod.getDescription() == DefaultEmbeddedLdapPersistMethod.DESCRIPTION
        defaultEmbeddedLdapPersistMethod.getRequiredFields() == DefaultEmbeddedLdapPersistMethod.REQUIRED_FIELDS
        defaultEmbeddedLdapPersistMethod.getOptionalFields() == null
    }

    def 'test persist(EmbeddedLdapConfiguration) ldap is authentication'() {
        setup:
        embeddedLdapConfiguration.ldapUseCase(LdapValidationUtils.AUTHENTICATION)

        configurator.commit(_ as String, _ as String) >> createMockReport(false)

        when:
        def report = defaultEmbeddedLdapPersistMethod.persist(embeddedLdapConfiguration)

        then:
        1 * configurator.startFeature(EMBEDDED_LDAP_FEATURE)
        1 * configurator.startFeature(LDAP_LOGIN_FEATURE)
        1 * configurator.startFeature(DEFAULT_EMBEDDED_LDAP_LOGIN_CONFIG_FEATURE)

        !report.containsFailureMessages()
    }

    def 'test persist(EmbeddedLdapConfiguration) ldap is attribute store'() {
        setup:
        embeddedLdapConfiguration.ldapUseCase(LdapValidationUtils.ATTRIBUTE_STORE)

        configurator.commit(_ as String, _ as String) >> createMockReport(false)

        when:
        def report = defaultEmbeddedLdapPersistMethod.persist(embeddedLdapConfiguration)

        then:
        1 * configurator.startFeature(EMBEDDED_LDAP_FEATURE)
        1 * configurator.startFeature(LdapClaimsHandlerServiceProperties.LDAP_CLAIMS_HANDLER_FEATURE)
        1 * configurator.startFeature(DEFAULT_EMBEDDED_LDAP_CLAIMS_HANDLER_CONFIG_FEATURE)

        !report.containsFailureMessages()
    }

    def 'test persist(EmbeddedLdapConfiguration) ldap is authentication and attribute store'() {
        setup:
        embeddedLdapConfiguration.ldapUseCase(LdapValidationUtils.AUTHENTICATION_AND_ATTRIBUTE_STORE)

        configurator.commit(_ as String, _ as String) >> createMockReport(false)

        when:
        def report = defaultEmbeddedLdapPersistMethod.persist(embeddedLdapConfiguration)

        then:
        1 * configurator.startFeature(EMBEDDED_LDAP_FEATURE)
        1 * configurator.startFeature(LDAP_LOGIN_FEATURE)
        1 * configurator.startFeature(LdapClaimsHandlerServiceProperties.LDAP_CLAIMS_HANDLER_FEATURE)
        1 * configurator.startFeature(ALL_DEFAULT_EMBEDDED_LDAP_CONFIG_FEATURE)

        !report.containsFailureMessages()
    }

    def 'test persist(EmbeddedLdapConfiguration) report contains failures'() {
        setup:
        embeddedLdapConfiguration.ldapUseCase(LdapValidationUtils.AUTHENTICATION_AND_ATTRIBUTE_STORE)

        configurator.commit(_ as String, _ as String) >> createMockReport(true)

        when:
        def report = defaultEmbeddedLdapPersistMethod.persist(embeddedLdapConfiguration)

        then:
        report.containsFailureMessages()
    }

    def 'test persist(EmbeddedLdapConfiguration) invalid LDAP use case'() {
        setup:
        embeddedLdapConfiguration.ldapUseCase("invalidUseCase")

        when:
        def report = defaultEmbeddedLdapPersistMethod.persist(embeddedLdapConfiguration)

        then:
        report.containsFailureMessages()
    }

    def createMockReport(boolean hasFailedResults) {
        return Mock(OperationReport) {
            containsFailedResults() >> hasFailedResults
        }
    }
}