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
package org.codice.ddf.admin.query;

import static junit.framework.TestCase.assertEquals;
import static org.codice.ddf.admin.query.request.SourcesRequestHelper.MASKED_PASSWORD;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.editConfigurationFilePut;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.codice.ddf.admin.common.fields.common.PidField;
import org.codice.ddf.admin.comp.test.AbstractComponentTest;
import org.codice.ddf.admin.comp.test.AdminQueryAppFeatureFile;
import org.codice.ddf.admin.comp.test.ComponentTestFeatureFile;
import org.codice.ddf.admin.comp.test.Feature;
import org.codice.ddf.admin.comp.test.PlatformAppFeatureFile;
import org.codice.ddf.admin.comp.test.SecurityAppFeatureFile;
import org.codice.ddf.admin.query.request.SourcesRequestHelper;
import org.codice.ddf.admin.sources.fields.CswProfile;
import org.codice.ddf.admin.sources.fields.CswSpatialOperator;
import org.codice.ddf.admin.sources.fields.WfsVersion;
import org.codice.ddf.admin.sources.fields.type.CswSourceConfigurationField;
import org.codice.ddf.admin.sources.fields.type.OpenSearchSourceConfigurationField;
import org.codice.ddf.admin.sources.fields.type.SourceConfigField;
import org.codice.ddf.admin.sources.fields.type.WfsSourceConfigurationField;
import org.codice.ddf.itests.common.annotations.BeforeExam;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class AdminSourcesIT extends AbstractComponentTest {

  // TODO: 8/20/17 phuffer - fix dynamic port once refactored.
  public static final String GRAPHQL_ENDPOINT = "https://localhost:9993/admin/hub/graphql";

  public static final String PID = PidField.DEFAULT_FIELD_NAME;

  public static final String WFS_NAME = WfsSourceConfigurationField.DEFAULT_FIELD_NAME;

  public static final String OPEN_SEARCH_NAME =
      OpenSearchSourceConfigurationField.DEFAULT_FIELD_NAME;

  public static final String CSW_NAME = CswSourceConfigurationField.DEFAULT_FIELD_NAME;

  private static final SourcesRequestHelper SOURCES_REQUEST_HELPER =
      new SourcesRequestHelper(GRAPHQL_ENDPOINT);

  public static final String TEST_SOURCE_NAME = "testSourceName";

  public static final String TEST_USERNAME = "testUsername";

  @Override
  public List<Feature> features() {
    return Arrays.asList(
        PlatformAppFeatureFile.featureFile(),
        SecurityAppFeatureFile.featureFile(),
        ComponentTestFeatureFile.thirdPartyFeature().bootFeature(),
        ComponentTestFeatureFile.commonTestDependenciesFeature().bootFeature(),
        AdminQueryAppFeatureFile.adminCoreFeature().bootFeature(),

        // Added a boot feature because the tests need the ServiceManager running before we can
        // startFeatures
        ComponentTestFeatureFile.securityAll().bootFeature(),
        ComponentTestFeatureFile.catalogCoreApiFeature().bootFeature(),
        ComponentTestFeatureFile.configuratorFeature(),
        ComponentTestFeatureFile.configSecurityPolicy(),
        ComponentTestFeatureFile.spatialWfs20Source(),
        ComponentTestFeatureFile.spatialWfs10Source(),
        ComponentTestFeatureFile.catalogOpenSearchSource(),
        // TODO: 8/23/17 phuffer - Feature requires a change in DDF to start correctly
        //                ComponentTestFeatureFile.spatialCswSource(),

        AdminQueryAppFeatureFile.adminUtilsFeature(),
        AdminQueryAppFeatureFile.adminSourceFeature(),
        AdminQueryAppFeatureFile.adminGraphQlFeature());
  }

  @Override
  public List<Option> customSettings() {
    // The catalog framework won't start until it is able to point a client to a solr instance over
    // http.
    // This configuration points the client to the embeddedSolr instead so fewer dependencies have
    // to be started up
    return Arrays.asList(
        editConfigurationFilePut("etc/system.properties", "solr.client", "EmbeddedSolrServer"),
        editConfigurationFilePut("etc/system.properties", "solr.http.url", ""),
        editConfigurationFilePut(
            "etc/system.properties", "solr.data.dir", "${karaf.home}/data/solr"),
        editConfigurationFilePut("etc/system.properties", "solr.cloud.zookeeper", ""));
  }

  @BeforeExam
  @Override
  public void beforeExam() throws Exception {
    super.beforeExam();
    securityPolicyConfigurator.configureRestForBasic();

    SOURCES_REQUEST_HELPER.waitForSourcesInSchema();
  }

  @Test
  public void testWfs20() {
    // create wfs source
    Map<String, Object> configToSave =
        createWfsArgs(TEST_SOURCE_NAME, null, WfsVersion.Wfs2.ENUM_TITLE).getValue();

    boolean createSuccess =
        SOURCES_REQUEST_HELPER.createSource(SourcesRequestHelper.SourceType.WFS, configToSave);
    assertThat("Error creating WFS source.", createSuccess, is(true));

    SOURCES_REQUEST_HELPER.waitForWfsSource(configToSave, true);

    // get created wfs source
    List<Map<String, Object>> wfsSources =
        SOURCES_REQUEST_HELPER.getSources(SourcesRequestHelper.SourceType.WFS);
    assertThat(wfsSources, is(not(empty())));

    Map<String, String> sourceProperties = (Map) wfsSources.get(0).get(WFS_NAME);
    String pid = sourceProperties.get(PID);
    assertThat("Error getting WFS source.", StringUtils.isNotEmpty(pid));

    // update wfs source
    Map<String, Object> updateArgs =
        createWfsArgs("updatedName", pid, WfsVersion.Wfs2.ENUM_TITLE).getValue();

    boolean updateSuccess =
        SOURCES_REQUEST_HELPER.updateSource(SourcesRequestHelper.SourceType.WFS, updateArgs);
    assertThat("Error updating WFS source", updateSuccess, is(true));

    SOURCES_REQUEST_HELPER.waitForWfsSource(updateArgs, false);

    // get updated wfs source
    wfsSources = SOURCES_REQUEST_HELPER.getSources(SourcesRequestHelper.SourceType.WFS);
    assertThat(wfsSources, is(not(empty())));

    sourceProperties = (Map) wfsSources.get(0).get(WFS_NAME);
    String updatedName = sourceProperties.get(SourceConfigField.SOURCE_NAME_FIELD_NAME);
    assertEquals("Error updating WFS source.", "updatedName", updatedName);

    // delete wfs source
    boolean deleteSuccess =
        SOURCES_REQUEST_HELPER.deleteSource(SourcesRequestHelper.SourceType.WFS, pid);
    assertThat("Error deleting WFS source.", deleteSuccess, is(true));
  }

  @Test
  public void testWfs10() {
    // create wfs source
    Map<String, Object> configToSave =
        createWfsArgs(TEST_SOURCE_NAME, null, WfsVersion.Wfs1.ENUM_TITLE).getValue();

    boolean createSuccess =
        SOURCES_REQUEST_HELPER.createSource(SourcesRequestHelper.SourceType.WFS, configToSave);
    assertThat("Error creating WFS source.", createSuccess, is(true));

    SOURCES_REQUEST_HELPER.waitForWfsSource(configToSave, true);

    // get created wfs source
    List<Map<String, Object>> wfsSources =
        SOURCES_REQUEST_HELPER.getSources(SourcesRequestHelper.SourceType.WFS);
    assertThat(wfsSources, is(not(empty())));

    Map<String, String> sourceProperties = (Map) wfsSources.get(0).get(WFS_NAME);
    String pid = sourceProperties.get(PID);
    assertThat("Error getting WFS source.", StringUtils.isNotEmpty(pid));

    // delete wfs source
    boolean deleteSuccess =
        SOURCES_REQUEST_HELPER.deleteSource(SourcesRequestHelper.SourceType.WFS, pid);
    assertThat("Error deleting WFS source.", deleteSuccess, is(true));
  }

  @Test
  public void testOpenSearch() {
    // create openSearch source
    Map<String, Object> configToSave = createOpenSearchArgs(TEST_SOURCE_NAME, null).getValue();

    boolean createSuccess =
        SOURCES_REQUEST_HELPER.createSource(
            SourcesRequestHelper.SourceType.OPEN_SEARCH, configToSave);
    assertThat("Error creating OpenSearch source.", createSuccess, is(true));

    SOURCES_REQUEST_HELPER.waitForOpenSearch(configToSave, true);

    // get created openSearch source
    List<Map<String, Object>> openSearchSources =
        SOURCES_REQUEST_HELPER.getSources(SourcesRequestHelper.SourceType.OPEN_SEARCH);
    assertThat(openSearchSources, is(not(empty())));

    Map<String, String> sourceProperties = (Map) openSearchSources.get(0).get(OPEN_SEARCH_NAME);
    String pid = sourceProperties.get(PID);
    assertThat("Error getting OpenSearch source.", StringUtils.isNotEmpty(pid));

    // update openSearch source
    Map<String, Object> updateArgs = createOpenSearchArgs("updatedName", pid).getValue();
    boolean updateSuccess =
        SOURCES_REQUEST_HELPER.updateSource(
            SourcesRequestHelper.SourceType.OPEN_SEARCH, updateArgs);
    assertThat("Error updating OpenSearch source", updateSuccess, is(true));

    SOURCES_REQUEST_HELPER.waitForOpenSearch(updateArgs, false);

    // get updated openSearch source
    openSearchSources =
        SOURCES_REQUEST_HELPER.getSources(SourcesRequestHelper.SourceType.OPEN_SEARCH);
    assertThat(openSearchSources, is(not(empty())));

    sourceProperties = (Map) openSearchSources.get(0).get(OPEN_SEARCH_NAME);
    String updatedName = sourceProperties.get(SourceConfigField.SOURCE_NAME_FIELD_NAME);
    assertEquals("Error updating OpenSearch source.", "updatedName", updatedName);

    // delete openSearch source
    boolean deleteSuccess =
        SOURCES_REQUEST_HELPER.deleteSource(SourcesRequestHelper.SourceType.OPEN_SEARCH, pid);
    assertThat("Error deleting OpenSearch source.", deleteSuccess, is(true));
  }

  @Ignore
  @Test
  // TODO: 8/23/17 phuffer - In order for the CSW feature to start, the ActionProvider must be
  // removed from the spatial-csw-transformer blueprint.
  public void testCsw() {
    // create CSW source
    Map<String, Object> cswConfigToSave =
        createCswArgs(TEST_SOURCE_NAME, null, MASKED_PASSWORD).getValue();

    boolean createSuccess =
        SOURCES_REQUEST_HELPER.createSource(SourcesRequestHelper.SourceType.CSW, cswConfigToSave);
    assertThat("Error creating CSW source.", createSuccess, is(true));

    SOURCES_REQUEST_HELPER.waitForCswSource(cswConfigToSave, true);

    // get created CSW source
    List<Map<String, Object>> cswSources =
        SOURCES_REQUEST_HELPER.getSources(SourcesRequestHelper.SourceType.CSW);
    assertThat(cswSources, is(not(empty())));

    Map<String, String> sourceProperties = (Map) cswSources.get(0).get(CSW_NAME);
    String pid = sourceProperties.get(PID);
    assertThat("Error getting CSW source.", StringUtils.isNotEmpty(pid));

    // update CSW source
    Map<String, Object> updateArgs = createCswArgs("updatedName", pid, MASKED_PASSWORD).getValue();
    boolean updateSuccess =
        SOURCES_REQUEST_HELPER.updateSource(SourcesRequestHelper.SourceType.CSW, updateArgs);
    assertThat("Error updating CSW source", updateSuccess, is(true));

    SOURCES_REQUEST_HELPER.waitForCswSource(updateArgs, false);

    // get updated CSW source
    cswSources = SOURCES_REQUEST_HELPER.getSources(SourcesRequestHelper.SourceType.CSW);
    assertThat(cswSources, is(not(empty())));

    sourceProperties = (Map) cswSources.get(0).get(OPEN_SEARCH_NAME);
    String updatedName = sourceProperties.get(SourceConfigField.SOURCE_NAME_FIELD_NAME);
    assertEquals("Error updating CSW source.", "updatedName", updatedName);

    // delete CSW source
    boolean deleteSuccess =
        SOURCES_REQUEST_HELPER.deleteSource(SourcesRequestHelper.SourceType.CSW, pid);
    assertThat("Error deleting CSW source.", deleteSuccess, is(true));
  }

  public WfsSourceConfigurationField createWfsArgs(
      String sourceName, String pid, String wfsVersion) {
    WfsSourceConfigurationField config = new WfsSourceConfigurationField();
    config
        .wfsVersion(wfsVersion)
        .endpointUrl("https://localhost:8993/geoserver/wfs")
        .sourceName(sourceName)
        .credentials()
        .username(TEST_USERNAME)
        .password(MASKED_PASSWORD);

    if (org.apache.commons.lang.StringUtils.isNotEmpty(pid)) {
      config.pid(pid);
    }

    return config;
  }

  public OpenSearchSourceConfigurationField createOpenSearchArgs(String sourceName, String pid) {
    OpenSearchSourceConfigurationField config = new OpenSearchSourceConfigurationField();
    config
        .endpointUrl("https://localhost:8993/services/csw")
        .sourceName(sourceName)
        .credentials()
        .username(TEST_USERNAME)
        .password(MASKED_PASSWORD);

    if (org.apache.commons.lang.StringUtils.isNotEmpty(pid)) {
      config.pid(pid);
    }

    return config;
  }

  public CswSourceConfigurationField createCswArgs(String sourceName, String pid, String password) {
    CswSourceConfigurationField config = new CswSourceConfigurationField();
    config
        .outputSchema("testOutputSchema")
        .spatialOperator(CswSpatialOperator.NoFilter.ENUM_TITLE)
        .cswProfile(CswProfile.DDFCswFederatedSource.CSW_FEDERATION_PROFILE_SOURCE)
        .endpointUrl("https://localhost:8993/services/csw")
        .sourceName(sourceName)
        .credentials()
        .username(TEST_USERNAME)
        .password(password);

    if (org.apache.commons.lang.StringUtils.isNotEmpty(pid)) {
      config.pid(pid);
    }

    return config;
  }
}
