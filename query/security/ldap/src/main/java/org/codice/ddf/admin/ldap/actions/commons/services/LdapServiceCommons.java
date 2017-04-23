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

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.codice.ddf.admin.api.fields.ListField;
import org.codice.ddf.admin.common.fields.base.ListFieldImpl;
import org.codice.ddf.admin.configurator.ConfiguratorFactory;
import org.codice.ddf.admin.ldap.fields.config.LdapConfigurationField;
import org.codice.ddf.admin.security.common.fields.ServiceCommons;

public class LdapServiceCommons {

    private ServiceCommons serviceCommons;

    public LdapServiceCommons(ConfiguratorFactory configuratorFactory) {
        serviceCommons = new ServiceCommons(configuratorFactory);
    }

    public LdapServiceCommons(ServiceCommons serviceCommons) {
        this.serviceCommons = serviceCommons;
    }

    public ListField<LdapConfigurationField> getLdapConfigurations(ConfiguratorFactory configuratorFactory) {
        List<LdapConfigurationField> ldapLoginConfigs = serviceCommons.getLdapLoginManagedServices()
                .values()
                .stream()
                .map(LdapLoginServiceProperties::ldapLoginServiceToLdapConfiguration)
                .collect(Collectors.toList());

        List<LdapConfigurationField> ldapClaimsHandlerConfigs = serviceCommons.getLdapClaimsHandlerManagedServices()
                .values()
                .stream()
                .map((props) -> LdapClaimsHandlerServiceProperties.ldapClaimsHandlerServiceToLdapConfig(
                        props,
                        configuratorFactory))
                .collect(Collectors.toList());

        List<LdapConfigurationField> configs = Stream.concat(ldapLoginConfigs.stream(), ldapClaimsHandlerConfigs.stream())
                .collect(Collectors.toList());

        configs.stream()
                .forEach(config -> config.bindUserInfoField()
                        .password("*******"));

        return new ListFieldImpl<>(LdapConfigurationField.class).addAll(configs);
    }
}
