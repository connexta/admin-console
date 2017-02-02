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

import static org.codice.ddf.admin.api.validation.LdapValidationUtils.AUTHENTICATION;
import static org.codice.ddf.admin.api.validation.LdapValidationUtils.LDAPS;
import static org.codice.ddf.admin.api.validation.ValidationUtils.FACTORY_PID_KEY;
import static org.codice.ddf.admin.api.validation.ValidationUtils.SERVICE_PID_KEY;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.codice.ddf.admin.api.config.ldap.LdapConfiguration;
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

    public static LdapConfiguration ldapLoginServiceToLdapConfiguration(Map<String, Object> props) {
        LdapConfiguration ldapConfiguration = new LdapConfiguration();

        ldapConfiguration.servicePid(mapStringValue(SERVICE_PID_KEY, props));
        ldapConfiguration.factoryPid(mapStringValue(FACTORY_PID_KEY, props));
        ldapConfiguration.bindUser(mapStringValue(LDAP_BIND_USER_DN, props));
        ldapConfiguration.bindUserPassword(mapStringValue(LDAP_BIND_USER_PASS, props));
        ldapConfiguration.bindUserMethod(mapStringValue(BIND_METHOD, props));
        //        ldapConfiguration.bindKdcAddress((String) props.get(KDC_ADDRESS));
        ldapConfiguration.bindRealm(mapStringValue(REALM, props));
        ldapConfiguration.userNameAttribute(mapStringValue(USER_NAME_ATTRIBUTE, props));
        ldapConfiguration.baseUserDn(mapStringValue(USER_BASE_DN, props));
        ldapConfiguration.baseGroupDn(mapStringValue(GROUP_BASE_DN, props));
        URI ldapUri = getUriFromProperty(mapStringValue(LDAP_URL, props));

        if(ldapUri != null) {
            ldapConfiguration.encryptionMethod(ldapUri.getScheme());
            ldapConfiguration.hostName(ldapUri.getHost());
            ldapConfiguration.port(ldapUri.getPort());
        }

        if ((Boolean) props.get(START_TLS)) {
            ldapConfiguration.encryptionMethod(START_TLS);
        }
        ldapConfiguration.ldapUseCase(AUTHENTICATION);
        return ldapConfiguration;
    }

    public static Map<String, Object> ldapConfigurationToLdapLoginService(
            LdapConfiguration config) {
        Map<String, Object> ldapStsConfig = new HashMap<>();

        if(config != null) {
            String ldapUrl = getLdapUrl(config);
            boolean startTls = isStartTls(config);

            ldapStsConfig.put(LDAP_URL, ldapUrl + config.hostName() + ":" + config.port());
            ldapStsConfig.put(START_TLS, Boolean.toString(startTls));
            ldapStsConfig.put(LDAP_BIND_USER_DN, config.bindUser());
            ldapStsConfig.put(LDAP_BIND_USER_PASS, config.bindUserPassword());
            ldapStsConfig.put(BIND_METHOD, config.bindUserMethod());
            //        ldapStsConfig.put(KDC_ADDRESS, config.bindKdcAddress());
            ldapStsConfig.put(REALM, config.bindRealm());

            ldapStsConfig.put(USER_NAME_ATTRIBUTE, config.userNameAttribute());
            ldapStsConfig.put(USER_BASE_DN, config.baseUserDn());
            ldapStsConfig.put(GROUP_BASE_DN, config.baseGroupDn());
        }
        return ldapStsConfig;
    }

    public static boolean isStartTls(LdapConfiguration config) {
        return config.encryptionMethod()
                .equalsIgnoreCase(START_TLS);
    }

    public static String getLdapUrl(LdapConfiguration config) {
        return config.encryptionMethod()
                .equalsIgnoreCase(LDAPS) ? "ldaps://" : "ldap://";
    }

    public static URI getUriFromProperty(String ldapUrl) {
        if(StringUtils.isNotEmpty(ldapUrl)) {
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
