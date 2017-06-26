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
package org.codice.ddf.admin.ldap.commons;

import static org.codice.ddf.admin.common.services.ServiceCommons.FLAG_PASSWORD;
import static org.codice.ddf.admin.common.services.ServiceCommons.SERVICE_PID_KEY;
import static org.codice.ddf.admin.common.services.ServiceCommons.mapValue;
import static org.codice.ddf.admin.ldap.fields.connection.LdapEncryptionMethodField.LDAPS;
import static org.codice.ddf.admin.security.common.fields.ldap.LdapUseCase.ATTRIBUTE_STORE;
import static org.codice.ddf.admin.security.common.fields.ldap.LdapUseCase.AUTHENTICATION;
import static org.codice.ddf.admin.security.common.services.LdapClaimsHandlerServiceProperties.PROPERTY_FILE_LOCATION;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;
import org.codice.ddf.admin.api.fields.ListField;
import org.codice.ddf.admin.api.report.Report;
import org.codice.ddf.admin.common.fields.base.ListFieldImpl;
import org.codice.ddf.admin.common.report.ReportImpl;
import org.codice.ddf.admin.common.report.message.DefaultMessages;
import org.codice.ddf.admin.common.services.ServiceCommons;
import org.codice.ddf.admin.ldap.fields.config.LdapConfigurationField;
import org.codice.ddf.admin.ldap.fields.config.LdapDirectorySettingsField;
import org.codice.ddf.admin.ldap.fields.connection.LdapBindUserInfo;
import org.codice.ddf.admin.ldap.fields.connection.LdapConnectionField;
import org.codice.ddf.admin.security.common.services.LdapClaimsHandlerServiceProperties;
import org.codice.ddf.admin.security.common.services.LdapLoginServiceProperties;
import org.codice.ddf.configuration.PropertyResolver;
import org.codice.ddf.internal.admin.configurator.actions.ManagedServiceActions;
import org.codice.ddf.internal.admin.configurator.actions.PropertyActions;

public class LdapServiceCommons {
    private static final Pattern URI_MATCHER = Pattern.compile("\\w*://.*");

    private final PropertyActions propertyActions;

    private final ManagedServiceActions managedServiceActions;

    public LdapServiceCommons(PropertyActions propertyActions,
            ManagedServiceActions managedServiceActions) {
        this.propertyActions = propertyActions;
        this.managedServiceActions = managedServiceActions;
    }

    public ListField<LdapConfigurationField> getLdapConfigurations() {
        List<LdapConfigurationField> ldapLoginConfigs = new LdapLoginServiceProperties(
                managedServiceActions).getLdapLoginManagedServices()
                .values()
                .stream()
                .map(this::ldapLoginServiceToLdapConfiguration)
                .collect(Collectors.toList());

        List<LdapConfigurationField> ldapClaimsHandlerConfigs =
                new LdapClaimsHandlerServiceProperties(managedServiceActions).getLdapClaimsHandlerManagedServices()
                        .values()
                        .stream()
                        .map(this::ldapClaimsHandlerServiceToLdapConfig)
                        .collect(Collectors.toList());

        List<LdapConfigurationField> configs = Stream.concat(ldapLoginConfigs.stream(),
                ldapClaimsHandlerConfigs.stream())
                .collect(Collectors.toList());

        configs.stream()
                .forEach(config -> config.bindUserInfoField()
                        .password(FLAG_PASSWORD));

        return new ListFieldImpl<>(LdapConfigurationField.class).addAll(configs);
    }

