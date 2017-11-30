/**
 * Copyright (c) Codice Foundation
 *
 * <p>This is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version.
 *
 * <p>This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details. A copy of the GNU Lesser General Public
 * License is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 */
package org.codice.ddf.admin.sources.wfs;

import com.google.common.collect.ImmutableList;
import java.util.List;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.common.fields.base.function.BaseFieldProvider;
import org.codice.ddf.admin.sources.wfs.discover.DiscoverWfsSource;
import org.codice.ddf.admin.sources.wfs.discover.GetWfsConfigurations;
import org.codice.ddf.admin.sources.wfs.persist.CreateWfsConfiguration;
import org.codice.ddf.admin.sources.wfs.persist.DeleteWfsConfiguration;
import org.codice.ddf.admin.sources.wfs.persist.UpdateWfsConfiguration;
import org.codice.ddf.internal.admin.configurator.actions.ConfiguratorSuite;

public class WfsFieldProvider extends BaseFieldProvider {

  private static final String NAME = "wfs";

  private static final String TYPE_NAME = "Wfs";

  private static final String DESCRIPTION =
      "Web Feature Service - an Open Geospatial Consortium (OGC) standard to requesting geographical features across the web. This is a source "
          + "that implements the WFS specification and provides methods for discovering and persisting WFS sources.";

  private DiscoverWfsSource discoverWfsSource;

  private GetWfsConfigurations getWfsConfigs;

  private CreateWfsConfiguration createWfsConfig;

  private UpdateWfsConfiguration updateWfsConfig;

  private DeleteWfsConfiguration deleteWfsConfig;

  public WfsFieldProvider(ConfiguratorSuite configuratorSuite) {
    super(NAME, TYPE_NAME, DESCRIPTION);
    discoverWfsSource = new DiscoverWfsSource(configuratorSuite);
    getWfsConfigs = new GetWfsConfigurations(configuratorSuite);
    createWfsConfig = new CreateWfsConfiguration(configuratorSuite);
    updateWfsConfig = new UpdateWfsConfiguration(configuratorSuite);
    deleteWfsConfig = new DeleteWfsConfiguration(configuratorSuite);
  }

  @Override
  public List<FunctionField> getDiscoveryFunctions() {
    return ImmutableList.of(discoverWfsSource, getWfsConfigs);
  }

  @Override
  public List<FunctionField> getMutationFunctions() {
    return ImmutableList.of(createWfsConfig, updateWfsConfig, deleteWfsConfig);
  }
}
