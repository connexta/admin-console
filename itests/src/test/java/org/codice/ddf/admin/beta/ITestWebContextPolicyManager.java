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
package org.codice.ddf.admin.beta;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.features;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.karafDistributionConfiguration;
import static com.jayway.restassured.RestAssured.given;

import java.io.File;
import java.util.Map;

import org.codice.ddf.itests.common.AbstractIntegrationTest;
import org.codice.ddf.itests.common.annotations.BeforeExam;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class ITestWebContextPolicyManager extends AbstractIntegrationTest {

    public static final DynamicUrl GRAPHQL_PATH =
            new DynamicUrl(new DynamicUrl(DynamicUrl.SECURE_ROOT, HTTPS_PORT),
                    "/admin/beta/graphql");

    // TODO: tbatie - 6/5/17 - Remove once merged into ddf, Need to override so it looks at this artifacts pom instead of test-itests-common's pom
    @Override
    protected Option[] configureDistribution() {
        return options(karafDistributionConfiguration(maven().groupId("org.codice.ddf")
                .artifactId("ddf")
                .type("zip")
                .versionAsInProject()
                .getURL(), "ddf", KARAF_VERSION).unpackDirectory(new File("target/exam"))
                .useDeployFolder(false));
    }

    // TODO: tbatie - 6/5/17 - Remove once merged into ddf, admin beta should be a boot feature
    @Override
    protected Option[] configureStartScript() {
        return combineOptions(super.configureStartScript(), options(features(maven().groupId(
                "org.codice.ddf.admin.beta")
                .artifactId("admin-query-app")
                .type("xml")
                .classifier("features")
                .versionAsInProject(), "admin-beta-query-app")));
    }

    @BeforeExam
    public void beforeExam() throws Exception {
        try {
            waitForSystemReady();
            // TODO: tbatie - 6/5/17 - Not sure how to send requests with IDP enabled in itests so turned basic on
            getSecurityPolicy().configureRestForGuest();

        } catch (Exception e) {
            LOGGER.error("Failed in @BeforeExam: ", e);
            fail("Failed in @BeforeExam: " + e.getMessage());
        }
    }

    @Test
    public void retrieveGraphQLSchema() {
        Map<String, Object> schema = given().auth()
                .basic("admin", "admin")
                .when()
                .get(GRAPHQL_PATH.getUrl() + "/schema.json")
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .jsonPath()
                .get("data.__schema");

        assertNotNull(schema);
        assertFalse(schema.isEmpty());
    }

    @Test
    public void retrieveAuthTypes() {

        // TODO: tbatie - 6/5/17 - Pull query from resource file and use the rest assured params instead
        Map<String, Object> wcpmData = given().auth()
                .basic("admin", "admin")
                .when()
                .body("{\n"
                        + "    \"query\": \"query GetAuthTypes {\\n  wcpm {\\n    authTypes,\\n    realms,\\n    whitelisted,\\n    policies {\\n      paths\\n      authTypes\\n      realm\\n      claimsMapping {\\n        key\\n        value\\n      }\\n    }\\n  }\\n}\"\n"
                        + "}")
                .post(GRAPHQL_PATH.getUrl())
                .then()
                .extract()
                .jsonPath()
                .get("data.wcpm");

        assertNotNull(wcpmData);
        assertFalse(wcpmData.isEmpty());
        // TODO: tbatie - 6/5/17 - Check the map for the expected values
    }
}
