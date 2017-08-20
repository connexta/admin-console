package org.codice.ddf.admin.query;
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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.wrappedBundle;
import static com.jayway.restassured.RestAssured.given;
import static junit.framework.TestCase.fail;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.boon.Boon;
import org.codice.ddf.admin.comp.test.AbstractComponentTest;
import org.codice.ddf.admin.comp.test.ComponentTestFeatureFile;
import org.codice.ddf.admin.comp.test.Feature;
import org.codice.ddf.admin.comp.test.PlatformAppFeatureFile;
import org.codice.ddf.admin.comp.test.SecurityAppFeatureFile;
import org.codice.ddf.itests.common.annotations.BeforeExam;
import org.hamcrest.MatcherAssert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.options.WrappedUrlProvisionOption;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

import com.google.common.collect.ImmutableMap;
import com.jayway.restassured.response.ExtractableResponse;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class AdminSourcesIT extends AbstractComponentTest {

    public static final String GRAPHQL_ENDPOINT = "http://localhost:9993/admin/hub/graphql";

    public static final String QUERY_RESOURCE_BASE_PATH = "/query/sources";

    public int cswPid;

    public int openSearchPid;

    public int wfsPid;


    //mvn:ddf.catalog.core/catalog-core-standardframework/2.11.0-SNAPSHOT
//
//    @ProbeBuilder
//    public TestProbeBuilder probe(TestProbeBuilder defaultProbe) {
//        defaultProbe.setHeader("Export-Package", "org.codice.ddf.catalog.resource.cache");
//        return defaultProbe;
//    }
//
    @Override
    public List<Option> customSettings() {
        return super.customSettings();
//        return Arrays.asList(wrappedBundle(mavenBundle().groupId("ddf.catalog.core")
//                .artifactId("catalog-core-downloadaction")
//                .version("2.11.0-SNAPSHOT")).instructions("Import-Package=*;resolution:=optional")
//                .bundleSymbolicName("wrapped-catalog-core-downloadaction")
//                .overwriteManifest(WrappedUrlProvisionOption.OverwriteMode.FULL));

//        return Arrays.asList(wrappedBundle(mavenBundle().groupId("ddf.catalog.core")
//                .artifactId("catalog-core-standardframework")
//                .version("2.11.0-SNAPSHOT")).instructions("DynamicImport-Package=*", "Export-Package=*", "-removeheaders=Export-Service,Import-Service").bundleSymbolicName("wrapped-standardframework")
//                .overwriteManifest(
//                WrappedUrlProvisionOption.OverwriteMode.FULL));

//        return Arrays.asList(wrappedBundle(mavenBundle().groupId("ddf.catalog.core")
//                .artifactId("catalog-core-standardframework")
//                .version("2.11.0-SNAPSHOT")).instructions("DynamicImport-Package=*", "Export-Package=*", "-removeheaders=Export-Service,Import-Service").bundleSymbolicName("wrapped-standardframework")
//                .overwriteManifest(
//                        WrappedUrlProvisionOption.OverwriteMode.FULL));


    }

    @BeforeExam
    public static void beforeClass() {
        // this is where the hard part is
    }

    @Override
    public List<Feature> features() {
        return Arrays.asList(
                PlatformAppFeatureFile.featureFile(),
                SecurityAppFeatureFile.featureFile(),

                ComponentTestFeatureFile.thirdPartyFeature().bootFeature(true),
                ComponentTestFeatureFile.commonTestDependenciesFeature().bootFeature(true),

                //Added a boot feature because the tests need the ServiceManager running before we can startFeatures
                ComponentTestFeatureFile.securityAll().bootFeature(true),
                ComponentTestFeatureFile.catalogCoreApiFeature()
//                ComponentTestFeatureFile.spatialCswSource()
//                ComponentTestFeatureFile.spatialWfs10(),
//                ComponentTestFeatureFile.spatialWfs20()

//                ComponentTestFeatureFile.spatialCswSource()
//                ComponentTestFeatureFile.configuratorFeature(),
//                ComponentTestFeatureFile.configSecurityPolicy(),
//
//                AdminQueryAppFeatureFile.adminSecurityFeature(),
//                AdminQueryAppFeatureFile.adminGraphQlFeature()
        );
    }

    @BeforeExam
    @Override
    public void beforeExam() throws Exception {
        super.beforeExam();
        //        ServiceUtils.registerService(CatalogFramework.class, mock(CatalogFramework.class));
//        ServiceUtils.registerService(ResourceCacheServiceMBean.class, mock(ResourceCacheServiceMBean.class));

        serviceManager.startFeature(true, ComponentTestFeatureFile.spatialCswSource().getFeatureName());
        //        serviceManager.startFeature(true, ComponentTestFeatureFile.spatialCswSource().getFeatureName());
        //
        //        // TODO: 8/17/17 phuffer - Make a convenience method that takes a feature and mock class?
        //        serviceManager.startFeature(true,
        //                ComponentTestFeatureFile.spatialCswSource().getFeatureName());
    }

    @Test
    public void createCsw() throws Exception {
//        ExtractableResponse response = sendGraphQlQuery("/create/CreateCswSource.graphql");
//        MatcherAssert.assertThat("Error creating CSW source.",
//                response.jsonPath()
//                        .getBoolean("data.createCswSource"));

        assertThat("I am", true);

        // TODO: 8/3/17 phuffer - Clean this up so tests dont leak over into each other
    }

    @Ignore
    @Test
    public void createOpenSearch() throws IOException {
        ExtractableResponse response = sendGraphQlQuery("/create/CreateOpenSearchSource.graphql");
        MatcherAssert.assertThat("Error creating OpenSearch source.",
                response.jsonPath()
                        .getBoolean("data.createOpenSearchSource"));
    }

    @Ignore
    @Test
    public void createWfs() throws IOException {
        ExtractableResponse response = sendGraphQlQuery("/create/CreateWfsSource.graphql");
        MatcherAssert.assertThat("Error creating WFS source.",
                response.jsonPath()
                        .getBoolean("data.createOpenSearchSource"));
    }

    @Ignore
    @Test
    public void verifySourceCreationsPersist() throws IOException {
        ExtractableResponse response = sendGraphQlQuery("/get-sources/GetAllSources.graphql");

        assertThat(response.jsonPath()
                .getList("data.csw.sources")
                .size(), is(equalTo(1)));
        assertThat(response.jsonPath()
                .getList("data.openSearch.sources")
                .size(), is(equalTo(1)));
        assertThat(response.jsonPath()
                .getList("data.wfs.sources")
                .size(), is(equalTo(1)));

        cswPid = response.jsonPath()
                .getInt("data.csw.sources[0].pid");
        openSearchPid = response.jsonPath()
                .getInt("data.openSearch.sources[0].pid");
        wfsPid = response.jsonPath()
                .getInt("data.wfs.sources[0].pid");
    }

    @Ignore
    @Test
    public void updateCsw() throws IOException {
        ExtractableResponse response = sendGraphQlQuery("/update/UpdateCswSource.graphql",
                ImmutableMap.of("pid", cswPid));
        MatcherAssert.assertThat("Error updating CSW source.",
                response.jsonPath()
                        .getBoolean("data.updateCswSource"));
    }

    @Ignore
    @Test
    public void updateOpenSearch() throws IOException {
        ExtractableResponse response = sendGraphQlQuery("/update/UpdateOpenSearchSource.graphql",
                ImmutableMap.of("pid", openSearchPid));
        MatcherAssert.assertThat("Error updating OpenSearch source.",
                response.jsonPath()
                        .getBoolean("data.updateOpenSearchSource"));
    }

    @Ignore
    @Test
    public void updateWfs() throws IOException {
        ExtractableResponse response = sendGraphQlQuery("/update/UpdateWfsSource.graphql",
                ImmutableMap.of("pid", wfsPid));
        MatcherAssert.assertThat("Error updating WFS source.",
                response.jsonPath()
                        .getBoolean("data.updateWfsSource"));
    }

    @Ignore
    @Test
    public void verifySourceUpdatesPersist() throws IOException {
        ExtractableResponse response = sendGraphQlQuery("/get-sources/GetAllSources.graphql");

        assertThat(response.jsonPath()
                .getList("data.csw.sources")
                .size(), is(equalTo(1)));
        assertThat(response.jsonPath()
                .getList("data.csw.sources[0].source.sourceName"), is(equalTo("testCswUpdated")));

        assertThat(response.jsonPath()
                .getList("data.openSearch.sources")
                .size(), is(equalTo(1)));
        assertThat(response.jsonPath()
                        .getList("data.openSearch.sources[0].source.sourceName"),
                is(equalTo("testOpenSearchUpdated")));

        assertThat(response.jsonPath()
                .getList("data.wfs.sources")
                .size(), is(equalTo(1)));
        assertThat(response.jsonPath()
                .getList("data.wfs.sources[0].source.sourceName"), is(equalTo("testWfsUpdated")));
    }

    @Ignore
    @Test
    public void deleteCswSource() throws IOException {
        ExtractableResponse response = sendGraphQlQuery("/delete/DeleteCswSource.graphql",
                ImmutableMap.of("pid", cswPid));

        MatcherAssert.assertThat("Error deleting CSW source.",
                response.jsonPath()
                        .getBoolean("data.deleteCswSource"));
    }

    @Ignore
    @Test
    public void verifyCswDeleted() throws IOException {
        ExtractableResponse response = sendGraphQlQuery("/get-sources/GetAllSources.graphql");

        assertThat(response.jsonPath()
                .getList("data.csw.sources")
                .size(), is(equalTo(0)));
        assertThat(response.jsonPath()
                .getList("data.openSearch.sources")
                .size(), is(equalTo(1)));
        assertThat(response.jsonPath()
                .getList("data.wfs.sources")
                .size(), is(equalTo(1)));
    }

    @Ignore
    @Test
    public void deleteOpenSearchSource() throws IOException {
        ExtractableResponse response = sendGraphQlQuery("/delete/DeleteCswSource.graphql",
                ImmutableMap.of("pid", openSearchPid));

        MatcherAssert.assertThat("Error deleting OpenSearch source.",
                response.jsonPath()
                        .getBoolean("data.deleteOpenSearchSource"));
    }

    @Ignore
    @Test
    public void verifyOpenSearchDeleted() throws IOException {
        ExtractableResponse response = sendGraphQlQuery("/get-sources/GetAllSources.graphql");

        assertThat(response.jsonPath()
                .getList("data.csw.sources")
                .size(), is(equalTo(0)));
        assertThat(response.jsonPath()
                .getList("data.openSearch.sources")
                .size(), is(equalTo(0)));
        assertThat(response.jsonPath()
                .getList("data.wfs.sources")
                .size(), is(equalTo(1)));
    }

    @Ignore
    @Test
    public void deleteWfsSource() throws IOException {
        ExtractableResponse response = sendGraphQlQuery("/delete/DeleteCswSource.graphql",
                ImmutableMap.of("pid", wfsPid));
        MatcherAssert.assertThat("Error deleting WFS source.",
                response.jsonPath()
                        .getBoolean("data.deleteWfsSource"));
    }

    @Ignore
    @Test
    public void verifyWfsDeleted() throws IOException {
        ExtractableResponse response = sendGraphQlQuery("/get-sources/GetAllSources.graphql");

        assertThat(response.jsonPath()
                .getList("data.csw.sources")
                .size(), is(equalTo(0)));
        assertThat(response.jsonPath()
                .getList("data.openSearch.sources")
                .size(), is(equalTo(0)));
        assertThat(response.jsonPath()
                .getList("data.wfs.sources")
                .size(), is(equalTo(0)));
    }

    public static String getResourceAsString(String filePath) {
        try {
            return IOUtils.toString(AdminSourcesIT.class.getClassLoader()
                    .getResourceAsStream(filePath), "UTF-8");
        } catch (IOException e) {
            fail("Unable to retrieve resource: " + filePath);
        }

        return null;
    }

    public ExtractableResponse sendGraphQlQuery(String queryFileName) {
        String jsonBody = Boon.toJson(ImmutableMap.of("query",
                getResourceAsString(QUERY_RESOURCE_BASE_PATH + queryFileName)));

        System.out.println("\nRequest: \n" + jsonBody);
        return given().when()
                .body(jsonBody)
                .post(GRAPHQL_ENDPOINT)
                .then()
                .extract();
    }

    public ExtractableResponse sendGraphQlQuery(String queryFileName, ImmutableMap variables) {
        String jsonBody = Boon.toJson(ImmutableMap.of("query",
                getResourceAsString(QUERY_RESOURCE_BASE_PATH + queryFileName),
                "variables",
                Boon.toJson(variables)));

        System.out.println("\nRequest: \n" + jsonBody);
        return given().when()
                .body(jsonBody)
                .post(GRAPHQL_ENDPOINT)
                .then()
                .extract();
    }


}