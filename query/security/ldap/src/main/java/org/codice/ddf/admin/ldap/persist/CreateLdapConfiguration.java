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

import static org.codice.ddf.admin.common.report.message.DefaultMessages.failedPersistError;
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
import org.codice.ddf.admin.common.fields.base.BaseFunctionField;
import org.codice.ddf.admin.common.fields.base.scalar.BooleanField;
import org.codice.ddf.admin.configurator.Configurator;
import org.codice.ddf.admin.configurator.ConfiguratorFactory;
import org.codice.ddf.admin.configurator.OperationReport;
import org.codice.ddf.admin.ldap.commons.LdapServiceCommons;
import org.codice.ddf.admin.ldap.fields.config.LdapConfigurationField;
import org.codice.ddf.admin.security.common.services.LdapClaimsHandlerServiceProperties;
import org.codice.ddf.admin.security.common.services.LdapLoginServiceProperties;
import org.codice.ddf.internal.admin.configurator.actions.FeatureActions;
import org.codice.ddf.internal.admin.configurator.actions.ManagedServiceActions;
import org.codice.ddf.internal.admin.configurator.actions.PropertyActions;

import com.google.common.collect.ImmutableList;

public class CreateLdapConfiguration extends BaseFunctionField<BooleanField> {

    public static final String FIELD_NAME = "createLdapConfig";

    public static final String DESCRIPTION = "Creates a LDAP configuration.";

    private LdapConfigurationField config;

    private final ConfiguratorFactory configuratorFactory;

    private final FeatureActions featureActions;

    private final ManagedServiceActions managedServiceActions;

    private final PropertyActions propertyActions;

    private LdapServiceCommons ldapServiceCommons;

    public CreateLdapConfiguration(ConfiguratorFactory configuratorFactory,
            FeatureActions featureActions, ManagedServiceActions managedServiceActions,
            PropertyActions propertyActions) {
        super(FIELD_NAME, DESCRIPTION, new BooleanField());
        this.configuratorFactory = configuratorFactory;
        this.featureActions = featureActions;
        this.managedServiceActions = managedServiceActions;
        this.propertyActions = propertyActions;

        config = new LdapConfigurationField().useDefaultRequired();
        updateArgumentPaths();

        this.ldapServiceCommons = new LdapServiceCommons(this.propertyActions,
                this.managedServiceActions);
    }

    @Override
    public List<DataType> getArguments() {
        return ImmutableList.of(config);
    }

    @Override
    public BooleanField performFunction() {
        Configurator configurator = configuratorFactory.getConfigurator();

        switch (config.settingsField()
                .useCase()) {
        case AUTHENTICATION:
        case AUTHENTICATION_AND_ATTRIBUTE_STORE: {
            Map<String, Object> ldapLoginServiceProps =
                    ldapServiceCommons.ldapConfigurationToLdapLoginService(config);
            configurator.add(featureActions.start(LdapLoginServiceProperties.LDAP_LOGIN_FEATURE));
            configurator.add(managedServiceActions.create(LdapLoginServiceProperties.LDAP_LOGIN_MANAGED_SERVICE_FACTORY_PID,
                    ldapLoginServiceProps));
        }
        }

        switch (config.settingsField()
                .useCase()) {
        case ATTRIBUTE_STORE:
        case AUTHENTICATION_AND_ATTRIBUTE_STORE: {
            Path newAttributeMappingPath = Paths.get(System.getProperty("ddf.home"),
                    "etc",
                    "ws-security",
                    "ldapAttributeMap-" + UUID.randomUUID()
                            .toString() + ".props");
            Map<String, Object> ldapClaimsServiceProps =
                    ldapServiceCommons.ldapConfigToLdapClaimsHandlerService(config,
                            newAttributeMappingPath.toString());
            configurator.add(propertyActions.create(newAttributeMappingPath,
                    config.claimsMapping()));
            configurator.add(featureActions.start(LdapClaimsHandlerServiceProperties.LDAP_CLAIMS_HANDLER_FEATURE));
            configurator.add(managedServiceActions.create(LdapClaimsHandlerServiceProperties.LDAP_CLAIMS_HANDLER_MANAGED_SERVICE_FACTORY_PID,
                    ldapClaimsServiceProps));
        }
        }

        OperationReport report = configurator.commit("Creating LDAP configuration.");

        if (report.containsFailedResults()) {
            addResultMessage(failedPersistError());
        }

        return new BooleanField(!containsErrorMsgs());
    }

    @Override
    public void validate() {
        String useCase = config.settingsField()
                .useCase();
        if (useCase != null && (useCase.equals(ATTRIBUTE_STORE) || useCase.equals(
                AUTHENTICATION_AND_ATTRIBUTE_STORE))) {
            config.settingsField()
                    .useDefaultRequiredForAttributeStore();
            config.claimMappingsField()
                    .isRequired(true);
        }

        super.validate();
        if (containsErrorMsgs()) {
            return;
        }

        addMessages(ldapServiceCommons.validateSimilarLdapServiceExists(config));
    }

    @Override
    public FunctionField<BooleanField> newInstance() {
        return new CreateLdapConfiguration(configuratorFactory,
                featureActions,
                managedServiceActions,
                propertyActions);
    }
}
