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
 **/
package org.codice.ddf.admin.ldap.discover

import org.codice.ddf.admin.api.ConfiguratorSuite
import org.codice.ddf.admin.api.fields.ListField
import org.codice.ddf.admin.common.fields.base.BaseFunctionField
import org.codice.ddf.admin.common.services.ServiceCommons
import org.codice.ddf.admin.ldap.LdapTestingCommons
import org.codice.ddf.admin.ldap.commons.LdapServiceCommons
import org.codice.ddf.admin.ldap.fields.config.LdapConfigurationField
import org.codice.ddf.admin.security.common.services.LdapClaimsHandlerServiceProperties
import org.codice.ddf.admin.security.common.services.LdapLoginServiceProperties
import org.codice.ddf.internal.admin.configurator.actions.ManagedServiceActions
import org.codice.ddf.internal.admin.configurator.actions.PropertyActions
import spock.lang.Specification

class GetLdapConfigurationsSpec extends Specification {

    BaseFunctionField getLdapConfigurations

    ManagedServiceActions managedServiceActions

    PropertyActions propertyActions

    ConfiguratorSuite configuratorSuite

    def setup() {
        propertyActions = Mock(PropertyActions)
        managedServiceActions = Mock(ManagedServiceActions)
        configuratorSuite = Mock(ConfiguratorSuite)
        configuratorSuite.propertyActions >> propertyActions
        configuratorSuite.managedServiceActions >> managedServiceActions
        getLdapConfigurations = new GetLdapConfigurations(configuratorSuite)
    }

    def 'Retrieved LDAP configurations have flag password'() {
        setup:
        managedServiceActions.read(LdapLoginServiceProperties.LDAP_LOGIN_MANAGED_SERVICE_FACTORY_PID) >> getTestLdapServiceProps()
        managedServiceActions.read(LdapClaimsHandlerServiceProperties.LDAP_CLAIMS_HANDLER_MANAGED_SERVICE_FACTORY_PID) >> getTestLdapServiceProps()

        when:
        def report = getLdapConfigurations.getValue()
        def configs = (ListField<LdapConfigurationField>) report.result()

        then:
        configs != null
        configs.getList().size() == 2
        configs.getList()*.bindUserInfoField()*.credentialsField()*.password() as List ==
                [ServiceCommons.FLAG_PASSWORD, ServiceCommons.FLAG_PASSWORD]
    }

    private getTestLdapServiceProps() {
        def config = new LdapConfigurationField()
                .bindUserInfo(LdapTestingCommons.simpleBindInfo().password('notTheFlagPassword'))
                .connection(LdapTestingCommons.noEncryptionLdapConnectionInfo())

        return [
                'somePid': new LdapServiceCommons(configuratorSuite).ldapConfigToLdapClaimsHandlerService(config, "/some/path")
        ]
    }
}
