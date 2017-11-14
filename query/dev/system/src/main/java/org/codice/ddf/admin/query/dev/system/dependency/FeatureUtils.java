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
package org.codice.ddf.admin.query.dev.system.dependency;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.karaf.features.BundleInfo;
import org.apache.karaf.features.Dependency;
import org.apache.karaf.features.Feature;
import org.apache.karaf.features.FeaturesService;
import org.codice.ddf.admin.query.dev.system.fields.BundleField;
import org.codice.ddf.admin.query.dev.system.fields.FeatureField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FeatureUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(FeatureUtils.class);

  private FeaturesService featuresService;
  private BundleUtils bundleUtils;

  public FeatureUtils(BundleUtils bundleUtils, FeaturesService featuresService) {
    this.bundleUtils = bundleUtils;
    this.featuresService = featuresService;
  }

  public List<FeatureField> getAllFeatures() {
    Map<String, BundleField> allBundlesByLocation =
        bundleUtils
            .getAllBundleFields()
            .stream()
            .collect(Collectors.toMap(BundleField::location, bundle -> bundle));

    try {
      return Arrays.stream(featuresService.listFeatures())
          .map(f -> featureToField(f, allBundlesByLocation))
          .collect(Collectors.toList());
    } catch (Exception e) {
      LOGGER.warn("Failed to retrieve features from feature service.");
      return Collections.emptyList();
    }
  }

  private FeatureField featureToField(Feature feat, Map<String, BundleField> bundlesByLocation) {
    List<String> featDeps =
        feat.getDependencies().stream().map(Dependency::getName).collect(Collectors.toList());

    List<BundleField> bundleDepLocations =
        feat.getBundles()
            .stream()
            .map(BundleInfo::getLocation)
            .map(loc -> createBundleFromLocation(loc, bundlesByLocation))
            .collect(Collectors.toList());

    return new FeatureField()
        .name(feat.getName())
        .featDescription(feat.getDescription())
        .state(feat.getInstall())
        .id(feat.getId())
        .repoUrl(feat.getRepositoryUrl())
        .addFeatureDeps(featDeps)
        .addBundleDeps(bundleDepLocations);
  }

  private BundleField createBundleFromLocation(
      String location, Map<String, BundleField> bundlesByLocation) {
    // If the bundle isn't started, we won't be able to match by location so create a new one
    return bundlesByLocation.containsKey(location)
        ? bundlesByLocation.get(location)
        : new BundleField().location(location);
  }

  public Feature getFeature(String featureName) throws Exception {
    return featuresService.getFeature(featureName);
  }
}
