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
package org.codice.ddf.admin.ldap.actions;

import java.util.Arrays;
import java.util.List;

import org.codice.ddf.admin.api.action.Action;
import org.codice.ddf.admin.common.actions.BaseActionCreator;
import org.codice.ddf.admin.configurator.ConfiguratorFactory;
import org.codice.ddf.admin.ldap.actions.discover.LdapConfigurations;
import org.codice.ddf.admin.ldap.actions.discover.LdapQuery;
import org.codice.ddf.admin.ldap.actions.discover.LdapRecommendedSettings;
import org.codice.ddf.admin.ldap.actions.discover.LdapTestBind;
import org.codice.ddf.admin.ldap.actions.discover.LdapTestConnection;
import org.codice.ddf.admin.ldap.actions.discover.LdapTestSettings;
import org.codice.ddf.admin.ldap.actions.discover.LdapUserAttributes;
import org.codice.ddf.admin.ldap.actions.embedded.InstallEmbeddedLdap;
import org.codice.ddf.admin.ldap.actions.persist.DeleteLdapConfiguration;
import org.codice.ddf.admin.ldap.actions.persist.SaveLdapConfiguration;

public class LdapActionCreator extends BaseActionCreator {

    public static final String NAME = "ldap";

    public static final String TYPE_NAME = "Ldap";

    public static final String DESCRIPTION = "Facilities for interacting with LDAP servers.";

    private ConfiguratorFactory configuratorFactory;

    public LdapActionCreator(ConfiguratorFactory configuratorFactory) {
        super(NAME, TYPE_NAME, DESCRIPTION);
        this.configuratorFactory = configuratorFactory;
    }

    @Override
    public List<Action> getDiscoveryActions() {
        return Arrays.asList(new LdapRecommendedSettings(),
                new LdapTestConnection(),
                new LdapTestBind(),
                new LdapTestSettings(),
                new LdapQuery(),
                new LdapUserAttributes(),
                new LdapConfigurations(configuratorFactory));
    }

    @Override
    public List<Action> getPersistActions() {
        return Arrays.asList(new SaveLdapConfiguration(configuratorFactory), new DeleteLdapConfiguration(configuratorFactory), new InstallEmbeddedLdap(configuratorFactory.getConfigurator()));
    }
}
