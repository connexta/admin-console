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
import org.codice.ddf.admin.common.fields.base.function.GetFunctionField;
import org.codice.ddf.admin.security.common.fields.wcpm.Realm;
import org.codice.ddf.admin.security.common.services.LdapLoginServiceProperties;
import org.codice.ddf.internal.admin.configurator.actions.BundleActions;
import org.codice.ddf.internal.admin.configurator.actions.ManagedServiceActions;
import org.codice.ddf.internal.admin.configurator.actions.ServiceReader;

public class GetRealms extends GetFunctionField<Realm.Realms> {

    public static final String FIELD_NAME = "realms";

    public static final String DESCRIPTION = "Retrieves all currently configured realms.";

    private final ManagedServiceActions managedServiceActions;

    private final BundleActions bundleActions;

    private ServiceReader serviceReader;

    LdapLoginServiceProperties serviceCommons;

    public GetRealms(ManagedServiceActions managedServiceActions, BundleActions bundleActions, ServiceReader serviceReader) {
        super(FIELD_NAME, DESCRIPTION);
        this.managedServiceActions = managedServiceActions;
        serviceCommons = new LdapLoginServiceProperties(managedServiceActions);
        this.bundleActions = bundleActions;
        this.serviceReader = serviceReader;
    }

    @Override
    public Realm.Realms performFunction() {
        Realm.Realms realms = new Realm.Realms(serviceReader);
        return realms;
    }

    @Override
    public Realm.Realms getReturnType() {
        return new Realm.Realms(serviceReader);
    }

    @Override
    public FunctionField<Realm.Realms> newInstance() {
        return new GetRealms(managedServiceActions, bundleActions, serviceReader);
    }
}
