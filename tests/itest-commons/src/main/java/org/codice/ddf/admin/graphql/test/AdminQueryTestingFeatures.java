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
package org.codice.ddf.admin.graphql.test;

import static org.ops4j.pax.exam.CoreOptions.maven;

import org.codice.ddf.test.common.DependencyVersionResolver;
import org.codice.ddf.test.common.features.Feature;
import org.codice.ddf.test.common.features.FeatureImpl;
import org.codice.ddf.test.common.features.FeatureRepo;
import org.codice.ddf.test.common.features.FeatureRepoImpl;

public class AdminQueryTestingFeatures {

  public static final FeatureRepo FEATURE_COORDINATES =
      new FeatureRepoImpl(
          maven()
              .groupId("ddf.features")
              .artifactId("admin-query-testing")
              .type("xml")
              .classifier("features")
              .version(DependencyVersionResolver.resolver()));

  public static final String TEST_COMMONS = "admin-query-itest-commons";

  private AdminQueryTestingFeatures() {}

  public static FeatureRepo featureRepo() {
    return FEATURE_COORDINATES;
  }

  public static Feature itestCommons() {
    return new FeatureImpl(FEATURE_COORDINATES.getFeatureFileUrl(), TEST_COMMONS);
  }
}
