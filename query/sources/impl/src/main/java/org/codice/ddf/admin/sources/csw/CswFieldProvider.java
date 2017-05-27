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

import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.common.fields.base.function.BaseFieldProvider;
import org.codice.ddf.admin.configurator.ConfiguratorFactory;
import org.codice.ddf.admin.sources.csw.discover.DiscoverCswSource;
import org.codice.ddf.admin.sources.csw.discover.GetCswConfigurations;
import org.codice.ddf.admin.sources.csw.persist.DeleteCswConfiguration;
import org.codice.ddf.admin.sources.csw.persist.SaveCswConfiguration;

import com.google.common.collect.ImmutableList;

public class CswFieldProvider extends BaseFieldProvider {

    private static final String ID = "csw";

    private static final String TYPE_NAME = "Csw";

    private static final String DESCRIPTION =
            "Catalog Service for the Web - a standard used to expose geospatial data over the web. This is a source "
                    + "that implements the CSW specification and provides methods for discovering and persisting CSW sources.";

    private GetCswConfigurations getCswConfigurations;
    private DiscoverCswSource discoverCswSource;

    private SaveCswConfiguration saveCswConfiguration;
    private DeleteCswConfiguration deleteCswConfiguration;

    public CswFieldProvider(ConfiguratorFactory configuratorFactory) {
        super(ID, TYPE_NAME, DESCRIPTION);
        getCswConfigurations = new GetCswConfigurations(configuratorFactory);
        discoverCswSource = new DiscoverCswSource();

        saveCswConfiguration = new SaveCswConfiguration(configuratorFactory);
        deleteCswConfiguration = new DeleteCswConfiguration(configuratorFactory);
        updateInnerFieldPaths();
    }

    @Override
    public List<Field> getDiscoveryFields() {
        return ImmutableList.of(getCswConfigurations,
                discoverCswSource);
    }

    @Override
    public List<FunctionField> getMutationFunctions() {
        return ImmutableList.of(saveCswConfiguration, deleteCswConfiguration);
    }
}
