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

public class AdminQueryFeatures {

  public static final FeatureRepo FEATURE_COORDINATES =
      new FeatureRepoImpl(
          maven()
              .groupId("ddf.features")
              .artifactId("admin-query")
              .type("xml")
              .classifier("features")
              .version(DependencyVersionResolver.resolver()));

  public static final String GRAPHQL_FEATURE = "admin-query-graphql";

  public static final String ADMIN_UTILS_FEATURE = "admin-query-utils";

  public static final String ADMIN_SECURITY_FEATURE = "admin-query-security";

  public static final String ADMIN_CORE_API = "admin-query-core";

  public static final String ADMIN_QUERY_FEDERATION = "admin-query-federation";

  public static final String ADMIN_QUERY_ALL = "admin-query-all";

  public static final String ADMIN_QUERY_STS = "admin-query-sts";

  public static final String ADMIN_QUERY_WCPM = "admin-query-wcpm";

  public static final String ADMIN_QUERY_LDAP = "admin-query-ldap";

  public static final String ADMIN_QUERY_SOURCES = "admin-query-sources";

  public static final String ADMIN_QUERY_EMBEDDED_LDAP = "admin-query-embeddedldap";

  private AdminQueryFeatures() {}

  public static FeatureRepo featureRepo() {
    return FEATURE_COORDINATES;
  }

  public static Feature adminQuerySts() {
    return new FeatureImpl(FEATURE_COORDINATES.getFeatureFileUrl(), ADMIN_QUERY_STS);
  }

  public static Feature adminQueryWcpm() {
    return new FeatureImpl(FEATURE_COORDINATES.getFeatureFileUrl(), ADMIN_QUERY_WCPM);
  }

  public static Feature adminQueryLdap() {
    return new FeatureImpl(FEATURE_COORDINATES.getFeatureFileUrl(), ADMIN_QUERY_LDAP);
  }

  public static Feature adminQueryEmbeddedLdapq() {
    return new FeatureImpl(FEATURE_COORDINATES.getFeatureFileUrl(), ADMIN_QUERY_EMBEDDED_LDAP);
  }

  public static Feature adminQuerySources() {
    return new FeatureImpl(FEATURE_COORDINATES.getFeatureFileUrl(), ADMIN_QUERY_SOURCES);
  }

  public static Feature adminQueryAll() {
    return new FeatureImpl(FEATURE_COORDINATES.getFeatureFileUrl(), ADMIN_QUERY_ALL);
  }
}
