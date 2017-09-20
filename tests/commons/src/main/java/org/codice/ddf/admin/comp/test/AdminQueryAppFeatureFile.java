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
package org.codice.ddf.admin.comp.test;

import static org.ops4j.pax.exam.CoreOptions.maven;

import org.ops4j.pax.exam.options.MavenArtifactUrlReference;

public class AdminQueryAppFeatureFile extends Feature {

  public static final String GRAPHQL_FEATURE = "admin-query-graphql";

  public static final String ADMIN_UTILS_FEATURE = "admin-query-utils";

  public static final String ADMIN_WCPM_FEATURE = "admin-query-wcpm";

  public static final String ADMIN_SECURITY_FEATURE = "admin-query-security";

  public static final String ADMIN_CORE_API = "admin-query-core";

  public static final String ADMIN_QUERY_FEDERATION = "admin-query-federation";

  protected AdminQueryAppFeatureFile(String featureName) {
    super(featureName);
  }

  public static Feature adminCoreFeature() {
    return new AdminQueryAppFeatureFile(ADMIN_CORE_API);
  }

  public static Feature adminGraphQlFeature() {
    return new AdminQueryAppFeatureFile(GRAPHQL_FEATURE);
  }

  public static Feature adminSecurityFeature() {
    return new AdminQueryAppFeatureFile(ADMIN_SECURITY_FEATURE);
  }

  public static Feature adminSourceFeature() {
    return new AdminQueryAppFeatureFile(ADMIN_QUERY_FEDERATION);
  }

  public static Feature adminUtilsFeature() {
    return new AdminQueryAppFeatureFile(ADMIN_UTILS_FEATURE);
  }

  public Feature adminWcpmFeature() {
    return new AdminQueryAppFeatureFile(ADMIN_WCPM_FEATURE);
  }

  @Override
  public MavenArtifactUrlReference getUrl() {
    return maven()
        .groupId("org.codice.ddf.admin.query")
        .artifactId("admin-query-app")
        .type("xml")
        .classifier("features")
        .versionAsInProject();
  }
}
