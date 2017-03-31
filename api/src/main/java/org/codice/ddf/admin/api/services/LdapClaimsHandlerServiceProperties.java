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
package org.codice.ddf.admin.api.services;

import static org.codice.ddf.admin.api.services.LdapLoginServiceProperties.getLdapUrl;
import static org.codice.ddf.admin.api.services.LdapLoginServiceProperties.getUriFromProperty;
import static org.codice.ddf.admin.api.services.LdapLoginServiceProperties.isStartTls;
import static org.codice.ddf.admin.api.services.LdapLoginServiceProperties.mapStringValue;
import static org.codice.ddf.admin.api.validation.LdapValidationUtils.ATTRIBUTE_STORE;
import static org.codice.ddf.admin.api.validation.ValidationUtils.SERVICE_PID_KEY;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.codice.ddf.admin.api.config.ldap.LdapConfiguration;
import org.codice.ddf.admin.configurator.Configurator;

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

    public static LdapConfiguration ldapClaimsHandlerServiceToLdapConfig(Map<String, Object> props,
            Configurator configurator) {
        LdapConfiguration config = new LdapConfiguration();
        config.servicePid(
                props.get(SERVICE_PID_KEY) == null ? null : (String) props.get(SERVICE_PID_KEY));

        URI ldapUri = getUriFromProperty((String) props.get(URL));
        if (ldapUri != null) {
            config.encryptionMethod(ldapUri.getScheme());
            config.hostName(ldapUri.getHost());
            config.port(ldapUri.getPort());
        }

        if ((Boolean) props.get(START_TLS)) {
            config.encryptionMethod(START_TLS);
        }
        config.bindUser(mapStringValue(LDAP_BIND_USER_DN, props));
        config.bindUserPassword(mapStringValue(PASSWORD, props));
        config.bindUserMethod(mapStringValue(BIND_METHOD, props));
        config.userNameAttribute(mapStringValue(LOGIN_USER_ATTRIBUTE, props));
        config.baseUserDn(mapStringValue(USER_BASE_DN, props));
        config.baseGroupDn(mapStringValue(GROUP_BASE_DN, props));
        config.groupObjectClass(mapStringValue(OBJECT_CLASS, props));
        config.groupAttributeHoldingMember(mapStringValue(MEMBERSHIP_USER_ATTRIBUTE, props));
        config.memberAttributeReferencedInGroup(mapStringValue(MEMBER_NAME_ATTRIBUTE, props));

        String attributeMappingsPath = mapStringValue(PROPERTY_FILE_LOCATION, props);
        config.attributeMappingsPath(attributeMappingsPath);

        if (StringUtils.isNotEmpty(attributeMappingsPath)) {
            Path ddfHome = Paths.get(System.getProperty("ddf.home"));
            Path mappingPath = Paths.get(attributeMappingsPath);
            if (!mappingPath.startsWith(ddfHome)) {
                mappingPath = ddfHome.resolve(mappingPath);
            }
            Map<String, String> attributeMappings = new HashMap<>(configurator.getProperties(
                    mappingPath));
            config.attributeMappings(attributeMappings);
        }
        config.ldapUseCase(ATTRIBUTE_STORE);
        return config;
    }

    public static Map<String, Object> ldapConfigToLdapClaimsHandlerService(
            LdapConfiguration config) {
        Map<String, Object> props = new HashMap<>();

        if (config != null) {
            String ldapUrl = getLdapUrl(config);
            boolean startTls = isStartTls(config);
            props.put(URL, ldapUrl + config.hostName() + ":" + config.port());
            props.put(START_TLS, startTls);
            props.put(LDAP_BIND_USER_DN, config.bindUser());
            props.put(PASSWORD, config.bindUserPassword());
            props.put(BIND_METHOD, config.bindUserMethod());
            props.put(LOGIN_USER_ATTRIBUTE, config.userNameAttribute());
            props.put(USER_BASE_DN, config.baseUserDn());
            props.put(GROUP_BASE_DN, config.baseGroupDn());
            props.put(OBJECT_CLASS, config.groupObjectClass());
            props.put(MEMBERSHIP_USER_ATTRIBUTE, config.memberAttributeReferencedInGroup());
            props.put(MEMBER_NAME_ATTRIBUTE, config.groupAttributeHoldingMember());
            props.put(PROPERTY_FILE_LOCATION, config.attributeMappingsPath());
        }
        return props;
    }
}
