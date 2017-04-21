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
package org.codice.ddf.admin.security.common.fields;

import java.util.Map;

import org.codice.ddf.admin.configurator.ConfiguratorFactory;

public class ServiceCommons {

    public static final String LDAP_LOGIN_MANAGED_SERVICE_FACTORY_PID = "Ldap_Login_Config";

    public static final String LDAP_CLAIMS_HANDLER_MANAGED_SERVICE_FACTORY_PID =
            "Claims_Handler_Manager";

    private ConfiguratorFactory configuratorFactory;

    public ServiceCommons(ConfiguratorFactory configuratorFactory) {
        this.configuratorFactory = configuratorFactory;
    }

    public Map<String, Map<String, Object>> getLdapLoginManagedServices() {
        return configuratorFactory.getConfigReader()
                .getManagedServiceConfigs(LDAP_LOGIN_MANAGED_SERVICE_FACTORY_PID);
    }

    public Map<String, Map<String, Object>> getLdapClaimsHandlerManagedServices() {
        return configuratorFactory.getConfigReader()
                .getManagedServiceConfigs(LDAP_CLAIMS_HANDLER_MANAGED_SERVICE_FACTORY_PID);
    }
}
