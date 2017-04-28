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
package org.codice.ddf.admin.ldap.persist;

import static org.apache.commons.lang.StringUtils.isNotEmpty;
import static org.codice.ddf.admin.common.report.message.DefaultMessages.failedPersistError;
import static org.codice.ddf.admin.common.report.message.DefaultMessages.noExistingConfigError;
import static org.codice.ddf.admin.ldap.fields.config.LdapUseCase.ATTRIBUTE_STORE;
import static org.codice.ddf.admin.ldap.fields.config.LdapUseCase.AUTHENTICATION;
import static org.codice.ddf.admin.ldap.fields.config.LdapUseCase.AUTHENTICATION_AND_ATTRIBUTE_STORE;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.codice.ddf.admin.api.DataType;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.api.fields.ListField;
import org.codice.ddf.admin.common.fields.base.BaseFunctionField;
import org.codice.ddf.admin.common.fields.base.ListFieldImpl;
import org.codice.ddf.admin.configurator.Configurator;
import org.codice.ddf.admin.configurator.ConfiguratorFactory;
import org.codice.ddf.admin.configurator.OperationReport;
import org.codice.ddf.admin.ldap.commons.LdapTestingUtils;
import org.codice.ddf.admin.ldap.commons.services.LdapServiceCommons;
import org.codice.ddf.admin.ldap.fields.config.LdapConfigurationField;
import org.codice.ddf.admin.security.common.services.LdapClaimsHandlerServiceProperties;
import org.codice.ddf.admin.security.common.services.LdapLoginServiceProperties;
import org.codice.ddf.internal.admin.configurator.actions.FeatureActions;
import org.codice.ddf.internal.admin.configurator.actions.ManagedServiceActions;
import org.codice.ddf.internal.admin.configurator.actions.PropertyActions;
import org.codice.ddf.internal.admin.configurator.actions.ServiceActions;

import com.google.common.collect.ImmutableList;

public class SaveLdapConfiguration extends BaseFunctionField<ListField<LdapConfigurationField>> {

    public static final String NAME = "saveLdapConfig";

    public static final String DESCRIPTION = "Saves the LDAP configuration.";

    private LdapConfigurationField config;

    private final ConfiguratorFactory configuratorFactory;

    private final FeatureActions featureActions;

    private final ManagedServiceActions managedServiceActions;

    private final PropertyActions propertyActions;

    private final ServiceActions serviceActions;

    private LdapServiceCommons serviceCommons;

    private LdapTestingUtils testingUtils;

    public SaveLdapConfiguration(ConfiguratorFactory configuratorFactory,
            FeatureActions featureActions, ManagedServiceActions managedServiceActions,
            PropertyActions propertyActions, ServiceActions serviceActions) {
        super(NAME, DESCRIPTION, new ListFieldImpl<>(LdapConfigurationField.class));
        this.configuratorFactory = configuratorFactory;
        this.featureActions = featureActions;
        this.managedServiceActions = managedServiceActions;
        this.propertyActions = propertyActions;
        this.serviceActions = serviceActions;

        config = new LdapConfigurationField();
        updateArgumentPaths();

        this.serviceCommons = new LdapServiceCommons(this.propertyActions,
                this.managedServiceActions);
        this.testingUtils = new LdapTestingUtils();
    }

    @Override
    public List<DataType> getArguments() {
        return ImmutableList.of(config);
    }

    @Override
    public ListField<LdapConfigurationField> performFunction() {
        Configurator configurator = configuratorFactory.getConfigurator();

        if (config.settingsField()
                .useCase()
                .equals(AUTHENTICATION) || config.settingsField()
                .useCase()
                .equals(AUTHENTICATION_AND_ATTRIBUTE_STORE)) {

            Map<String, Object> ldapLoginServiceProps = new LdapServiceCommons(propertyActions,
                    managedServiceActions).ldapConfigurationToLdapLoginService(config);
            configurator.add(featureActions.start(LdapLoginServiceProperties.LDAP_LOGIN_FEATURE));
            if (isNotEmpty(config.pid())) {
                configurator.add(serviceActions.build(config.pid(), ldapLoginServiceProps, false));
            } else {
                configurator.add(managedServiceActions.create(LdapLoginServiceProperties.LDAP_LOGIN_MANAGED_SERVICE_FACTORY_PID,
                        ldapLoginServiceProps));
            }
        }

        if (config.settingsField()
                .useCase()
                .equals(ATTRIBUTE_STORE) || config.settingsField()
                .useCase()
                .equals(AUTHENTICATION_AND_ATTRIBUTE_STORE)) {

            Path newAttributeMappingPath = Paths.get(System.getProperty("ddf.home"),
                    "etc",
                    "ws-security",
                    "ldapAttributeMap-" + UUID.randomUUID()
                            .toString() + ".props");
            Map<String, Object> ldapClaimsServiceProps =
                    LdapServiceCommons.ldapConfigToLdapClaimsHandlerService(config);
            configurator.add(propertyActions.create(newAttributeMappingPath,
                    config.settingsField()
                            .attributeMap()));
            configurator.add(featureActions.start(LdapClaimsHandlerServiceProperties.LDAP_CLAIMS_HANDLER_FEATURE));
            configurator.add(managedServiceActions.create(LdapClaimsHandlerServiceProperties.LDAP_CLAIMS_HANDLER_MANAGED_SERVICE_FACTORY_PID,
                    ldapClaimsServiceProps));
        }

        OperationReport report = configurator.commit("LDAP Configuration saved with details: {}",
                config.toString());

        if (report.containsFailedResults()) {
            addResultMessage(failedPersistError());
        }

        return serviceCommons.getLdapConfigurations();
    }

    @Override
    public void validate() {
        super.validate();
        if (containsErrorMsgs()) {
            return;
        }

        if (config.pid() != null && !testingUtils.serviceExists(config.pid(), serviceActions)) {
            addArgumentMessage(noExistingConfigError());
        } else {
            addResultMessages(testingUtils.ldapConnectionExists(config, managedServiceActions,
                    propertyActions));
        }
    }

    @Override
    public FunctionField<ListField<LdapConfigurationField>> newInstance() {
        return new SaveLdapConfiguration(configuratorFactory,
                featureActions,
                managedServiceActions,
                propertyActions,
                serviceActions);
    }
}
