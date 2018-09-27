/**
 * Copyright (c) Codice Foundation
 *
 * <p>This is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version.
 *
 * <p>This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details. A copy of the GNU Lesser General Public
 * License is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 */
package org.codice.ddf.admin.ldap.commons;

import static org.codice.ddf.admin.common.services.ServiceCommons.FLAG_PASSWORD;
import static org.codice.ddf.admin.common.services.ServiceCommons.SERVICE_PID_KEY;
import static org.codice.ddf.admin.common.services.ServiceCommons.mapValue;
import static org.codice.ddf.admin.ldap.fields.connection.LdapEncryptionMethodField.LdapsEncryption.LDAPS;
import static org.codice.ddf.admin.security.common.fields.ldap.LdapUseCase.AttributeStore.ATTRIBUTE_STORE;
import static org.codice.ddf.admin.security.common.fields.ldap.LdapUseCase.AuthenticationEnumValue.AUTHENTICATION;
import static org.codice.ddf.admin.security.common.services.LdapClaimsHandlerServiceProperties.PROPERTY_FILE_LOCATION;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang.StringUtils;
import org.codice.ddf.admin.api.fields.ListField;
import org.codice.ddf.admin.common.services.ServiceCommons;
import org.codice.ddf.admin.ldap.fields.config.LdapConfigurationField;
import org.codice.ddf.admin.ldap.fields.config.LdapDirectorySettingsField;
import org.codice.ddf.admin.ldap.fields.connection.LdapBindMethod;
import org.codice.ddf.admin.ldap.fields.connection.LdapBindUserInfo;
import org.codice.ddf.admin.ldap.fields.connection.LdapConnectionField;
import org.codice.ddf.admin.ldap.fields.connection.LdapEncryptionMethodField;
import org.codice.ddf.admin.ldap.fields.connection.LdapLoadBalancingField;
import org.codice.ddf.admin.security.common.services.LdapClaimsHandlerServiceProperties;
import org.codice.ddf.admin.security.common.services.LdapLoginServiceProperties;
import org.codice.ddf.configuration.PropertyResolver;
import org.codice.ddf.internal.admin.configurator.actions.ConfiguratorSuite;

public class LdapServiceCommons {

  private static final Pattern URI_MATCHER = Pattern.compile("\\w*://.*");

  private final ConfiguratorSuite configuratorSuite;

  public LdapServiceCommons(ConfiguratorSuite configuratorSuite) {
    this.configuratorSuite = configuratorSuite;
  }

  public ListField<LdapConfigurationField> getLdapConfigurations() {
    List<LdapConfigurationField> ldapLoginConfigs =
        new LdapLoginServiceProperties(configuratorSuite)
            .getLdapLoginManagedServices()
            .values()
            .stream()
            .map(this::ldapLoginServiceToLdapConfiguration)
            .collect(Collectors.toList());

    List<LdapConfigurationField> ldapClaimsHandlerConfigs =
        new LdapClaimsHandlerServiceProperties(configuratorSuite)
            .getLdapClaimsHandlerManagedServices()
            .values()
            .stream()
            .map(this::ldapClaimsHandlerServiceToLdapConfig)
            .collect(Collectors.toList());

    List<LdapConfigurationField> configs =
        Stream.concat(ldapLoginConfigs.stream(), ldapClaimsHandlerConfigs.stream())
            .collect(Collectors.toList());

    configs.stream().forEach(config -> config.bindUserInfoField().password(FLAG_PASSWORD));

    return new LdapConfigurationField.ListImpl().addAll(configs);
  }

  public Map<String, Object> ldapConfigToLdapClaimsHandlerService(
      LdapConfigurationField config, String attributeMappingPath) {
    Map<String, Object> props = new HashMap<>();

    if (config != null) {
      boolean startTls = isStartTls(config.connectionsField());
      props.put(LdapClaimsHandlerServiceProperties.URL, config.connectionsField().getLdapUrls());
      props.put(
          LdapClaimsHandlerServiceProperties.LOAD_BALANCING,
          config.loadBalancingField().getValue());
      props.put(LdapClaimsHandlerServiceProperties.START_TLS, startTls);
      props.put(
          LdapClaimsHandlerServiceProperties.LDAP_BIND_USER_DN,
          config.bindUserInfoField().credentialsField().username());
      props.put(
          LdapClaimsHandlerServiceProperties.PASSWORD,
          config.bindUserInfoField().credentialsField().password());
      props.put(
          LdapClaimsHandlerServiceProperties.BIND_METHOD, config.bindUserInfoField().bindMethod());
      props.put(
          LdapClaimsHandlerServiceProperties.LOGIN_USER_ATTRIBUTE,
          config.settingsField().loginUserAttribute());
      props.put(
          LdapClaimsHandlerServiceProperties.USER_BASE_DN, config.settingsField().baseUserDn());
      props.put(
          LdapClaimsHandlerServiceProperties.GROUP_BASE_DN, config.settingsField().baseGroupDn());
      props.put(
          LdapClaimsHandlerServiceProperties.OBJECT_CLASS,
          config.settingsField().groupObjectClass());
      props.put(
          LdapClaimsHandlerServiceProperties.MEMBERSHIP_USER_ATTRIBUTE,
          config.settingsField().memberAttributeReferencedInGroup());
      props.put(
          LdapClaimsHandlerServiceProperties.MEMBER_NAME_ATTRIBUTE,
          config.settingsField().groupAttributeHoldingMember());
      props.put(LdapClaimsHandlerServiceProperties.PROPERTY_FILE_LOCATION, attributeMappingPath);
    }
    return props;
  }

