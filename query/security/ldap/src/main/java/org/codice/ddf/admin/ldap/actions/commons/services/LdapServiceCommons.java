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
package org.codice.ddf.admin.ldap.actions.commons.services;

import static org.codice.ddf.admin.common.services.ServiceCommons.SERVICE_PID_KEY;
import static org.codice.ddf.admin.ldap.fields.config.LdapUseCase.ATTRIBUTE_STORE;
import static org.codice.ddf.admin.ldap.fields.config.LdapUseCase.LOGIN;
import static org.codice.ddf.admin.ldap.fields.connection.LdapEncryptionMethodField.LDAPS;

import java.net.URI;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;
import org.codice.ddf.admin.api.fields.ListField;
import org.codice.ddf.admin.common.fields.base.ListFieldImpl;
import org.codice.ddf.admin.common.services.ServiceCommons;
import org.codice.ddf.admin.ldap.fields.config.LdapConfigurationField;
import org.codice.ddf.admin.ldap.fields.config.LdapSettingsField;
import org.codice.ddf.admin.ldap.fields.connection.LdapBindUserInfo;
import org.codice.ddf.admin.ldap.fields.connection.LdapConnectionField;
import org.codice.ddf.admin.security.common.services.LdapClaimsHandlerServiceProperties;
import org.codice.ddf.admin.security.common.services.LdapLoginServiceProperties;
import org.codice.ddf.configuration.PropertyResolver;
import org.codice.ddf.internal.admin.configurator.opfactory.ManagedServiceOpFactory;
import org.codice.ddf.internal.admin.configurator.opfactory.PropertyOpFactory;

public class LdapServiceCommons {

    public static final Pattern URI_MATCHER = Pattern.compile("\\w*://.*");

    private ManagedServiceOpFactory managedServiceOpFactory;

    private PropertyOpFactory propertyOpFactory;

    public LdapServiceCommons(ManagedServiceOpFactory managedServiceOpFactory,
            PropertyOpFactory propertyOpFactory) {
        this.managedServiceOpFactory = managedServiceOpFactory;
        this.propertyOpFactory = propertyOpFactory;
    }

    public ListField<LdapConfigurationField> getLdapConfigurations() {
        List<LdapConfigurationField> ldapLoginConfigs = new LdapClaimsHandlerServiceProperties(
                managedServiceOpFactory).getLdapClaimsHandlerManagedServices()
                .values()
                .stream()
                .map(this::ldapLoginServiceToLdapConfiguration)
                .collect(Collectors.toList());

        List<LdapConfigurationField> ldapClaimsHandlerConfigs =
                new LdapClaimsHandlerServiceProperties(managedServiceOpFactory).getLdapClaimsHandlerManagedServices()
                        .values()
                        .stream()
                        .map(this::ldapClaimsHandlerServiceToLdapConfig)
                        .collect(Collectors.toList());

        List<LdapConfigurationField> configs = Stream.concat(ldapLoginConfigs.stream(),
                ldapClaimsHandlerConfigs.stream())
                .collect(Collectors.toList());

        configs.stream()
                .forEach(config -> config.bindUserInfoField()
                        .password("*******"));

        return new ListFieldImpl<>(LdapConfigurationField.class).addAll(configs);
    }

