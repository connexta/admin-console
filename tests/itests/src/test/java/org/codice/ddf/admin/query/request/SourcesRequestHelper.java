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
package org.codice.ddf.admin.query.request;

import com.jayway.restassured.response.ExtractableResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.codice.ddf.admin.common.fields.common.PidField;
import org.codice.ddf.admin.comp.GraphQlHelper;
import org.codice.ddf.admin.sources.fields.type.CswSourceConfigurationField;
import org.codice.ddf.admin.sources.fields.type.OpenSearchSourceConfigurationField;
import org.codice.ddf.admin.sources.fields.type.WfsSourceConfigurationField;
import org.codice.ddf.itests.common.WaitCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SourcesRequestHelper {

  private static final Logger LOGGER = LoggerFactory.getLogger(SourcesRequestHelper.class);

  public static final String SOURCES_QUERY_RESOURCE_PATH = "/query/sources/query/";

  public static final String SOURCES_MUTATION_RESOURCE_PATH = "/query/sources/mutation/";

  public static final String MASKED_PASSWORD = "*****";

  private GraphQlHelper requestFactory;

  public SourcesRequestHelper(String graphQlEndpoint) {
    requestFactory =
        new GraphQlHelper(
            WcpmRequestHelper.class,
            SOURCES_QUERY_RESOURCE_PATH,
            SOURCES_MUTATION_RESOURCE_PATH,
            graphQlEndpoint);
  }

  public void waitForSourcesInSchema() {
    WaitCondition.expect("get sources in schema.")
        .within(30L, TimeUnit.SECONDS)
        .until(
            () -> {
              LOGGER.info("Waiting for sources in graphql schema.");
              Map<String, Object> sources = getAllSources();
              return sources != null;
            });
  }

  public Map<String, Object> getAllSources() {
    GraphQlHelper.GraphQLRequest graphQLRequest = requestFactory.createRequest();
    graphQLRequest.usingQuery("GetAllSources.graphql");

    ExtractableResponse response = doSend(graphQLRequest);
    if (responseHasErrors(response)) {
      return new HashMap<>();
    }

    return extractResponse(response, "data");
  }

  public boolean createSource(SourceType sourceType, Map<String, Object> configToSave) {
    GraphQlHelper.GraphQLRequest graphQLRequest = requestFactory.createRequest();
    String jsonPath = "";

    switch (sourceType) {
      case CSW:
        graphQLRequest
            .usingMutation("CreateCswSource.graphql")
            .addArgument(CswSourceConfigurationField.DEFAULT_FIELD_NAME, configToSave);
        jsonPath = "data.createCswSource";
        break;
      case WFS:
        graphQLRequest
            .usingMutation("CreateWfsSource.graphql")
            .addArgument(WfsSourceConfigurationField.DEFAULT_FIELD_NAME, configToSave);
        jsonPath = "data.createWfsSource";
        break;
      case OPEN_SEARCH:
        graphQLRequest
            .usingMutation("CreateOpenSearchSource.graphql")
            .addArgument(OpenSearchSourceConfigurationField.DEFAULT_FIELD_NAME, configToSave);
        jsonPath = "data.createOpenSearchSource";
    }

    ExtractableResponse response = doSend(graphQLRequest);
    if (responseHasErrors(response)) {
      return false;
    }

    return extractResponse(response, jsonPath);
  }

  public List<Map<String, Object>> getSources(SourceType sourceType) {
    GraphQlHelper.GraphQLRequest graphQLRequest = requestFactory.createRequest();
    String jsonPath = "";

    switch (sourceType) {
      case CSW:
        graphQLRequest.usingQuery("GetCswSources.graphql");
        jsonPath = "data.csw.sources";
        break;
      case WFS:
        graphQLRequest.usingQuery("GetWfsSources.graphql");
        jsonPath = "data.wfs.sources";
        break;
      case OPEN_SEARCH:
        graphQLRequest.usingQuery("GetOpenSearchSources.graphql");
        jsonPath = "data.openSearch.sources";
        break;
    }

    ExtractableResponse response = doSend(graphQLRequest);
    if (responseHasErrors(response)) {
      return new ArrayList<>();
    }

    return extractResponse(response, jsonPath);
  }

  public void waitForCswSource(Map<String, Object> expectedConfig, boolean ignorePid) {
    WaitCondition.expect("Failed to retrieve expect CSW sources")
        .within(30L, TimeUnit.SECONDS)
        .until(
            () -> {
              List<Map<String, Object>> currentCswSources = getSources(SourceType.CSW);
              Map<String, Object> source = currentCswSources.get(0);
              Map<String, Object> cswConfig =
                  (Map) source.get(CswSourceConfigurationField.DEFAULT_FIELD_NAME);

              if (ignorePid) {
                cswConfig.put(PidField.DEFAULT_FIELD_NAME, null);
              }

              return expectedConfig.equals(cswConfig);
            });
  }

  public void waitForOpenSearch(Map<String, Object> expectedConfig, boolean ignorePid) {
    WaitCondition.expect("Failed to retrieve expected OpenSearch source")
        .within(30L, TimeUnit.SECONDS)
        .until(
            () -> {
              List<Map<String, Object>> currentOpenSearchSources =
                  getSources(SourceType.OPEN_SEARCH);
              Map<String, Object> source = currentOpenSearchSources.get(0);
              Map<String, Object> openSearchConfig =
                  (Map) source.get(OpenSearchSourceConfigurationField.DEFAULT_FIELD_NAME);

              if (ignorePid) {
                openSearchConfig.put(PidField.DEFAULT_FIELD_NAME, null);
              }

              return expectedConfig.equals(openSearchConfig);
            });
  }

  public void waitForWfsSource(Map<String, Object> expectedConfig, boolean ignorePid) {
    WaitCondition.expect("Failed to retrieve expect WFS sources")
        .within(30L, TimeUnit.SECONDS)
        .until(
            () -> {
              List<Map<String, Object>> currentWfsSources = getSources(SourceType.WFS);
              Map<String, Object> source = currentWfsSources.get(0);
              Map<String, Object> wfsConfig =
                  (Map) source.get(WfsSourceConfigurationField.DEFAULT_FIELD_NAME);

              if (ignorePid) {
                wfsConfig.put(PidField.DEFAULT_FIELD_NAME, null);
              }

              return expectedConfig.equals(wfsConfig);
            });
  }

  public boolean deleteSource(SourceType sourceType, String pid) {
    GraphQlHelper.GraphQLRequest graphQLRequest = requestFactory.createRequest();
    String jsonPath = "";

    PidField pidField = new PidField();
    pidField.setValue(pid);

    switch (sourceType) {
      case CSW:
        graphQLRequest.usingMutation("DeleteCswSource.graphql");
        jsonPath = "data.deleteCswSource";
        break;
      case WFS:
        graphQLRequest.usingMutation("DeleteWfsSource.graphql");
        jsonPath = "data.deleteWfsSource";
        break;
      case OPEN_SEARCH:
        graphQLRequest.usingMutation("DeleteOpenSearchSource.graphql");
        jsonPath = "data.deleteOpenSearchSource";
    }

    graphQLRequest.addArgument(PidField.DEFAULT_FIELD_NAME, pidField.getValue());

    ExtractableResponse response = doSend(graphQLRequest);
    if (responseHasErrors(response)) {
      return false;
    }

    return extractResponse(response, jsonPath);
  }

  public boolean updateSource(SourceType sourceType, Map<String, Object> configToUpdate) {
    GraphQlHelper.GraphQLRequest graphQLRequest = requestFactory.createRequest();
    String jsonPath = "";

    switch (sourceType) {
      case CSW:
        graphQLRequest
            .usingMutation("UpdateCswSource.graphql")
            .addArgument(CswSourceConfigurationField.DEFAULT_FIELD_NAME, configToUpdate);
        jsonPath = "data.updateCswSource";
        break;
      case WFS:
        graphQLRequest
            .usingMutation("UpdateWfsSource.graphql")
            .addArgument(WfsSourceConfigurationField.DEFAULT_FIELD_NAME, configToUpdate);
        jsonPath = "data.updateWfsSource";
        break;
      case OPEN_SEARCH:
        graphQLRequest
            .usingMutation("UpdateOpenSearchSource.graphql")
            .addArgument(OpenSearchSourceConfigurationField.DEFAULT_FIELD_NAME, configToUpdate);
        jsonPath = "data.updateOpenSearchSource";
    }

    ExtractableResponse response = doSend(graphQLRequest);
    if (responseHasErrors(response)) {
      return false;
    }

    return extractResponse(response, jsonPath);
  }

  private boolean responseHasErrors(ExtractableResponse response) {
    return response != null && response.jsonPath().get("errors") != null;
  }

  private ExtractableResponse doSend(GraphQlHelper.GraphQLRequest request) {
    return request.send().getResponse();
  }

  private <T> T extractResponse(ExtractableResponse response, String jsonPath) {
    return response.jsonPath().get(jsonPath);
  }

  public enum SourceType {
    WFS,
    OPEN_SEARCH,
    CSW
  }
}
