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
import org.codice.ddf.admin.sources.opensearch.discover.DiscoverOpenSearchByAddressAction;
import org.codice.ddf.admin.sources.opensearch.discover.DiscoverOpenSearchByUrlAction;
import org.codice.ddf.admin.sources.opensearch.discover.GetOpenSearchConfigsAction;
import org.codice.ddf.admin.sources.opensearch.persist.DeleteOpenSearchConfiguration;
import org.codice.ddf.admin.sources.opensearch.persist.SaveOpenSearchConfiguration;

import com.google.common.collect.ImmutableList;

public class OpenSearchActionCreator extends BaseActionCreator {

    private static final String ID = "openSearch";

    private static final String TYPE_NAME = "OpenSearch";

    private static final String DESCRIPTION = "OpenSearch description.";

    private ConfiguratorFactory configuratorFactory;


    public OpenSearchActionCreator() {
        super(ID, TYPE_NAME, DESCRIPTION);
    }

    @Override
    public List<Action> getDiscoveryActions() {
        return ImmutableList.of(new DiscoverOpenSearchByAddressAction(),
                new GetOpenSearchConfigsAction(configuratorFactory),
                new DiscoverOpenSearchByUrlAction());
    }

    @Override
    public List<Action> getPersistActions() {
        return ImmutableList.of(new SaveOpenSearchConfiguration(configuratorFactory),
                new DeleteOpenSearchConfiguration(configuratorFactory));
    }

    public void setConfiguratorFactory(ConfiguratorFactory configuratorFactory) {
        this.configuratorFactory = configuratorFactory;
    }
}