  public Map<String, Object> ldapConfigurationToLdapLoginService(LdapConfigurationField config) {
    Map<String, Object> ldapStsConfig = new HashMap<>();

    if (config != null) {
      boolean startTls = isStartTls(config.connectionsField());

      ldapStsConfig.put(
          LdapLoginServiceProperties.LDAP_URL, config.connectionsField().getLdapUrls());
      ldapStsConfig.put(
          LdapLoginServiceProperties.LDAP_LOAD_BALANCING, config.loadBalancingField().getValue());
      ldapStsConfig.put(LdapLoginServiceProperties.START_TLS, Boolean.toString(startTls));
      ldapStsConfig.put(
          LdapLoginServiceProperties.LDAP_BIND_USER_DN,
          config.bindUserInfoField().credentialsField().username());
      ldapStsConfig.put(
          LdapLoginServiceProperties.LDAP_BIND_USER_PASS,
          config.bindUserInfoField().credentialsField().password());
      ldapStsConfig.put(
          LdapLoginServiceProperties.BIND_METHOD, config.bindUserInfoField().bindMethod());
      //        ldapStsConfig.put(KDC_ADDRESS, config.bindKdcAddress())
      ldapStsConfig.put(LdapLoginServiceProperties.REALM, config.bindUserInfoField().realm());

      ldapStsConfig.put(
          LdapLoginServiceProperties.LOGIN_USER_ATTRIBUTE,
          config.settingsField().loginUserAttribute());

      ldapStsConfig.put(
          LdapLoginServiceProperties.MEMBERSHIP_USER_ATTRIBUTE,
          config.settingsField().memberAttributeReferencedInGroup());

      ldapStsConfig.put(
          LdapLoginServiceProperties.MEMBER_NAME_ATTRIBUTE,
          config.settingsField().groupAttributeHoldingMember());

      ldapStsConfig.put(
          LdapLoginServiceProperties.USER_BASE_DN, config.settingsField().baseUserDn());
      ldapStsConfig.put(
          LdapLoginServiceProperties.GROUP_BASE_DN, config.settingsField().baseGroupDn());
    }
    return ldapStsConfig;
  }

  private LdapConfigurationField ldapClaimsHandlerServiceToLdapConfig(Map<String, Object> props) {
    LdapConnectionField.ListImpl connections =
        getLdapConnectionsField(
            props,
            LdapClaimsHandlerServiceProperties.URL,
            LdapClaimsHandlerServiceProperties.START_TLS);

    LdapLoadBalancingField loadBalancing = new LdapLoadBalancingField();
    loadBalancing.setValue(mapValue(props, LdapClaimsHandlerServiceProperties.LOAD_BALANCING));

    LdapBindUserInfo bindUserInfo =
        new LdapBindUserInfo()
            .username(mapValue(props, LdapClaimsHandlerServiceProperties.LDAP_BIND_USER_DN))
            .password(FLAG_PASSWORD)
            .bindMethod(mapValue(props, LdapClaimsHandlerServiceProperties.BIND_METHOD));

    LdapDirectorySettingsField settings =
        new LdapDirectorySettingsField()
            .loginUserAttribute(
                mapValue(props, LdapClaimsHandlerServiceProperties.LOGIN_USER_ATTRIBUTE))
            .baseUserDn(mapValue(props, LdapClaimsHandlerServiceProperties.USER_BASE_DN))
            .baseGroupDn(mapValue(props, LdapClaimsHandlerServiceProperties.GROUP_BASE_DN))
            .groupObjectClass(mapValue(props, LdapClaimsHandlerServiceProperties.OBJECT_CLASS))
            .groupAttributeHoldingMember(
                mapValue(props, LdapClaimsHandlerServiceProperties.MEMBER_NAME_ATTRIBUTE))
            .memberAttributeReferencedInGroup(
                mapValue(props, LdapClaimsHandlerServiceProperties.MEMBERSHIP_USER_ATTRIBUTE))
            .useCase(ATTRIBUTE_STORE);

    Map<String, String> claimMappings = Collections.emptyMap();
    String attributeMappingsPath = mapValue(props, PROPERTY_FILE_LOCATION);
    if (StringUtils.isNotEmpty(attributeMappingsPath)) {
      Path path = Paths.get(attributeMappingsPath).toAbsolutePath();
      if (path.toFile().exists()) {
        claimMappings = new HashMap<>(configuratorSuite.getPropertyActions().getProperties(path));
      }
    }

    return new LdapConfigurationField()
        .connections(connections)
        .loadBalancing(loadBalancing)
        .bindUserInfo(bindUserInfo)
        .settings(settings)
        .mapAllClaims(claimMappings)
        .pid(
            props.get(ServiceCommons.SERVICE_PID_KEY) == null
                ? null
                : (String) props.get(ServiceCommons.SERVICE_PID_KEY));
  }

