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

package org.codice.ddf.admin.security.ldap;

import static org.codice.ddf.admin.api.services.LdapClaimsHandlerServiceProperties.LDAP_CLAIMS_HANDLER_MANAGED_SERVICE_FACTORY_PID;
import static org.codice.ddf.admin.api.services.LdapLoginServiceProperties.LDAP_LOGIN_MANAGED_SERVICE_FACTORY_PID;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.codice.ddf.admin.api.config.ConfigurationType;
import org.codice.ddf.admin.api.config.ldap.LdapConfiguration;
import org.codice.ddf.admin.api.handler.DefaultConfigurationHandler;
import org.codice.ddf.admin.api.handler.method.PersistMethod;
import org.codice.ddf.admin.api.handler.method.ProbeMethod;
import org.codice.ddf.admin.api.handler.method.TestMethod;
import org.codice.ddf.admin.api.services.LdapClaimsHandlerServiceProperties;
import org.codice.ddf.admin.api.services.LdapLoginServiceProperties;
import org.codice.ddf.admin.configurator.Configurator;
import org.codice.ddf.admin.configurator.ConfiguratorFactory;
import org.codice.ddf.admin.security.ldap.persist.CreateLdapConfigMethod;
import org.codice.ddf.admin.security.ldap.persist.DeleteLdapConfigMethod;
import org.codice.ddf.admin.security.ldap.probe.DefaultDirectoryStructureProbe;
import org.codice.ddf.admin.security.ldap.probe.LdapQueryProbe;
import org.codice.ddf.admin.security.ldap.probe.SubjectAttributeProbe;
import org.codice.ddf.admin.security.ldap.test.AttributeMappingTestMethod;
import org.codice.ddf.admin.security.ldap.test.BindUserTestMethod;
import org.codice.ddf.admin.security.ldap.test.ConnectTestMethod;
import org.codice.ddf.admin.security.ldap.test.DirectoryStructTestMethod;
import org.codice.ddf.admin.security.ldap.test.LdapTestingCommons;

import com.google.common.collect.ImmutableList;

public class LdapConfigurationHandler extends DefaultConfigurationHandler<LdapConfiguration> {
    private static final String LDAP_CONFIGURATION_HANDLER_ID =
            LdapConfiguration.CONFIGURATION_TYPE;

    private final ConfiguratorFactory configuratorFactory;

    public LdapConfigurationHandler(ConfiguratorFactory configuratorFactory) {
        this.configuratorFactory = configuratorFactory;
    }

    @Override
    public String getConfigurationHandlerId() {
        return LDAP_CONFIGURATION_HANDLER_ID;
    }

    @Override
    public ConfigurationType getConfigurationType() {
        return new LdapConfiguration().getConfigurationType();
    }

    @Override
    public List<ProbeMethod> getProbeMethods() {
        LdapTestingCommons ldapTestingCommons = new LdapTestingCommons();
        Configurator configurator = configuratorFactory.getConfigurator();
        return ImmutableList.of(new DefaultDirectoryStructureProbe(ldapTestingCommons),
                new LdapQueryProbe(ldapTestingCommons),
                new SubjectAttributeProbe(ldapTestingCommons, configurator));
    }

    @Override
    public List<TestMethod> getTestMethods() {
        LdapTestingCommons ldapTestingCommons = new LdapTestingCommons();
        return ImmutableList.of(new ConnectTestMethod(ldapTestingCommons),
                new BindUserTestMethod(ldapTestingCommons),
                new DirectoryStructTestMethod(ldapTestingCommons),
                new AttributeMappingTestMethod(ldapTestingCommons,
                        configuratorFactory.getConfigurator()));
    }

    @Override
    public List<PersistMethod> getPersistMethods() {
        return ImmutableList.of(new CreateLdapConfigMethod(configuratorFactory),
                new DeleteLdapConfigMethod(configuratorFactory));
    }

    @Override
    public List<LdapConfiguration> getConfigurations() {
        List<LdapConfiguration> ldapLoginConfigs = configuratorFactory.getConfigurator()
                .getManagedServiceConfigs(LDAP_LOGIN_MANAGED_SERVICE_FACTORY_PID)
                .values()
                .stream()
                .map(LdapLoginServiceProperties::ldapLoginServiceToLdapConfiguration)
                .collect(Collectors.toList());

        List<LdapConfiguration> ldapClaimsHandlerConfigs = configuratorFactory.getConfigurator()
                .getManagedServiceConfigs(LDAP_CLAIMS_HANDLER_MANAGED_SERVICE_FACTORY_PID)
                .values()
                .stream()
                .map((props) -> LdapClaimsHandlerServiceProperties.ldapClaimsHandlerServiceToLdapConfig(
                        props,
                        configuratorFactory.getConfigurator()))
                .collect(Collectors.toList());

        return Stream.concat(ldapLoginConfigs.stream(), ldapClaimsHandlerConfigs.stream())
                .map(config -> config.bindUserPassword("*******"))
                .collect(Collectors.toList());
    }
}

