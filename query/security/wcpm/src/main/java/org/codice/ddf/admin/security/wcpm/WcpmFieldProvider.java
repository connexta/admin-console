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
package org.codice.ddf.admin.security.wcpm;

import java.util.List;

import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.common.fields.base.function.BaseFieldProvider;
import org.codice.ddf.admin.configurator.ConfiguratorFactory;
import org.codice.ddf.admin.security.wcpm.discover.GetAuthTypes;
import org.codice.ddf.admin.security.wcpm.discover.GetContextPolicies;
import org.codice.ddf.admin.security.wcpm.discover.GetRealms;
import org.codice.ddf.admin.security.wcpm.discover.GetWhiteListContexts;
import org.codice.ddf.admin.security.wcpm.persist.SaveContextPolices;
import org.codice.ddf.admin.security.wcpm.persist.SaveWhitelistContexts;
import org.codice.ddf.internal.admin.configurator.actions.BundleActions;
import org.codice.ddf.internal.admin.configurator.actions.ManagedServiceActions;
import org.codice.ddf.internal.admin.configurator.actions.ServiceActions;
import org.codice.ddf.internal.admin.configurator.actions.ServiceReader;

import com.google.common.collect.ImmutableList;

public class WcpmFieldProvider extends BaseFieldProvider {

    public static final String NAME = "wcpm";

    public static final String TYPE_NAME = "WebContextPolicyManager";

    public static final String DESCRIPTION = "Manages policies for the system's endpoints";

    //Discovery functions
    private GetAuthTypes getAuthTypes;

    private GetRealms getRealms;

    private GetWhiteListContexts getWhiteListContexts;

    private GetContextPolicies getContextPolicies;

    //Mutation functions
    private SaveContextPolices saveContextPolices;

    private SaveWhitelistContexts saveWhitelistContexts;

    public WcpmFieldProvider(ConfiguratorFactory configuratorFactory, ServiceActions serviceActions,
            BundleActions bundleActions, ManagedServiceActions managedServiceActions,
            ServiceReader serviceReader) {
        super(NAME, TYPE_NAME, DESCRIPTION);
        getAuthTypes = new GetAuthTypes(configuratorFactory, serviceReader);
        getRealms = new GetRealms(managedServiceActions, bundleActions, serviceReader);
        getWhiteListContexts = new GetWhiteListContexts(serviceActions);
        getContextPolicies = new GetContextPolicies(serviceReader);

        saveContextPolices = new SaveContextPolices(configuratorFactory, serviceActions, serviceReader);
        saveWhitelistContexts = new SaveWhitelistContexts(configuratorFactory, serviceActions);
        updateInnerFieldPaths();
    }

    @Override
    public List<Field> getDiscoveryFields() {
        return ImmutableList.of(getAuthTypes, getRealms, getWhiteListContexts, getContextPolicies);
    }

    @Override
    public List<FunctionField> getMutationFunctions() {
        return ImmutableList.of(saveContextPolices, saveWhitelistContexts);
    }

}