  private LdapConfigurationField ldapLoginServiceToLdapConfiguration(Map<String, Object> props) {
    LdapConnectionField.ListImpl connections =
        getLdapConnectionsField(
            props, LdapLoginServiceProperties.LDAP_URL, LdapLoginServiceProperties.START_TLS);

    LdapLoadBalancingField loadBalancing = new LdapLoadBalancingField();
    loadBalancing.setValue(mapValue(props, LdapLoginServiceProperties.LDAP_LOAD_BALANCING));

    LdapBindUserInfo bindUserInfo =
        new LdapBindUserInfo()
            .username(mapValue(props, LdapLoginServiceProperties.LDAP_BIND_USER_DN))
            .password(FLAG_PASSWORD)
            .bindMethod(mapValue(props, LdapLoginServiceProperties.BIND_METHOD));

    if (bindUserInfo.bindMethod() == LdapBindMethod.DigestMd5Sasl.DIGEST_MD5_SASL) {
      bindUserInfo.realm(mapValue(props, LdapLoginServiceProperties.REALM));
    }
    //        ldapConfiguration.bindKdcAddress((String) props.get(KDC_ADDRESS))

    LdapDirectorySettingsField settings =
        new LdapDirectorySettingsField()
            .loginUserAttribute(mapValue(props, LdapLoginServiceProperties.LOGIN_USER_ATTRIBUTE))
            .memberAttributeReferencedInGroup(
                mapValue(props, LdapLoginServiceProperties.MEMBERSHIP_USER_ATTRIBUTE))
            .groupAttributeHoldingMember(
                mapValue(props, LdapLoginServiceProperties.MEMBER_NAME_ATTRIBUTE))
            .baseUserDn(mapValue(props, LdapLoginServiceProperties.USER_BASE_DN))
            .baseGroupDn(mapValue(props, LdapLoginServiceProperties.GROUP_BASE_DN))
            .useCase(AUTHENTICATION);

    return new LdapConfigurationField()
        .connections(connections)
        .loadBalancing(loadBalancing)
        .bindUserInfo(bindUserInfo)
        .settings(settings)
        .pid(mapValue(props, SERVICE_PID_KEY));
  }

  private LdapConnectionField.ListImpl getLdapConnectionsField(
      Map<String, Object> props, String ldapUrlKey, String startTlsKey) {
    LdapConnectionField.ListImpl connection = new LdapConnectionField.ListImpl();

    Boolean isStartTls = (Boolean) props.get(startTlsKey);

    Object ldapUrls = mapValue(props, ldapUrlKey);
    if (ldapUrls instanceof String[]) {
      for (String url : ((String[]) ldapUrls)) {
        connection.add(getLdapConnectionField(url, isStartTls));
      }
    } else if (ldapUrls != null) {
      connection.add(getLdapConnectionField(ldapUrls.toString(), isStartTls));
    }

    return connection;
  }

  private LdapConnectionField getLdapConnectionField(String url, Boolean startTls) {
    LdapConnectionField connection = new LdapConnectionField();
    URI ldapUri = getUriFromProperty(url);

    if (ldapUri != null && ldapUri.getScheme() != null) {
      // TODO: tbatie - 8/17/17 - It'd be great if we had some sort of match method in the EnumValue
      // instead of doing little checks like this
      connection
          .encryptionMethod(
              ldapUri.getScheme().equals("ldap")
                  ? LdapEncryptionMethodField.NoEncryption.NONE
                  : ldapUri.getScheme())
          .hostname(ldapUri.getHost())
          .port(ldapUri.getPort());
    }

    if (startTls) {
      connection.encryptionMethod(LdapLoginServiceProperties.START_TLS);
    }
    return connection;
  }

  private static boolean isStartTls(ListField<LdapConnectionField> config) {
    boolean startTls = false;
    if (config != null && !config.getList().isEmpty()) {
      startTls =
          config
              .getList()
              .get(0)
              .encryptionMethod()
              .equalsIgnoreCase(LdapLoginServiceProperties.START_TLS);
    }
    return startTls;
  }

  private static String getLdapUrl(LdapConnectionField connection) {
    return connection.encryptionMethod().equalsIgnoreCase(LDAPS) ? "ldaps://" : "ldap://";
  }

  private static URI getUriFromProperty(String ldapUrl) {
    if (StringUtils.isEmpty(ldapUrl)) {
      return null;
    }

    String newUri = PropertyResolver.resolveProperties(ldapUrl);
    if (!URI_MATCHER.matcher(newUri).matches()) {
      newUri = "ldap://" + newUri;
    }
    return URI.create(newUri);
  }
}
