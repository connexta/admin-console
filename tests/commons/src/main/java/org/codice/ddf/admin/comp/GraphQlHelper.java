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
package org.codice.ddf.admin.comp;

import static com.jayway.restassured.RestAssured.given;
import static junit.framework.TestCase.fail;

import com.jayway.restassured.response.ExtractableResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.IOUtils;
import org.boon.Boon;
import org.codice.ddf.itests.common.WaitCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GraphQlHelper {

  private static final Logger LOGGER = LoggerFactory.getLogger(GraphQlHelper.class);
  public static final String USERNAME = "admin";
  public static final String PASSWORD = "admin";

  private Class resourceClass;

  private String queryResourcePath;

  private String mutationResourcePath;

  private String graphQlEndpoint;

  private String graphQlSchemaEndpoint;

  public GraphQlHelper(
      Class resourceClass,
      String queryResourcePath,
      String mutationResourcePath,
      String graphQlEndpoint) {
    this.resourceClass = resourceClass;
    this.queryResourcePath = queryResourcePath;
    this.mutationResourcePath = mutationResourcePath;
    this.graphQlEndpoint = graphQlEndpoint;
    this.graphQlSchemaEndpoint = graphQlEndpoint + "/schema.json";
  }

  public GraphQLRequest createRequest() {
    return new GraphQLRequest(queryResourcePath, mutationResourcePath, graphQlEndpoint);
  }

  public GraphQLRequest createMutationRequest(String usingMutation, Map<String, Object> arguments) {
    return populateRequestArgs(createRequest().usingMutation(usingMutation), arguments);
  }

  public GraphQLRequest createQueryRequest(String usingQuery, Map<String, Object> arguments) {
    return populateRequestArgs(createRequest().usingQuery(usingQuery), arguments);
  }

  public void waitForGraphQLSchema() {
    WaitCondition.expect("GraphQL Schema responds")
        .within(30L, TimeUnit.SECONDS)
        .until(
            () -> {
              try {
                return given()
                        .when()
                        .auth()
                        .basic(USERNAME, PASSWORD)
                        .get(graphQlSchemaEndpoint)
                        .then()
                        .extract()
                        .jsonPath()
                        .get("data")
                    != null;

              } catch (Exception e) {
                return false;
              }
            });
  }

  public <T> Optional<T> mutationRequest(
      String usingMutation, String jsonResultPath, Map<String, Object> arguments) {
    return sendAndExtract(createMutationRequest(usingMutation, arguments), jsonResultPath);
  }

  public <T> Optional<T> queryRequest(
      String usingQuery, String jsonResultPath, Map<String, Object> arguments) {
    return sendAndExtract(createQueryRequest(usingQuery, arguments), jsonResultPath);
  }

  private <T> Optional<T> sendAndExtract(GraphQLRequest request, String jsonPath) {
    try {
      ExtractableResponse response = request.send().getResponse();
      if (response != null && response.jsonPath().get("errors") != null) {
        LOGGER.debug("GraphQL response had errors.\n{}", response.body().asString());
        return Optional.empty();
      }

      return Optional.of(response.jsonPath().get(jsonPath));
    } catch (Exception e) {
      LOGGER.debug(
          "Error sending GraphQL request or parsing the JSON response. Double check the jsonResultPath maps to a key in the JSON response.",
          e);
      return Optional.empty();
    }
  }

  private String getResourceAsString(String filePath) {
    try {
      return IOUtils.toString(resourceClass.getResourceAsStream(filePath), "UTF-8");
    } catch (IOException e) {
      fail("Unable to retrieve resource: " + filePath);
    }

    return null;
  }

  private GraphQLRequest populateRequestArgs(GraphQLRequest request, Map<String, Object> args) {
    for (String key : args.keySet()) {
      request.addArgument(key, args.get(key));
    }
    return request;
  }

  public class GraphQLRequest {
    private String queryResourcePath;

    private String mutationResourcePath;

    private String mutationFile;

    private String queryFile;

    private String graphQlEndpoint;

    private Map<String, Object> reqArgs;

    private ExtractableResponse response;

    public GraphQLRequest(
        String queryResourcePath, String mutationResourcePath, String graphQlEndpoint) {
      this.queryResourcePath = queryResourcePath;
      this.mutationResourcePath = mutationResourcePath;
      this.graphQlEndpoint = graphQlEndpoint;
      this.reqArgs = new HashMap<>();
    }

    public GraphQLRequest addArgument(String argName, Object argValue) {
      reqArgs.put(argName, argValue);
      return this;
    }

    public GraphQLRequest usingQuery(String queryFileName) {
      this.queryFile = queryFileName;
      return this;
    }

    public GraphQLRequest usingMutation(String mutationFileName) {
      this.mutationFile = mutationFileName;
      return this;
    }

    public GraphQLRequest send() {
      Map<String, String> query = new HashMap<>();

      String queryBody = null;

      if (queryFile != null) {
        queryBody = getResourceAsString(queryResourcePath + queryFile);
      } else if (mutationFile != null) {
        queryBody = getResourceAsString(mutationResourcePath + mutationFile);
      } else {
        fail(
            "Failed to send GraphQLRequest. A query or mutation file must be specified before attempting to send the request.");
      }
      query.put("query", queryBody);

      if (!reqArgs.isEmpty()) {
        query.put("variables", Boon.toJson(reqArgs));
      }

      String queryStr = Boon.toPrettyJson(query);
      LOGGER.debug("\nSending request:\n{}", queryStr);

      response =
          given()
              .when()
              .auth()
              .basic(USERNAME, PASSWORD)
              .body(queryStr)
              .post(graphQlEndpoint)
              .then()
              .extract();

      LOGGER.debug("\nReplied with:\n{}", response.body().asString());

      return this;
    }

    public ExtractableResponse getResponse() {
      return response;
    }
  }
}
