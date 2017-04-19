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

import static org.codice.ddf.admin.ldap.actions.commons.services.LdapClaimsHandlerServiceProperties.SERVICE_PID_KEY;
import static org.codice.ddf.admin.ldap.fields.config.LdapUseCase.LOGIN;
import static org.codice.ddf.admin.ldap.fields.connection.LdapEncryptionMethodField.LDAPS;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.codice.ddf.admin.ldap.fields.config.LdapConfigurationField;
import org.codice.ddf.admin.ldap.fields.config.LdapSettingsField;
import org.codice.ddf.admin.ldap.fields.connection.LdapBindUserInfo;
import org.codice.ddf.admin.ldap.fields.connection.LdapConnectionField;
import org.codice.ddf.configuration.PropertyResolver;

public class LdapLoginServiceProperties {
    public static final Pattern URI_MATCHER = Pattern.compile("\\w*://.*");

    // --- Ldap Login Service Properties
    public static final String LDAP_LOGIN_MANAGED_SERVICE_FACTORY_PID = "Ldap_Login_Config";

    public static final String LDAP_LOGIN_FEATURE = "security-sts-ldaplogin";

    public static final String LDAP_BIND_USER_DN = "ldapBindUserDn";

    public static final String LDAP_BIND_USER_PASS = "ldapBindUserPass";

    public static final String BIND_METHOD = "bindMethod";

    //    public static final String KDC_ADDRESS = "kdcAddress";
    public static final String REALM = "realm";

    public static final String USER_NAME_ATTRIBUTE = "userNameAttribute";

    public static final String USER_BASE_DN = "userBaseDn";

    public static final String GROUP_BASE_DN = "groupBaseDn";

    public static final String LDAP_URL = "ldapUrl";

    public static final String START_TLS = "startTls";
    // ---

    public static LdapConfigurationField ldapLoginServiceToLdapConfiguration(Map<String, Object> props) {
        LdapConnectionField connection = new LdapConnectionField();
        URI ldapUri = getUriFromProperty(mapStringValue(LDAP_URL, props));
        if (ldapUri != null) {
            connection.encryptionMethod(ldapUri.getScheme())
                    .hostname(ldapUri.getHost())
                    .port(ldapUri.getPort());
        }

        if ((Boolean) props.get(START_TLS)) {
            connection.encryptionMethod(START_TLS);
        }

        LdapBindUserInfo bindUserInfo = new LdapBindUserInfo()
                .username(mapStringValue(LDAP_BIND_USER_DN, props))
                .password(mapStringValue(LDAP_BIND_USER_PASS, props))
                .bindMethod(mapStringValue(BIND_METHOD, props))
                .realm(mapStringValue(REALM, props));
        //        ldapConfiguration.bindKdcAddress((String) props.get(KDC_ADDRESS));

        LdapSettingsField settings = new LdapSettingsField()
                .usernameAttribute(mapStringValue(USER_NAME_ATTRIBUTE, props))
                .baseUserDn(mapStringValue(USER_BASE_DN, props))
                .baseGroupDn(mapStringValue(GROUP_BASE_DN, props))
                .useCase(LOGIN);

        // TODO: tbatie - 4/3/17 - Don't think we need the factory pid
        //        ldapConfiguration.factoryPid(mapStringValue(FACTORY_PID_KEY, props));
        return new LdapConfigurationField()
                .connection(connection)
                .bindUserInfo(bindUserInfo)
                .settings(settings)
                .pid(mapStringValue(SERVICE_PID_KEY, props));
    }

    public static Map<String, Object> ldapConfigurationToLdapLoginService(LdapConfigurationField config) {
        Map<String, Object> ldapStsConfig = new HashMap<>();

        if (config != null) {
            String ldapUrl = getLdapUrl(config.connectionField());
            boolean startTls = isStartTls(config.connectionField());

            ldapStsConfig.put(LDAP_URL, ldapUrl + config.connectionField().hostname() + ":" + config.connectionField().port());
            ldapStsConfig.put(START_TLS, Boolean.toString(startTls));
            ldapStsConfig.put(LDAP_BIND_USER_DN, config.bindUserInfoField().credentials().username());
            ldapStsConfig.put(LDAP_BIND_USER_PASS, config.bindUserInfoField().credentials().password());
            ldapStsConfig.put(BIND_METHOD, config.bindUserInfoField().bindMethod());
            //        ldapStsConfig.put(KDC_ADDRESS, config.bindKdcAddress());
            ldapStsConfig.put(REALM, config.bindUserInfoField().realm());

            ldapStsConfig.put(USER_NAME_ATTRIBUTE, config.settingsField().usernameAttribute());
            ldapStsConfig.put(USER_BASE_DN, config.settingsField().baseUserDn());
            ldapStsConfig.put(GROUP_BASE_DN, config.settingsField().baseGroupDn());
        }
        return ldapStsConfig;
    }

    public static boolean isStartTls(LdapConnectionField config) {
        return config.encryptionMethod()
                .equalsIgnoreCase(START_TLS);
    }

    public static String getLdapUrl(LdapConnectionField connection) {
        return connection.encryptionMethod()
                .equalsIgnoreCase(LDAPS) ? "ldaps://" : "ldap://";
    }

    public static URI getUriFromProperty(String ldapUrl) {
        if (StringUtils.isNotEmpty(ldapUrl)) {
            ldapUrl = PropertyResolver.resolveProperties(ldapUrl);
            if (!URI_MATCHER.matcher(ldapUrl)
                    .matches()) {
                ldapUrl = "ldap://" + ldapUrl;
            }
            return URI.create(ldapUrl);
        }

        return null;
    }

    public static String mapStringValue(String key, Map<String, Object> properties) {
        return properties.get(key) == null ? null : (String) properties.get(key);
    }
}
