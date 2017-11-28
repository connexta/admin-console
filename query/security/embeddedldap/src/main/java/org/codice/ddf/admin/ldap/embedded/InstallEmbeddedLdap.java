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
package org.codice.ddf.admin.ldap.embedded;

import static org.codice.ddf.admin.common.report.message.DefaultMessages.failedPersistError;
import static org.codice.ddf.admin.security.common.fields.ldap.LdapUseCase.AttributeStore.ATTRIBUTE_STORE;
import static org.codice.ddf.admin.security.common.fields.ldap.LdapUseCase.AuthenticationAndAttributeStore.AUTHENTICATION_AND_ATTRIBUTE_STORE;
import static org.codice.ddf.admin.security.common.fields.ldap.LdapUseCase.AuthenticationEnumValue.AUTHENTICATION;
import static org.codice.ddf.admin.security.common.services.EmbeddedLdapServiceProperties.ALL_DEFAULT_EMBEDDED_LDAP_CONFIG_FEATURE;
import static org.codice.ddf.admin.security.common.services.EmbeddedLdapServiceProperties.DEFAULT_EMBEDDED_LDAP_CLAIMS_HANDLER_CONFIG_FEATURE;
import static org.codice.ddf.admin.security.common.services.EmbeddedLdapServiceProperties.DEFAULT_EMBEDDED_LDAP_LOGIN_CONFIG_FEATURE;
import static org.codice.ddf.admin.security.common.services.EmbeddedLdapServiceProperties.EMBEDDED_LDAP_FEATURE;
import static org.codice.ddf.admin.security.common.services.LdapClaimsHandlerServiceProperties.LDAP_CLAIMS_HANDLER_FEATURE;
import static org.codice.ddf.admin.security.common.services.LdapLoginServiceProperties.LDAP_LOGIN_FEATURE;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Set;
import org.codice.ddf.internal.admin.configurator.actions.ConfiguratorSuite;
import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.common.fields.base.BaseFunctionField;
import org.codice.ddf.admin.common.fields.base.scalar.BooleanField;
import org.codice.ddf.admin.common.report.message.DefaultMessages;
import org.codice.ddf.admin.configurator.Configurator;
import org.codice.ddf.admin.configurator.ConfiguratorFactory;
import org.codice.ddf.admin.configurator.OperationReport;
import org.codice.ddf.admin.security.common.fields.ldap.LdapUseCase;
import org.codice.ddf.internal.admin.configurator.actions.FeatureActions;

public class InstallEmbeddedLdap extends BaseFunctionField<BooleanField> {
  public static final String FIELD_NAME = "installEmbeddedLdap";

  public static final String DESCRIPTION =
      "Installs the internal embedded LDAP. Used for testing purposes only. LDAP port: 1389, LDAPS port: 1636, ADMIN port: 4444";

  public static final BooleanField RETURN_TYPE = new BooleanField();

  private LdapUseCase useCase;

  private final ConfiguratorSuite configuratorSuite;

  private final ConfiguratorFactory configuratorFactory;

  private final FeatureActions featureActions;

  public InstallEmbeddedLdap(ConfiguratorSuite configuratorSuite) {
    super(FIELD_NAME, DESCRIPTION);
    this.configuratorSuite = configuratorSuite;
    this.configuratorFactory = configuratorSuite.getConfiguratorFactory();
    this.featureActions = configuratorSuite.getFeatureActions();
    useCase = new LdapUseCase();
    useCase.isRequired(true);
  }

  @Override
  public List<Field> getArguments() {
    return ImmutableList.of(useCase);
  }

  @Override
  @SuppressWarnings("squid:SwitchLastCaseIsDefaultCheck" /* No default case in switch statement */)
  public BooleanField performFunction() {
    Configurator configurator = configuratorFactory.getConfigurator();
    configurator.add(featureActions.start(EMBEDDED_LDAP_FEATURE));

    switch (useCase.getValue()) {
      case AUTHENTICATION:
        configurator.add(featureActions.start(LDAP_LOGIN_FEATURE));
        configurator.add(featureActions.start(DEFAULT_EMBEDDED_LDAP_LOGIN_CONFIG_FEATURE));
        break;
      case ATTRIBUTE_STORE:
        configurator.add(featureActions.start(LDAP_CLAIMS_HANDLER_FEATURE));
        configurator.add(featureActions.start(DEFAULT_EMBEDDED_LDAP_CLAIMS_HANDLER_CONFIG_FEATURE));
        break;
      case AUTHENTICATION_AND_ATTRIBUTE_STORE:
        configurator.add(featureActions.start(LDAP_LOGIN_FEATURE));
        configurator.add(featureActions.start(LDAP_CLAIMS_HANDLER_FEATURE));
        configurator.add(featureActions.start(ALL_DEFAULT_EMBEDDED_LDAP_CONFIG_FEATURE));
        break;
    }

    OperationReport report = configurator.commit("Installed Embedded LDAP");

    if (report.containsFailedResults()) {
      addErrorMessage(failedPersistError());
    }

    return new BooleanField(!containsErrorMsgs());
  }

  @Override
  public BooleanField getReturnType() {
    return RETURN_TYPE;
  }

  @Override
  public FunctionField<BooleanField> newInstance() {
    return new InstallEmbeddedLdap(configuratorSuite);
  }

  @Override
  public Set<String> getFunctionErrorCodes() {
    return ImmutableSet.of(DefaultMessages.FAILED_PERSIST);
  }
}
