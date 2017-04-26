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
package org.codice.ddf.admin.sources.csw;

import java.util.List;

import org.codice.ddf.admin.api.action.Action;
import org.codice.ddf.admin.common.actions.BaseActionCreator;
import org.codice.ddf.admin.configurator.ConfiguratorFactory;
import org.codice.ddf.admin.sources.csw.discover.DiscoverCswByAddressAction;
import org.codice.ddf.admin.sources.csw.discover.DiscoverCswByUrlAction;
import org.codice.ddf.admin.sources.csw.discover.GetCswConfigsAction;
import org.codice.ddf.admin.sources.csw.persist.DeleteCswConfiguration;
import org.codice.ddf.admin.sources.csw.persist.SaveCswConfiguration;

import com.google.common.collect.ImmutableList;

public class CswActionCreator extends BaseActionCreator {

    private static final String ID = "csw";

    private static final String TYPE_NAME = "Csw";

    private static final String DESCRIPTION =
            "Catalog Service for the Web - a standard used to expose geospatial data over the web. This is a source "
                    + "that implements the CSW specification and provides methods for discovering and persisting CSW sources.";

    private ConfiguratorFactory configuratorFactory;


    public CswActionCreator() {
        super(ID, TYPE_NAME, DESCRIPTION);
    }

    @Override
    public List<Action> getDiscoveryActions() {
        return ImmutableList.of(new DiscoverCswByAddressAction(),
                new GetCswConfigsAction(configuratorFactory),
                new DiscoverCswByUrlAction());
    }

    @Override
    public List<Action> getPersistActions() {
        return ImmutableList.of(new SaveCswConfiguration(configuratorFactory),
                new DeleteCswConfiguration(configuratorFactory));
    }

    public void setConfiguratorFactory(ConfiguratorFactory configuratorFactory) {
        this.configuratorFactory = configuratorFactory;
    }
}
