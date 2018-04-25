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
package org.codice.ddf.admin.query.dev.system;

import com.google.common.collect.ImmutableList;
import java.util.List;
import org.apache.karaf.bundle.core.BundleService;
import org.apache.karaf.features.FeaturesService;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.common.fields.base.function.BaseFieldProvider;
import org.codice.ddf.admin.query.dev.system.dependency.BundleUtils;
import org.codice.ddf.admin.query.dev.system.dependency.FeatureUtils;
import org.codice.ddf.admin.query.dev.system.discover.GetBundles;
import org.codice.ddf.admin.query.dev.system.discover.GetFeatures;
import org.codice.ddf.admin.query.dev.system.persist.CreateFeatureDependencyGraph;
import org.codice.ddf.admin.query.dev.system.persist.CreatePackageDependencyGraph;
import org.codice.ddf.admin.query.dev.system.persist.CreateServiceDependencyGraph;
import org.codice.ddf.admin.query.dev.system.persist.FeatureStatistics;

public class DeveloperToolsFieldProvider extends BaseFieldProvider {

  public static final String FIELD_NAME = "dev";

  public static final String FIELD_TYPE_NAME = "DeveloperTools";

  public static final String DESCRIPTION =
      "Contains useful tools for developing and debugging applications.";

  private GetBundles getBundles;
  private GetFeatures getFeatures;
  private CreateServiceDependencyGraph createServiceDepGraph;
  private CreatePackageDependencyGraph createPkgDepsGraph;
  private CreateFeatureDependencyGraph createFeatureDepsGraph;
  private FeatureStatistics dumpFeatureStatistics;

  public DeveloperToolsFieldProvider(BundleService bundleService, FeaturesService featuresService) {
    super(FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
    BundleUtils bundleUtils = new BundleUtils(bundleService);
    FeatureUtils featureUtils = new FeatureUtils(bundleUtils, featuresService);

    getBundles = new GetBundles(bundleUtils);
    getFeatures = new GetFeatures(featureUtils);
    createServiceDepGraph = new CreateServiceDependencyGraph(bundleUtils);
    createPkgDepsGraph = new CreatePackageDependencyGraph(bundleUtils);
    createFeatureDepsGraph = new CreateFeatureDependencyGraph(featureUtils);
    dumpFeatureStatistics = new FeatureStatistics(featuresService, bundleService);
  }

  @Override
  public List<FunctionField> getDiscoveryFunctions() {
    return ImmutableList.of(getBundles, getFeatures);
  }

  @Override
  public List<FunctionField> getMutationFunctions() {
    return ImmutableList.of(
        createServiceDepGraph, createPkgDepsGraph, createFeatureDepsGraph, dumpFeatureStatistics);
  }
}
