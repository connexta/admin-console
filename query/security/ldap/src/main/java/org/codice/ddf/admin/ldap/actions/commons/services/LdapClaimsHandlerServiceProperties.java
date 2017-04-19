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
package org.codice.ddf.admin.ldap.actions.commons.services;

import static org.codice.ddf.admin.ldap.actions.commons.services.LdapLoginServiceProperties.getLdapUrl;
import static org.codice.ddf.admin.ldap.actions.commons.services.LdapLoginServiceProperties.isStartTls;
import static org.codice.ddf.admin.ldap.fields.config.LdapUseCase.ATTRIBUTE_STORE;

import java.net.URI;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.codice.ddf.admin.configurator.ConfiguratorFactory;
import org.codice.ddf.admin.ldap.fields.config.LdapConfigurationField;
import org.codice.ddf.admin.ldap.fields.config.LdapSettingsField;
import org.codice.ddf.admin.ldap.fields.connection.LdapBindUserInfo;
import org.codice.ddf.admin.ldap.fields.connection.LdapConnectionField;

public class LdapClaimsHandlerServiceProperties {

    // TODO: tbatie - 4/3/17 - Find a common place for these keys
    public static final String SERVICE_PID_KEY = "service.pid";

    public static final String FACTORY_PID_KEY = "service.factoryPid";

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

    public static LdapConfigurationField ldapClaimsHandlerServiceToLdapConfig(
            Map<String, Object> props, ConfiguratorFactory configuratorFactory) {

        LdapConnectionField connection = new LdapConnectionField();

        URI ldapUri = LdapLoginServiceProperties.getUriFromProperty((String) props.get(URL));
        if (ldapUri != null) {
            connection.encryptionMethod(ldapUri.getScheme())
                    .hostname(ldapUri.getHost())
                    .port(ldapUri.getPort());
        }

        if ((Boolean) props.get(START_TLS)) {
            connection.encryptionMethod(START_TLS);
        }

        LdapBindUserInfo bindUserInfo =
                new LdapBindUserInfo().username(LdapLoginServiceProperties.mapStringValue(LDAP_BIND_USER_DN, props))
                        .password(LdapLoginServiceProperties.mapStringValue(PASSWORD, props))
                        .bindMethod(LdapLoginServiceProperties.mapStringValue(BIND_METHOD, props));

        LdapSettingsField settings = new LdapSettingsField().usernameAttribute(
                LdapLoginServiceProperties.mapStringValue(LOGIN_USER_ATTRIBUTE, props))
                .baseUserDn(LdapLoginServiceProperties.mapStringValue(USER_BASE_DN, props))
                .baseGroupDn(LdapLoginServiceProperties.mapStringValue(GROUP_BASE_DN, props))
                .groupObjectClass(LdapLoginServiceProperties.mapStringValue(OBJECT_CLASS, props))
                .groupAttributeHoldingMember(LdapLoginServiceProperties.mapStringValue(MEMBERSHIP_USER_ATTRIBUTE, props))
                .memberAttributeReferencedInGroup(LdapLoginServiceProperties.mapStringValue(MEMBER_NAME_ATTRIBUTE, props))
                .useCase(ATTRIBUTE_STORE);

        String attributeMappingsPath = LdapLoginServiceProperties.mapStringValue(PROPERTY_FILE_LOCATION, props);
        if (StringUtils.isNotEmpty(attributeMappingsPath)) {
            Map<String, String> attributeMappings =
                    new HashMap<>(configuratorFactory.getConfigReader().getProperties(Paths.get(attributeMappingsPath)));
            settings.attributeMapField(attributeMappings);
        }

        return new LdapConfigurationField().connection(connection)
                .bindUserInfo(bindUserInfo)
                .settings(settings)
                .pid(props.get(SERVICE_PID_KEY) == null ? null : (String) props.get(SERVICE_PID_KEY));
    }

    public static Map<String, Object> ldapConfigToLdapClaimsHandlerService(LdapConfigurationField config) {
        Map<String, Object> props = new HashMap<>();


        if (config != null) {
            String ldapUrl = getLdapUrl(config.connectionField());
            boolean startTls = isStartTls(config.connectionField());
            props.put(URL, ldapUrl + config.connectionField().hostname() + ":" + config.connectionField().port());
            props.put(START_TLS, startTls);
            props.put(LDAP_BIND_USER_DN, config.bindUserInfoField().credentials().username());
            props.put(PASSWORD, config.bindUserInfoField().credentials().password());
            props.put(BIND_METHOD, config.bindUserInfoField().bindMethod());
            props.put(LOGIN_USER_ATTRIBUTE, config.settingsField().usernameAttribute());
            props.put(USER_BASE_DN, config.settingsField().baseUserDn());
            props.put(GROUP_BASE_DN, config.settingsField().baseGroupDn());
            props.put(OBJECT_CLASS, config.settingsField().groupObjectClass());
            props.put(MEMBERSHIP_USER_ATTRIBUTE, config.settingsField().memberAttributeReferencedInGroup());
            props.put(MEMBER_NAME_ATTRIBUTE, config.settingsField().groupAttributeHoldingMember());
            // TODO: tbatie - 4/11/17 - Look up the pid, if it doesn't exist then create a new attribute mapping, else use the existing one
//            props.put(PROPERTY_FILE_LOCATION, config.settings().attributeMappingPath());
        }
        return props;
    }
}
