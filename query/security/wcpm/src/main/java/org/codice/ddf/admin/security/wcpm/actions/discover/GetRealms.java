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
package org.codice.ddf.admin.security.wcpm.actions.discover;

import static org.codice.ddf.admin.security.common.fields.wcpm.services.PolicyManagerServiceProperties.IDP_SERVER_BUNDLE_NAME;

import java.util.Map;

import org.codice.ddf.admin.api.fields.ListField;
import org.codice.ddf.admin.common.actions.GetAction;
import org.codice.ddf.admin.common.fields.base.ListFieldImpl;
import org.codice.ddf.admin.configurator.ConfiguratorFactory;
import org.codice.ddf.admin.security.common.fields.ServiceCommons;
import org.codice.ddf.admin.security.common.fields.wcpm.Realm;

public class GetRealms extends GetAction<ListField<Realm>> {

    public static final String FIELD_NAME = "realms";

    public static final String DESCRIPTION = "Retrieves all currently configured realms.";

    ConfiguratorFactory configuratorFactory;

    ServiceCommons serviceCommons;

    public GetRealms(ConfiguratorFactory configuratorFactory) {
        super(FIELD_NAME, DESCRIPTION, new ListFieldImpl<>(Realm.class));
        this.configuratorFactory = configuratorFactory;
        serviceCommons = new ServiceCommons(configuratorFactory);
    }

    @Override
    public ListField<Realm> performAction() {
        ListField<Realm> realms = new ListFieldImpl<>(Realm.class);
        realms.add(Realm.KARAF_REALM);

        if (configuratorFactory.getConfigReader()
                .isBundleStarted(IDP_SERVER_BUNDLE_NAME)) {
            // TODO: 4/19/17 How are we going to treat/display IdP as an auth type
        }

        Map<String, Map<String, Object>> ldapConfigs = serviceCommons.getLdapLoginManagedServices();
        if(ldapConfigs.isEmpty()) {
            return realms;
        }

        realms.add(Realm.LDAP_REALM);
        return realms;
    }
}
