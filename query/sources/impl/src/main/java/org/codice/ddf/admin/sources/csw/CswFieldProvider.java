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
package org.codice.ddf.admin.sources.csw;

import com.google.common.collect.ImmutableList;
import java.util.List;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.common.fields.base.function.BaseFieldProvider;
import org.codice.ddf.admin.sources.csw.discover.DiscoverCswSource;
import org.codice.ddf.admin.sources.csw.discover.GetCswConfigurations;
import org.codice.ddf.admin.sources.csw.persist.CreateCswConfiguration;
import org.codice.ddf.admin.sources.csw.persist.DeleteCswConfiguration;
import org.codice.ddf.admin.sources.csw.persist.UpdateCswConfiguration;

public class CswFieldProvider extends BaseFieldProvider {

  private static final String ID = "csw";

  private static final String TYPE_NAME = "Csw";

  private static final String DESCRIPTION =
      "Catalog Service for the Web - a standard used to expose geospatial data over the web. This is a source "
          + "that implements the CSW specification and provides methods for discovering and persisting CSW sources.";

  private GetCswConfigurations getCswConfigurations;

  private DiscoverCswSource discoverCswSource;

  private CreateCswConfiguration createCswConfiguration;

  private UpdateCswConfiguration updateCswConfiguration;

  private DeleteCswConfiguration deleteCswConfiguration;

  public CswFieldProvider() {
    super(ID, TYPE_NAME, DESCRIPTION);
  }

  @Override
  public List<FunctionField> getDiscoveryFunctions() {
    return ImmutableList.of(getCswConfigurations, discoverCswSource);
  }

  @Override
  public List<FunctionField> getMutationFunctions() {
    return ImmutableList.of(createCswConfiguration, updateCswConfiguration, deleteCswConfiguration);
  }

  public void setGetCswConfigurations(GetCswConfigurations getCswConfigurations) {
    this.getCswConfigurations = getCswConfigurations;
  }

  public void setCreateCswConfiguration(CreateCswConfiguration createCswConfiguration) {
    this.createCswConfiguration = createCswConfiguration;
  }

  public void setUpdateCswConfiguration(UpdateCswConfiguration updateCswConfiguration) {
    this.updateCswConfiguration = updateCswConfiguration;
  }

  public void setDeleteCswConfiguration(DeleteCswConfiguration deleteCswConfiguration) {
    this.deleteCswConfiguration = deleteCswConfiguration;
  }

  public void setDiscoverCswSource(DiscoverCswSource discoverCswSource) {
    this.discoverCswSource = discoverCswSource;
  }
}
