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

import static org.codice.ddf.admin.ldap.fields.config.LdapUseCase.ATTRIBUTE_STORE;
import static org.codice.ddf.admin.ldap.fields.config.LdapUseCase.LOGIN;
import static org.codice.ddf.admin.ldap.fields.config.LdapUseCase.LOGIN_AND_ATTRIBUTE_STORE;

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
import org.codice.ddf.admin.ldap.commons.services.LdapServiceCommons;
import org.codice.ddf.admin.ldap.fields.config.LdapConfigurationField;
import org.codice.ddf.admin.security.common.services.LdapClaimsHandlerServiceProperties;
import org.codice.ddf.admin.security.common.services.LdapLoginServiceProperties;

import com.google.common.collect.ImmutableList;

public class SaveLdapConfiguration extends BaseFunctionField<ListField<LdapConfigurationField>> {

    public static final String NAME = "saveLdapConfig";

    public static final String DESCRIPTION = "Saves the LDAP configuration.";

    private LdapConfigurationField config;
    private ConfiguratorFactory configuratorFactory;
    private LdapServiceCommons serviceCommons;

    public SaveLdapConfiguration(ConfiguratorFactory configuratorFactory) {
        super(NAME, DESCRIPTION, new ListFieldImpl<>(LdapConfigurationField.class));
        config = new LdapConfigurationField();
        updateArgumentPaths();

        this.configuratorFactory = configuratorFactory;
        this.serviceCommons = new LdapServiceCommons(configuratorFactory);
    }

    @Override
    public List<DataType> getArguments() {
        return ImmutableList.of(config);
    }

    @Override
    public ListField<LdapConfigurationField> performFunction() {
        Configurator configurator = configuratorFactory.getConfigurator();
        if (config.settingsField().useCase()
                .equals(LOGIN) || config.settingsField().useCase()
                .equals(LOGIN_AND_ATTRIBUTE_STORE)) {

            Map<String, Object> ldapLoginServiceProps = serviceCommons.ldapConfigurationToLdapLoginService(config);
            configurator.startFeature(LdapLoginServiceProperties.LDAP_LOGIN_FEATURE);
            configurator.createManagedService(LdapLoginServiceProperties.LDAP_LOGIN_MANAGED_SERVICE_FACTORY_PID,
                    ldapLoginServiceProps);
        }

        if (config.settingsField().useCase()
                .equals(ATTRIBUTE_STORE) || config.settingsField().useCase()
                .equals(LOGIN_AND_ATTRIBUTE_STORE)) {

            Path newAttributeMappingPath = Paths.get(System.getProperty("ddf.home"),
                    "etc",
                    "ws-security",
                    "ldapAttributeMap-" + UUID.randomUUID()
                            .toString() + ".props");
            Map<String, Object> ldapClaimsServiceProps = serviceCommons.ldapConfigToLdapClaimsHandlerService(config);
            configurator.createPropertyFile(newAttributeMappingPath, config.settingsField().attributeMap());
            configurator.startFeature(LdapClaimsHandlerServiceProperties.LDAP_CLAIMS_HANDLER_FEATURE);
            configurator.createManagedService(LdapClaimsHandlerServiceProperties.LDAP_CLAIMS_HANDLER_MANAGED_SERVICE_FACTORY_PID,
                    ldapClaimsServiceProps);
        }

        OperationReport report = configurator.commit("LDAP Configuration saved with details: {}",
                config.toString());

        // TODO: tbatie - 4/3/17 - Handle error messages
        return serviceCommons.getLdapConfigurations(configuratorFactory);
    }

    @Override
    public FunctionField<ListField<LdapConfigurationField>> newInstance() {
        return new SaveLdapConfiguration(configuratorFactory);
    }
}
