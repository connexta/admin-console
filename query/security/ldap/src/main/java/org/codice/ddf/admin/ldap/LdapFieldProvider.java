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

import java.util.Arrays;
import java.util.List;

import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.common.fields.base.function.BaseFieldProvider;
import org.codice.ddf.admin.configurator.ConfiguratorFactory;
import org.codice.ddf.admin.ldap.discover.LdapTestBind;
import org.codice.ddf.admin.ldap.discover.LdapConfigurations;
import org.codice.ddf.admin.ldap.discover.LdapQuery;
import org.codice.ddf.admin.ldap.discover.LdapRecommendedSettings;
import org.codice.ddf.admin.ldap.discover.LdapTestConnection;
import org.codice.ddf.admin.ldap.discover.LdapTestSettings;
import org.codice.ddf.admin.ldap.discover.LdapUserAttributes;
import org.codice.ddf.admin.ldap.embedded.InstallEmbeddedLdap;
import org.codice.ddf.admin.ldap.persist.DeleteLdapConfiguration;
import org.codice.ddf.admin.ldap.persist.SaveLdapConfiguration;

public class LdapFieldProvider extends BaseFieldProvider {

    public static final String NAME = "ldap";

    public static final String TYPE_NAME = "Ldap";

    public static final String DESCRIPTION = "Facilities for interacting with LDAP servers.";

    private ConfiguratorFactory configuratorFactory;

    //Discovery functions
    private LdapRecommendedSettings getRecommendedSettings;
    private LdapTestConnection testConnection;
    private LdapTestBind testBind;
    private LdapTestSettings testSettings;
    private LdapQuery runLdapQuery;
    private LdapUserAttributes getLdapUserAttributes;
    private LdapConfigurations getLdapConfigs;

    //Mutation functions
    private SaveLdapConfiguration saveConfig;
    private DeleteLdapConfiguration deleteConfig;
    private InstallEmbeddedLdap installEmbeddedLdap;

    public LdapFieldProvider(ConfiguratorFactory configuratorFactory) {
        super(NAME, TYPE_NAME, DESCRIPTION);
        this.configuratorFactory = configuratorFactory;

        getRecommendedSettings = new LdapRecommendedSettings();
        testConnection = new LdapTestConnection();
        testBind = new LdapTestBind();
        testSettings = new LdapTestSettings();
        runLdapQuery = new LdapQuery();
        getLdapUserAttributes = new LdapUserAttributes();
        getLdapConfigs = new LdapConfigurations(configuratorFactory);

        saveConfig = new SaveLdapConfiguration(configuratorFactory);
        deleteConfig = new DeleteLdapConfiguration(configuratorFactory);
        installEmbeddedLdap = new InstallEmbeddedLdap(configuratorFactory);
        updateInnerFieldPaths();
    }

    @Override
    public List<Field> getDiscoveryFields() {
        return Arrays.asList(getRecommendedSettings,
                testConnection,
                testBind,
                testSettings,
                runLdapQuery,
                getLdapUserAttributes,
                getLdapConfigs);
    }

    @Override
    public List<FunctionField> getMutationFunctions() {
        return Arrays.asList(saveConfig,
                deleteConfig,
                installEmbeddedLdap);
    }
}
