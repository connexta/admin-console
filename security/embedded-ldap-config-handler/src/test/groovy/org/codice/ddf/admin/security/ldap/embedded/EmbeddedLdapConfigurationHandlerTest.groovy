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
package org.codice.ddf.admin.security.ldap.embedded

import org.codice.ddf.admin.api.config.ldap.EmbeddedLdapConfiguration
import org.codice.ddf.admin.api.configurator.Configurator
import org.codice.ddf.admin.api.services.EmbeddedLdapServiceProperties
import spock.lang.Specification

class EmbeddedLdapConfigurationHandlerTest extends Specification {

    EmbeddedLdapConfigurationHandler embeddedLdapConfigurationHandler

    def setup() {
        embeddedLdapConfigurationHandler = new EmbeddedLdapConfigurationHandler()
    }

    def 'test getConfigurations() no service properties'() {
        setup:
        def configurator = Mock(Configurator)
        embeddedLdapConfigurationHandler.setConfigurator(configurator)

        when:
        def configurations = embeddedLdapConfigurationHandler.getConfigurations()

        then:
        1 * configurator.getConfig(EmbeddedLdapServiceProperties.EMBEDDED_LDAP_MANAGER_SERVICE_PID) >> null
        configurations == Collections.emptyList()
    }

    def 'test getConfigurations() service properties available'() {
        setup:
        def configurator = Mock(Configurator)
        embeddedLdapConfigurationHandler.setConfigurator(configurator)

        when:
        def configurations = embeddedLdapConfigurationHandler.getConfigurations()

        then:
        1 * configurator.getConfig(EmbeddedLdapServiceProperties.EMBEDDED_LDAP_MANAGER_SERVICE_PID) >> [(EmbeddedLdapConfiguration.EMBEDDED_LDAP_PORT): 123]
        configurations.size() == 1
        configurations.get(0).toString().contains("123")
    }

    def 'test getConfigurationType() is embedded LDAP configuration'() {
        when:
        def configurationType = embeddedLdapConfigurationHandler.getConfigurationType()

        then:
        configurationType.configTypeName() == EmbeddedLdapConfiguration.CONFIGURATION_TYPE
        configurationType.configClass() == EmbeddedLdapConfiguration.class
    }

    def 'test there are no probe or test methods'() {
        expect:
        embeddedLdapConfigurationHandler.getTestMethods() == null
        embeddedLdapConfigurationHandler.getProbeMethods() == null
    }
}