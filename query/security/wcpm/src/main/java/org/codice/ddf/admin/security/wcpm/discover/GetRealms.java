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
package org.codice.ddf.admin.security.wcpm.discover;

import static org.codice.ddf.admin.security.common.services.PolicyManagerServiceProperties.IDP_SERVER_BUNDLE_NAME;

import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.api.fields.ListField;
import org.codice.ddf.admin.common.fields.base.ListFieldImpl;
import org.codice.ddf.admin.common.fields.base.function.GetFunctionField;
import org.codice.ddf.admin.security.common.fields.wcpm.Realm;
import org.codice.ddf.admin.security.common.services.LdapLoginServiceProperties;
import org.codice.ddf.internal.admin.configurator.actions.BundleActions;
import org.codice.ddf.internal.admin.configurator.actions.ManagedServiceActions;

public class GetRealms extends GetFunctionField<ListField<Realm>> {

    public static final String FIELD_NAME = "realms";

    public static final String DESCRIPTION = "Retrieves all currently configured realms.";

    private final ManagedServiceActions managedServiceActions;

    private final BundleActions bundleActions;

    LdapLoginServiceProperties serviceCommons;

    public GetRealms(ManagedServiceActions managedServiceActions, BundleActions bundleActions) {
        super(FIELD_NAME, DESCRIPTION);
        this.managedServiceActions = managedServiceActions;
        serviceCommons = new LdapLoginServiceProperties(managedServiceActions);
        this.bundleActions = bundleActions;
    }

    @Override
    public ListField<Realm> performFunction() {
        ListField<Realm> realms = new ListFieldImpl<>(Realm.class);
        realms.add(Realm.KARAF_REALM);

        if (bundleActions.isStarted(IDP_SERVER_BUNDLE_NAME)) {
            // TODO: 4/19/17 How are we going to treat/display IdP as an auth type
        }

        if (!serviceCommons.getLdapLoginManagedServices()
                .keySet()
                .isEmpty()) {
            realms.add(Realm.LDAP_REALM);
        }

        return realms;
    }

    @Override
    public ListField<Realm> getReturnType() {
        return new ListFieldImpl<>(Realm.class);
    }

    @Override
    public FunctionField<ListField<Realm>> newInstance() {
        return new GetRealms(managedServiceActions, bundleActions);
    }
}