    public Map<String, Object> ldapConfigToLdapClaimsHandlerService(LdapConfigurationField config,
            String attributeMappingPath) {
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
                            .credentialsField()
                            .username());
            props.put(LdapClaimsHandlerServiceProperties.PASSWORD,
                    config.bindUserInfoField()
                            .credentialsField()
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
            props.put(LdapClaimsHandlerServiceProperties.PROPERTY_FILE_LOCATION,
                    attributeMappingPath);
        }
        return props;
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
                            .credentialsField()
                            .username());
            ldapStsConfig.put(LdapLoginServiceProperties.LDAP_BIND_USER_PASS,
                    config.bindUserInfoField()
                            .credentialsField()
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

    /**
     * Checks for existing LDAP configurations with the same hostname and port of the {@code configuration}.
     * If there is an existing configuration, errors will be returned.
     * <p>
     * Possible error types: IDENTICAL_SERVICE_EXISTS
     *
     * @param newConfig configuration to check for existing configurations for
     * @return {@link Report} with errors indicating there are existing configurations
     * the {@code configuration}
     */
    public Report validateSimilarLdapServiceExists(LdapConfigurationField newConfig) {
        ReportImpl report = new ReportImpl();
        List<LdapConfigurationField> existingConfigs = getLdapConfigurations().getList();

        boolean identicalServiceExists = existingConfigs.stream()
                .anyMatch(existingConfig -> identicalSettingsExist(existingConfig, newConfig));

        if (identicalServiceExists) {
            report.addArgumentMessage(DefaultMessages.similarServiceExists(newConfig.path()));
        }

        return report;
    }

    private LdapConfigurationField ldapClaimsHandlerServiceToLdapConfig(Map<String, Object> props) {
        LdapConnectionField connection = getLdapConnectionField(props,
                LdapClaimsHandlerServiceProperties.URL,
                LdapClaimsHandlerServiceProperties.START_TLS);

        LdapBindUserInfo bindUserInfo = new LdapBindUserInfo().username(mapValue(props,
                LdapClaimsHandlerServiceProperties.LDAP_BIND_USER_DN))
                .password(FLAG_PASSWORD)
                .bindMethod(mapValue(props, LdapClaimsHandlerServiceProperties.BIND_METHOD));

        LdapDirectorySettingsField settings = new LdapDirectorySettingsField().usernameAttribute(
                mapValue(props, LdapClaimsHandlerServiceProperties.LOGIN_USER_ATTRIBUTE))
                .baseUserDn(mapValue(props, LdapClaimsHandlerServiceProperties.USER_BASE_DN))
                .baseGroupDn(mapValue(props, LdapClaimsHandlerServiceProperties.GROUP_BASE_DN))
                .groupObjectClass(mapValue(props, LdapClaimsHandlerServiceProperties.OBJECT_CLASS))
                .groupAttributeHoldingMember(mapValue(props,
                        LdapClaimsHandlerServiceProperties.MEMBERSHIP_USER_ATTRIBUTE))
                .memberAttributeReferencedInGroup(mapValue(props,
                        LdapClaimsHandlerServiceProperties.MEMBER_NAME_ATTRIBUTE))
                .useCase(ATTRIBUTE_STORE);

        Map<String, String> claimMappings = null;
        String attributeMappingsPath = mapValue(props, PROPERTY_FILE_LOCATION);
        if (StringUtils.isNotEmpty(attributeMappingsPath)) {
            Path path = Paths.get(attributeMappingsPath)
                    .toAbsolutePath();
            if (Files.exists(path)) {
                claimMappings = new HashMap<>(propertyActions.getProperties(path));
            }
        }

        return new LdapConfigurationField().connection(connection)
                .bindUserInfo(bindUserInfo)
                .settings(settings)
                .mapAllClaims(claimMappings)
                .pid(props.get(ServiceCommons.SERVICE_PID_KEY) == null ?
                        null :
                        (String) props.get(ServiceCommons.SERVICE_PID_KEY));
    }

    private LdapConfigurationField ldapLoginServiceToLdapConfiguration(Map<String, Object> props) {
        LdapConnectionField connection = getLdapConnectionField(props,
                LdapLoginServiceProperties.LDAP_URL,
                LdapLoginServiceProperties.START_TLS);

        LdapBindUserInfo bindUserInfo = new LdapBindUserInfo().username(mapValue(props,
                LdapLoginServiceProperties.LDAP_BIND_USER_DN))
                .password(FLAG_PASSWORD)
                .bindMethod(mapValue(props, LdapLoginServiceProperties.BIND_METHOD))
                .realm(mapValue(props, LdapLoginServiceProperties.REALM));
        //        ldapConfiguration.bindKdcAddress((String) props.get(KDC_ADDRESS));

        LdapDirectorySettingsField settings = new LdapDirectorySettingsField().usernameAttribute(
                mapValue(props, LdapLoginServiceProperties.USER_NAME_ATTRIBUTE))
                .baseUserDn(mapValue(props, LdapLoginServiceProperties.USER_BASE_DN))
                .baseGroupDn(mapValue(props, LdapLoginServiceProperties.GROUP_BASE_DN))
                .useCase(AUTHENTICATION);

        return new LdapConfigurationField().connection(connection)
                .bindUserInfo(bindUserInfo)
                .settings(settings)
                .pid(mapValue(props, SERVICE_PID_KEY));
    }

    private LdapConnectionField getLdapConnectionField(Map<String, Object> props, String ldapUrl,
            String startTls) {
        LdapConnectionField connection = new LdapConnectionField();
        URI ldapUri = getUriFromProperty(mapValue(props, ldapUrl));
        if (ldapUri != null) {
            connection.encryptionMethod(ldapUri.getScheme())
                    .hostname(ldapUri.getHost())
                    .port(ldapUri.getPort());
        }

        if ((Boolean) props.get(startTls)) {
            connection.encryptionMethod(startTls);
        }
        return connection;
    }

    private boolean identicalSettingsExist(LdapConfigurationField existingConfiguration,
            LdapConfigurationField newConfiguration) {
        return existingConfiguration.connectionField()
                .hostname()
                .equals(newConfiguration.connectionField()
                        .hostname()) && existingConfiguration.connectionField()
                .port() == existingConfiguration.connectionField()
                .port() && existingConfiguration.settingsField()
                .useCase()
                .equals(newConfiguration.settingsField()
                        .useCase());
    }

    private static boolean isStartTls(LdapConnectionField config) {
        return config.encryptionMethod()
                .equalsIgnoreCase(LdapLoginServiceProperties.START_TLS);
    }

    private static String getLdapUrl(LdapConnectionField connection) {
        return connection.encryptionMethod()
                .equalsIgnoreCase(LDAPS) ? "ldaps://" : "ldap://";
    }

    private static URI getUriFromProperty(String ldapUrl) {
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
}
