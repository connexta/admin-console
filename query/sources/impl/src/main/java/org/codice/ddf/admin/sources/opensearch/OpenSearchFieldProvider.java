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

import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.common.fields.base.function.BaseFieldProvider;
import org.codice.ddf.admin.configurator.ConfiguratorFactory;
import org.codice.ddf.admin.sources.opensearch.discover.DiscoverOpenSearchSource;
import org.codice.ddf.admin.sources.opensearch.discover.GetOpenSearchConfigurations;
import org.codice.ddf.admin.sources.opensearch.persist.DeleteOpenSearchConfiguration;
import org.codice.ddf.admin.sources.opensearch.persist.SaveOpenSearchConfiguration;

import com.google.common.collect.ImmutableList;

public class OpenSearchFieldProvider extends BaseFieldProvider {

    private static final String ID = "openSearch";

    private static final String TYPE_NAME = "OpenSearch";

    private static final String DESCRIPTION =
            "A specification for querying geospatial data using standard data formats. This is a source that implements the OpenSearch specification.";

    private DiscoverOpenSearchSource discoverOpenSearchSource;
    private GetOpenSearchConfigurations getOpenSearchConfigs;

    private SaveOpenSearchConfiguration saveOpenSearchConfigs;
    private DeleteOpenSearchConfiguration deleteOpenSearchConfig;

    public OpenSearchFieldProvider(ConfiguratorFactory configuratorFactory) {
        super(ID, TYPE_NAME, DESCRIPTION);
        discoverOpenSearchSource = new DiscoverOpenSearchSource();
        getOpenSearchConfigs = new GetOpenSearchConfigurations(configuratorFactory);

        saveOpenSearchConfigs = new SaveOpenSearchConfiguration(configuratorFactory);
        deleteOpenSearchConfig = new DeleteOpenSearchConfiguration(configuratorFactory);
        updateInnerFieldPaths();
    }

    @Override
    public List<Field> getDiscoveryFields() {
        return ImmutableList.of(discoverOpenSearchSource, getOpenSearchConfigs);
    }

    @Override
    public List<FunctionField> getMutationFunctions() {
        return ImmutableList.of(saveOpenSearchConfigs, deleteOpenSearchConfig);
    }
}