    public LdapConfigurationField ldapClaimsHandlerServiceToLdapConfig(Map<String, Object> props) {

        LdapConnectionField connection = new LdapConnectionField();

        URI ldapUri =
                getUriFromProperty((String) props.get(LdapClaimsHandlerServiceProperties.URL));
        if (ldapUri != null) {
            connection.encryptionMethod(ldapUri.getScheme())
                    .hostname(ldapUri.getHost())
                    .port(ldapUri.getPort());
        }

        if ((Boolean) props.get(LdapClaimsHandlerServiceProperties.START_TLS)) {
            connection.encryptionMethod(LdapClaimsHandlerServiceProperties.START_TLS);
        }

        LdapBindUserInfo bindUserInfo = new LdapBindUserInfo().username(mapStringValue(
                LdapClaimsHandlerServiceProperties.LDAP_BIND_USER_DN,
                props))
                .password(mapStringValue(LdapClaimsHandlerServiceProperties.PASSWORD, props))
                .bindMethod(mapStringValue(LdapClaimsHandlerServiceProperties.BIND_METHOD, props));

        LdapSettingsField settings = new LdapSettingsField().usernameAttribute(mapStringValue(
                LdapClaimsHandlerServiceProperties.LOGIN_USER_ATTRIBUTE,
                props))
                .baseUserDn(mapStringValue(LdapClaimsHandlerServiceProperties.USER_BASE_DN, props))
                .baseGroupDn(mapStringValue(LdapClaimsHandlerServiceProperties.GROUP_BASE_DN,
                        props))
                .groupObjectClass(mapStringValue(LdapClaimsHandlerServiceProperties.OBJECT_CLASS,
                        props))
                .groupAttributeHoldingMember(mapStringValue(LdapClaimsHandlerServiceProperties.MEMBERSHIP_USER_ATTRIBUTE,
                        props))
                .memberAttributeReferencedInGroup(mapStringValue(LdapClaimsHandlerServiceProperties.MEMBER_NAME_ATTRIBUTE,
                        props))
                .useCase(ATTRIBUTE_STORE);

        String attributeMappingsPath =
                mapStringValue(LdapClaimsHandlerServiceProperties.PROPERTY_FILE_LOCATION, props);
        if (StringUtils.isNotEmpty(attributeMappingsPath)) {
            Map<String, String> attributeMappings = new HashMap<>(propertyOpFactory.getProperties(
                    Paths.get(attributeMappingsPath)));
            settings.attributeMapField(attributeMappings);
        }

        return new LdapConfigurationField().connection(connection)
                .bindUserInfo(bindUserInfo)
                .settings(settings)
                .pid(props.get(ServiceCommons.SERVICE_PID_KEY) == null ?
                        null :
                        (String) props.get(ServiceCommons.SERVICE_PID_KEY));
    }

    public static Map<String, Object> ldapConfigToLdapClaimsHandlerService(
            LdapConfigurationField config) {
        Map<String, Object> props = new HashMap<>();

        if (config != null) {
            String ldapUrl = getLdapUrl(config.connectionField());
            boolean startTls = isStartTls(config.connectionField());
            props.put(LdapClaimsHandlerServiceProperties.URL,
                    ldapUrl + config.connectionField()
                            .hostname() + ":" + config.connectionField()
                            .port());
            props.put(LdapClaimsHandlerServiceProperties.START_TLS, startTls);
            props.put(LdapClaimsHandlerServiceProperties.LDAP_BIND_USER_DN,
                    config.bindUserInfoField()
                            .credentials()
                            .username());
            props.put(LdapClaimsHandlerServiceProperties.PASSWORD,
                    config.bindUserInfoField()
                            .credentials()
                            .password());
            props.put(LdapClaimsHandlerServiceProperties.BIND_METHOD,
                    config.bindUserInfoField()
                            .bindMethod());
            props.put(LdapClaimsHandlerServiceProperties.LOGIN_USER_ATTRIBUTE,
                    config.settingsField()
                            .usernameAttribute());
            props.put(LdapClaimsHandlerServiceProperties.USER_BASE_DN,
                    config.settingsField()
                            .baseUserDn());
            props.put(LdapClaimsHandlerServiceProperties.GROUP_BASE_DN,
                    config.settingsField()
                            .baseGroupDn());
            props.put(LdapClaimsHandlerServiceProperties.OBJECT_CLASS,
                    config.settingsField()
                            .groupObjectClass());
            props.put(LdapClaimsHandlerServiceProperties.MEMBERSHIP_USER_ATTRIBUTE,
                    config.settingsField()
                            .memberAttributeReferencedInGroup());
            props.put(LdapClaimsHandlerServiceProperties.MEMBER_NAME_ATTRIBUTE,
                    config.settingsField()
                            .groupAttributeHoldingMember());
            // TODO: tbatie - 4/11/17 - Look up the pid, if it doesn't exist then create a new attribute mapping, else use the existing one
            //            props.put(PROPERTY_FILE_LOCATION, config.settings().attributeMappingPath());
        }
        return props;
    }

