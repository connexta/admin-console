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
package org.codice.ddf.admin.ldap.persist;

import static org.codice.ddf.admin.common.report.message.DefaultMessages.failedPersistError;
import static org.codice.ddf.admin.security.common.fields.ldap.LdapUseCase.AttributeStore.ATTRIBUTE_STORE;
import static org.codice.ddf.admin.security.common.fields.ldap.LdapUseCase.AuthenticationAndAttributeStore.AUTHENTICATION_AND_ATTRIBUTE_STORE;
import static org.codice.ddf.admin.security.common.fields.ldap.LdapUseCase.AuthenticationEnumValue.AUTHENTICATION;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.common.fields.base.BaseFunctionField;
import org.codice.ddf.admin.common.fields.base.scalar.BooleanField;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;
import org.codice.ddf.admin.common.report.message.DefaultMessages;
import org.codice.ddf.admin.configurator.Configurator;
import org.codice.ddf.admin.configurator.OperationReport;
import org.codice.ddf.admin.ldap.commons.LdapServiceCommons;
import org.codice.ddf.admin.ldap.fields.config.LdapConfigurationField;
import org.codice.ddf.admin.security.common.SecurityValidation;
import org.codice.ddf.admin.security.common.fields.wcpm.ClaimsMapEntry;
import org.codice.ddf.admin.security.common.services.LdapClaimsHandlerServiceProperties;
import org.codice.ddf.admin.security.common.services.LdapLoginServiceProperties;
import org.codice.ddf.admin.security.common.services.StsServiceProperties;
import org.codice.ddf.internal.admin.configurator.actions.ConfiguratorSuite;

public class CreateLdapConfiguration extends BaseFunctionField<BooleanField> {

  public static final String FIELD_NAME = "createLdapConfig";

  public static final String DESCRIPTION = "Creates a LDAP configuration.";

  public static final BooleanField RETURN_TYPE = new BooleanField();

  private LdapConfigurationField config;

  private final ConfiguratorSuite configuratorSuite;

  private LdapServiceCommons ldapServiceCommons;

  private StsServiceProperties stsServiceProperties;

  public CreateLdapConfiguration(ConfiguratorSuite configuratorSuite) {
    super(FIELD_NAME, DESCRIPTION);
    this.configuratorSuite = configuratorSuite;

    config = new LdapConfigurationField().useDefaultRequired();
    stsServiceProperties = new StsServiceProperties();

    this.ldapServiceCommons = new LdapServiceCommons(configuratorSuite);
  }

  @Override
  public BooleanField getReturnType() {
    return RETURN_TYPE;
  }

  @Override
  public List<Field> getArguments() {
    return ImmutableList.of(config);
  }

  @Override
  public BooleanField performFunction() {
    Configurator configurator = configuratorSuite.getConfiguratorFactory().getConfigurator();

    String authType = config.settingsField().useCase();

    if (authType.equalsIgnoreCase(AUTHENTICATION)
        || authType.equalsIgnoreCase(AUTHENTICATION_AND_ATTRIBUTE_STORE)) {
      Map<String, Object> ldapLoginServiceProps =
          ldapServiceCommons.ldapConfigurationToLdapLoginService(config);
      configurator.add(
          configuratorSuite
              .getFeatureActions()
              .start(LdapLoginServiceProperties.LDAP_LOGIN_FEATURE));
      configurator.add(
          configuratorSuite
              .getManagedServiceActions()
              .create(
                  LdapLoginServiceProperties.LDAP_LOGIN_MANAGED_SERVICE_FACTORY_PID,
                  ldapLoginServiceProps));
    }

    if (authType.equalsIgnoreCase(ATTRIBUTE_STORE)
        || authType.equalsIgnoreCase(AUTHENTICATION_AND_ATTRIBUTE_STORE)) {
      Path newAttributeMappingPath =
          Paths.get(
              System.getProperty("ddf.home"),
              "etc",
              "ws-security",
              "ldapAttributeMap-" + UUID.randomUUID().toString() + ".props");
      Map<String, Object> ldapClaimsServiceProps =
          ldapServiceCommons.ldapConfigToLdapClaimsHandlerService(
              config, newAttributeMappingPath.toString());
      configurator.add(
          configuratorSuite
              .getPropertyActions()
              .create(newAttributeMappingPath, config.claimsMapping()));
      configurator.add(
          configuratorSuite
              .getFeatureActions()
              .start(LdapClaimsHandlerServiceProperties.LDAP_CLAIMS_HANDLER_FEATURE));
      configurator.add(
          configuratorSuite
              .getManagedServiceActions()
              .create(
                  LdapClaimsHandlerServiceProperties
                      .LDAP_CLAIMS_HANDLER_MANAGED_SERVICE_FACTORY_PID,
                  ldapClaimsServiceProps));
    }

    OperationReport report =
        AccessController.doPrivileged(
            (PrivilegedAction<OperationReport>)
                () -> configurator.commit("Creating LDAP configuration."));

    if (report.containsFailedResults()) {
      addErrorMessage(failedPersistError());
    }

    return new BooleanField(!containsErrorMsgs());
  }

  @Override
  public void validate() {
    String useCase = config.settingsField().useCase();
    if (useCase != null
        && (useCase.equals(ATTRIBUTE_STORE)
            || useCase.equals(AUTHENTICATION_AND_ATTRIBUTE_STORE))) {
      config.settingsField().useDefaultRequiredForAttributeStore();
      config.claimMappingsField().isRequired(true);
    }

    super.validate();
    if (containsErrorMsgs()) {
      return;
    }

    List<StringField> claimArgs =
        config
            .claimMappingsField()
            .getList()
            .stream()
            .map(ClaimsMapEntry::claimField)
            .collect(Collectors.toList());

    addErrorMessages(
        SecurityValidation.validateStsClaimsExist(
            claimArgs, configuratorSuite.getServiceActions(), stsServiceProperties));
  }

  @Override
  public FunctionField<BooleanField> newInstance() {
    return new CreateLdapConfiguration(configuratorSuite);
  }

  @Override
  public Set<String> getFunctionErrorCodes() {
    return ImmutableSet.of(DefaultMessages.FAILED_PERSIST);
  }
}
