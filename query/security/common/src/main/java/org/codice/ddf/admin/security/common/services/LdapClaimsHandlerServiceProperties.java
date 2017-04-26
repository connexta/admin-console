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
package org.codice.ddf.admin.security.common.services;

import java.util.Map;

import org.codice.ddf.admin.configurator.ConfiguratorFactory;

public class LdapClaimsHandlerServiceProperties {

    // --- Ldap Claims Handler Service Properties
    public static final String LDAP_CLAIMS_HANDLER_MANAGED_SERVICE_FACTORY_PID =
            "Claims_Handler_Manager";

    public static final String LDAP_CLAIMS_HANDLER_FEATURE = "security-sts-ldapclaimshandler";

    public static final String URL = "url";

    public static final String START_TLS = "startTls";

    public static final String LDAP_BIND_USER_DN = "ldapBindUserDn";

    public static final String PASSWORD = "password";

    public static final String BIND_METHOD = "bindMethod";

    public static final String LOGIN_USER_ATTRIBUTE = "loginUserAttribute";

    public static final String USER_BASE_DN = "userBaseDn";

    public static final String GROUP_BASE_DN = "groupBaseDn";

    public static final String OBJECT_CLASS = "objectClass";

    public static final String MEMBERSHIP_USER_ATTRIBUTE = "membershipUserAttribute";

    public static final String MEMBER_NAME_ATTRIBUTE = "memberNameAttribute";

    public static final String PROPERTY_FILE_LOCATION = "propertyFileLocation";
    // ---

    private ConfiguratorFactory configuratorFactory;

    public LdapClaimsHandlerServiceProperties(ConfiguratorFactory configuratorFactory) {
        this.configuratorFactory = configuratorFactory;
    }

    public Map<String, Map<String, Object>> getLdapClaimsHandlerManagedServices() {
        return configuratorFactory.getConfigReader()
                .getManagedServiceConfigs(LDAP_CLAIMS_HANDLER_MANAGED_SERVICE_FACTORY_PID);
    }
}
