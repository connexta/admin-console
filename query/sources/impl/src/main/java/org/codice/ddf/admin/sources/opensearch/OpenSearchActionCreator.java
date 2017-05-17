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
package org.codice.ddf.admin.sources.opensearch;

import java.util.List;

import org.codice.ddf.admin.api.action.Action;
import org.codice.ddf.admin.common.actions.BaseActionCreator;
import org.codice.ddf.admin.configurator.ConfiguratorFactory;
import org.codice.ddf.admin.sources.opensearch.discover.DiscoverOpenSearchAction;
import org.codice.ddf.admin.sources.opensearch.discover.GetOpenSearchConfigsAction;
import org.codice.ddf.admin.sources.opensearch.persist.DeleteOpenSearchConfiguration;
import org.codice.ddf.admin.sources.opensearch.persist.SaveOpenSearchConfiguration;
import org.codice.ddf.internal.admin.configurator.opfactory.AdminOpFactory;
import org.codice.ddf.internal.admin.configurator.opfactory.ManagedServiceOpFactory;
import org.codice.ddf.internal.admin.configurator.opfactory.ServiceReader;

import com.google.common.collect.ImmutableList;

public class OpenSearchActionCreator extends BaseActionCreator {

    private static final String ID = "openSearch";

    private static final String TYPE_NAME = "OpenSearch";

    private static final String DESCRIPTION =
            "A specification for querying geospatial data using standard data formats. This is a source that implements the OpenSearch specification.";

    private ConfiguratorFactory configuratorFactory;

    private AdminOpFactory adminOpFactory;

    private ManagedServiceOpFactory managedServiceOpFactory;

    private ServiceReader serviceReader;

    public OpenSearchActionCreator() {
        super(ID, TYPE_NAME, DESCRIPTION);
    }

    @Override
    public List<Action> getDiscoveryActions() {
        return ImmutableList.of(new DiscoverOpenSearchAction(),
                new GetOpenSearchConfigsAction(adminOpFactory,
                        managedServiceOpFactory,
                        serviceReader));
    }

    @Override
    public List<Action> getPersistActions() {
        return ImmutableList.of(new SaveOpenSearchConfiguration(configuratorFactory,
                        adminOpFactory,
                        managedServiceOpFactory, serviceReader),
                new DeleteOpenSearchConfiguration(configuratorFactory,
                        managedServiceOpFactory,
                        adminOpFactory));
    }

    public void setConfiguratorFactory(ConfiguratorFactory configuratorFactory) {
        this.configuratorFactory = configuratorFactory;
    }

    public void setAdminOpFactory(AdminOpFactory adminOpFactory) {
        this.adminOpFactory = adminOpFactory;
    }

    public void setManagedServiceOpFactory(ManagedServiceOpFactory managedServiceOpFactory) {
        this.managedServiceOpFactory = managedServiceOpFactory;
    }

    public void setServiceReader(ServiceReader serviceReader) {
        this.serviceReader = serviceReader;
    }
}
