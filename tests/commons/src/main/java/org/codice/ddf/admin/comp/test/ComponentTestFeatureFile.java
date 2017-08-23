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
package org.codice.ddf.admin.comp.test;

import static org.ops4j.pax.exam.CoreOptions.maven;

import org.ops4j.pax.exam.options.MavenArtifactUrlReference;

public class ComponentTestFeatureFile extends Feature {

    private static final String SECURITY_ALL = "security-all-fix";

    public static final String THIRDPARTY_FEATURE = "thirdparty";

    public static final String COMMON_TEST_DEPENDENCIES_FEATURE = "common-test-dependencies";

    public static final String CONFIGURATOR = "admin-configurator-impl-dependencies";

    public static final String CONFIG_SECURITY_POLICY = "admin-core-configpolicy-dependencies";

    public static final String LDAP_CLAIMS_HANDLER = "security-sts-ldapclaimshandler-dependencies";

    public static final String LDAP_LOGIN = "security-sts-ldaplogin-dependencies";

    public static final String CATALOG_CORE_API = "catalog-core-api-dependencies";

    public static final String SPATIAL_CSW_SOURCE = "spatial-csw-source-dependencies";

    public static final String SPATIAL_WFS_V2_0_0_SOURCE = "spatial-wfs-v2_0_0-source-dependencies";

    public static final String CATALOG_OPENSEARCH_SOURCE = "catalog-opensearch-source-dependencies";

    public static final String SPATIAL_WFS_V1_0_0_SOURCE = "spatial-wfs-v1_0_0-source-dependencies";

    protected ComponentTestFeatureFile(String featureName) {
        super(featureName);
    }

    public static Feature ldapLoginFeature() {
        return new ComponentTestFeatureFile(LDAP_LOGIN);
    }

    public static Feature ldapClaimsHandlerFeature() {
        return new ComponentTestFeatureFile(LDAP_CLAIMS_HANDLER);
    }

    public static Feature configuratorFeature() {
        return new ComponentTestFeatureFile(CONFIGURATOR);
    }

    public static Feature configSecurityPolicy() {
        return new ComponentTestFeatureFile(CONFIG_SECURITY_POLICY);
    }

    public static Feature thirdPartyFeature() {
        return new ComponentTestFeatureFile(THIRDPARTY_FEATURE);
    }

    public static Feature securityAll() {
        return new ComponentTestFeatureFile(SECURITY_ALL);
    }

    public static Feature commonTestDependenciesFeature() {
        return new ComponentTestFeatureFile(COMMON_TEST_DEPENDENCIES_FEATURE);
    }

    public static Feature catalogCoreApiFeature() {
        return new ComponentTestFeatureFile(CATALOG_CORE_API);
    }

    public static Feature spatialCswSource() {
        return new ComponentTestFeatureFile(SPATIAL_CSW_SOURCE);
    }

    public static Feature spatialWfs20Source() {
        return new ComponentTestFeatureFile(SPATIAL_WFS_V2_0_0_SOURCE);
    }

    public static Feature spatialWfs10Source() {
        return new ComponentTestFeatureFile(SPATIAL_WFS_V1_0_0_SOURCE);
    }

    public static Feature catalogOpenSearchSource() {
        return new ComponentTestFeatureFile(CATALOG_OPENSEARCH_SOURCE);
    }

    @Override
    public MavenArtifactUrlReference getUrl() {
        return maven().groupId(
                "org.codice.ddf.admin.query")
                .artifactId("commons")
                .versionAsInProject()
                .type("xml")
                .classifier("features");
    }
}