    public LdapConfigurationField ldapLoginServiceToLdapConfiguration(Map<String, Object> props) {
        LdapConnectionField connection = new LdapConnectionField();
        URI ldapUri = getUriFromProperty(mapStringValue(LdapLoginServiceProperties.LDAP_URL,
                props));
        if (ldapUri != null) {
            connection.encryptionMethod(ldapUri.getScheme())
                    .hostname(ldapUri.getHost())
                    .port(ldapUri.getPort());
        }

        if ((Boolean) props.get(LdapLoginServiceProperties.START_TLS)) {
            connection.encryptionMethod(LdapLoginServiceProperties.START_TLS);
        }

        LdapBindUserInfo bindUserInfo = new LdapBindUserInfo().username(mapStringValue(
                LdapLoginServiceProperties.LDAP_BIND_USER_DN,
                props))
                .password(mapStringValue(LdapLoginServiceProperties.LDAP_BIND_USER_PASS, props))
                .bindMethod(mapStringValue(LdapLoginServiceProperties.BIND_METHOD, props))
                .realm(mapStringValue(LdapLoginServiceProperties.REALM, props));
        //        ldapConfiguration.bindKdcAddress((String) props.get(KDC_ADDRESS));

        LdapSettingsField settings = new LdapSettingsField().usernameAttribute(mapStringValue(
                LdapLoginServiceProperties.USER_NAME_ATTRIBUTE,
                props))
                .baseUserDn(mapStringValue(LdapLoginServiceProperties.USER_BASE_DN, props))
                .baseGroupDn(mapStringValue(LdapLoginServiceProperties.GROUP_BASE_DN, props))
                .useCase(LOGIN);

        return new LdapConfigurationField().connection(connection)
                .bindUserInfo(bindUserInfo)
                .settings(settings)
                .pid(mapStringValue(SERVICE_PID_KEY, props));
    }

    public Map<String, Object> ldapConfigurationToLdapLoginService(LdapConfigurationField config) {
        Map<String, Object> ldapStsConfig = new HashMap<>();

        if (config != null) {
            String ldapUrl = getLdapUrl(config.connectionField());
            boolean startTls = isStartTls(config.connectionField());

            ldapStsConfig.put(LdapLoginServiceProperties.LDAP_URL,
                    ldapUrl + config.connectionField()
                            .hostname() + ":" + config.connectionField()
                            .port());
            ldapStsConfig.put(LdapLoginServiceProperties.START_TLS, Boolean.toString(startTls));
            ldapStsConfig.put(LdapLoginServiceProperties.LDAP_BIND_USER_DN,
                    config.bindUserInfoField()
                            .credentials()
                            .username());
            ldapStsConfig.put(LdapLoginServiceProperties.LDAP_BIND_USER_PASS,
                    config.bindUserInfoField()
                            .credentials()
                            .password());
            ldapStsConfig.put(LdapLoginServiceProperties.BIND_METHOD,
                    config.bindUserInfoField()
                            .bindMethod());
            //        ldapStsConfig.put(KDC_ADDRESS, config.bindKdcAddress());
            ldapStsConfig.put(LdapLoginServiceProperties.REALM,
                    config.bindUserInfoField()
                            .realm());

            ldapStsConfig.put(LdapLoginServiceProperties.USER_NAME_ATTRIBUTE,
                    config.settingsField()
                            .usernameAttribute());
            ldapStsConfig.put(LdapLoginServiceProperties.USER_BASE_DN,
                    config.settingsField()
                            .baseUserDn());
            ldapStsConfig.put(LdapLoginServiceProperties.GROUP_BASE_DN,
                    config.settingsField()
                            .baseGroupDn());
        }
        return ldapStsConfig;
    }

    public static boolean isStartTls(LdapConnectionField config) {
        return config.encryptionMethod()
                .equalsIgnoreCase(LdapLoginServiceProperties.START_TLS);
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
