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
package org.codice.ddf.admin.sources.wfs;

import java.util.List;

import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.common.fields.base.function.BaseFieldProvider;
import org.codice.ddf.admin.configurator.ConfiguratorFactory;
import org.codice.ddf.admin.sources.wfs.discover.DiscoverWfsSource;
import org.codice.ddf.admin.sources.wfs.discover.GetWfsConfigurations;
import org.codice.ddf.admin.sources.wfs.persist.DeleteWfsConfiguration;
import org.codice.ddf.admin.sources.wfs.persist.SaveWfsConfiguration;

import com.google.common.collect.ImmutableList;

public class WfsFieldProvider extends BaseFieldProvider {

    private static final String NAME = "wfs";

    private static final String TYPE_NAME = "Wfs";

    private static final String DESCRIPTION =
            "Web Feature Service - an Open Geospatial Consortium (OGC) standard to requesting geographical features across the web. This is a source "
                    + "that implements the WFS specification and provides methods for discovering and persisting WFS sources.";

    private ConfiguratorFactory configuratorFactory;

    public WfsFieldProvider() {
        super(NAME, TYPE_NAME, DESCRIPTION);
    }

    @Override
    public List<Field> getDiscoveryFields() {
        return ImmutableList.of(new DiscoverWfsSource(),
                new GetWfsConfigurations(configuratorFactory));
    }

    @Override
    public List<FunctionField> getMutationFunctions() {
        return ImmutableList.of(new SaveWfsConfiguration(configuratorFactory),
                new DeleteWfsConfiguration(configuratorFactory));
    }

    public void setConfiguratorFactory(ConfiguratorFactory configuratorFactory) {
        this.configuratorFactory = configuratorFactory;
    }
}
