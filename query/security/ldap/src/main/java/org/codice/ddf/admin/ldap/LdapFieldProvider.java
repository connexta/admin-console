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
package org.codice.ddf.admin.ldap;

import java.util.List;

import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.common.fields.base.function.BaseFieldProvider;
import org.codice.ddf.admin.configurator.ConfiguratorFactory;
import org.codice.ddf.admin.ldap.discover.LdapConfigurations;
import org.codice.ddf.admin.ldap.discover.LdapQuery;
import org.codice.ddf.admin.ldap.discover.LdapRecommendedSettings;
import org.codice.ddf.admin.ldap.discover.LdapTestAttributeMappings;
import org.codice.ddf.admin.ldap.discover.LdapTestBind;
import org.codice.ddf.admin.ldap.discover.LdapTestConnection;
import org.codice.ddf.admin.ldap.discover.LdapTestSettings;
import org.codice.ddf.admin.ldap.discover.LdapUserAttributes;
import org.codice.ddf.admin.ldap.embedded.InstallEmbeddedLdap;
import org.codice.ddf.admin.ldap.persist.CreateLdapConfiguration;
import org.codice.ddf.admin.ldap.persist.DeleteLdapConfiguration;
import org.codice.ddf.internal.admin.configurator.actions.FeatureActions;
import org.codice.ddf.internal.admin.configurator.actions.ManagedServiceActions;
import org.codice.ddf.internal.admin.configurator.actions.PropertyActions;
import org.codice.ddf.internal.admin.configurator.actions.ServiceActions;

import com.google.common.collect.ImmutableList;

public class LdapFieldProvider extends BaseFieldProvider {
    public static final String DESCRIPTION = "Facilities for interacting with LDAP servers.";

    private static final String NAME = "ldap";

    private static final String TYPE_NAME = "Ldap";

    //Discovery
    private LdapTestConnection testConnection;
    private LdapTestBind testBind;
    private LdapTestSettings testSettings;
    private LdapRecommendedSettings recommendedSettings;

    private LdapTestAttributeMappings attributeMappings;
    private LdapQuery ldapQuery;
    private LdapUserAttributes getUserAttris;
    private LdapConfigurations getConfigs;

    //Mutate
    private CreateLdapConfiguration createConfig;
    private DeleteLdapConfiguration deleteConfig;
    private InstallEmbeddedLdap installEmbeddedLdap;

    public LdapFieldProvider(ConfiguratorFactory configuratorFactory, FeatureActions featureActions,
            ManagedServiceActions managedServiceActions, PropertyActions propertyActions,
            ServiceActions serviceActions) {
        super(NAME, TYPE_NAME, DESCRIPTION);
        testConnection = new LdapTestConnection();
        testBind = new LdapTestBind();
        testSettings = new LdapTestSettings();
        recommendedSettings = new LdapRecommendedSettings();
        attributeMappings = new LdapTestAttributeMappings();

        ldapQuery = new LdapQuery();
        getUserAttris = new LdapUserAttributes();
        getConfigs = new LdapConfigurations(managedServiceActions, propertyActions);

        createConfig = new CreateLdapConfiguration(configuratorFactory,
                featureActions,
                managedServiceActions,
                propertyActions);
        deleteConfig = new DeleteLdapConfiguration(configuratorFactory,
                managedServiceActions,
                propertyActions,
                serviceActions);
        installEmbeddedLdap = new InstallEmbeddedLdap(configuratorFactory, featureActions);
    }

    @Override
    public List<Field> getDiscoveryFields() {
        return ImmutableList.of(testConnection,
                testBind,
                testSettings,
                recommendedSettings, attributeMappings,
                ldapQuery,
                getUserAttris,
                getConfigs);
    }

    @Override
    public List<FunctionField> getMutationFunctions() {
        return ImmutableList.of(createConfig, deleteConfig, installEmbeddedLdap);
    }
}
