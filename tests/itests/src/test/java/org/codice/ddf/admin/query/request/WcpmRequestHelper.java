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

import static org.awaitility.Awaitility.await;
import static org.codice.ddf.test.common.options.TestResourcesOptions.getTestResource;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.fail;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jayway.restassured.response.ExtractableResponse;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.codice.ddf.admin.common.fields.common.ContextPath;
import org.codice.ddf.admin.graphql.test.GraphQlHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WcpmRequestHelper {

  private static final Logger LOGGER = LoggerFactory.getLogger(WcpmRequestHelper.class);

  public static final String WCPM_QUERY_RESOURCE_PATH = "/query/wcpm/query/";

  public static final String WCPM_MUTATION_RESOURCE_PATH = "/query/wcpm/mutation/";

  private static final Gson GSON = new GsonBuilder().create();

  private GraphQlHelper requestFactory;

  private List<String> originalWhitelist;

  private List<Map<String, Object>> originalPolicies;

  public WcpmRequestHelper(String graphQlEndpoint) {
    requestFactory =
        new GraphQlHelper(
            getTestResource(WCPM_QUERY_RESOURCE_PATH),
            getTestResource(WCPM_MUTATION_RESOURCE_PATH),
            graphQlEndpoint);
  }

  public void saveWhitelistDefaultValues() {
    await("getWhiteList appears in schema.")
        .atMost(30L, TimeUnit.SECONDS)
        .until(
            () -> {
              LOGGER.info("Waiting for wcpm in graphql schema.");
              try {
                originalWhitelist = getWhiteListContexts();
              } catch (Exception e) {
                LOGGER.info("Failed to sent getWhiteListRequest.", e);
              }
              return originalWhitelist != null && !originalWhitelist.isEmpty();
            });

    await("Context policy manager appears in schema.")
        .atMost(30L, TimeUnit.SECONDS)
        .until(
            () -> {
              LOGGER.info("Waiting for wcpm in graphql schema.");
              originalPolicies = getContextPolicies();
              return originalPolicies != null && !originalPolicies.isEmpty();
            });
  }

  public void resetWhiteList() {
    saveWhiteListContexts(originalWhitelist);
    waitForContextPolicies(originalPolicies);
  }

  public void resetContextPolicies() {
    saveContextPolicies(originalPolicies);
    waitForContextPolicies(originalPolicies);
  }

  public void waitForContextPolicies(List<Map<String, Object>> expectedPolicies) {
    await("Successfully retrieve policies.")
        .atMost(30L, TimeUnit.SECONDS)
        .until(
            () -> {
              List<Map<String, Object>> retrievedPolicies = getContextPolicies();

              boolean conditionMet =
                  retrievedPolicies != null
                      && retrievedPolicies
                          .stream()
                          .allMatch(
                              policyToMatch -> policyMatches(policyToMatch, expectedPolicies));

              if (!conditionMet) {
                LOGGER.info("Expecting configs:\n{}", GSON.toJson(expectedPolicies));
                LOGGER.info("Received:\n", GSON.toJson(retrievedPolicies));
              }

              return conditionMet;
            });
  }

  public boolean policyMatches(
      Map<String, Object> policyToMatch, List<Map<String, Object>> policies) {
    for (Map<String, Object> policy : policies) {
      Map<String, Object> copyPolicyToMatch = new HashMap<>(policyToMatch);
      Map<String, Object> copyPolicy = new HashMap<>(policy);

      // It's possible for paths to be rearranged on persist, order does not matter in terms of
      // context paths
      List<String> pathsToMatch =
          (List<String>) copyPolicyToMatch.get(ContextPath.ListImpl.DEFAULT_NAME);
      List<String> paths = (List<String>) copyPolicy.get(ContextPath.ListImpl.DEFAULT_NAME);

      copyPolicyToMatch
          .entrySet()
          .removeIf(entry -> entry.getKey().equals(ContextPath.ListImpl.DEFAULT_NAME));
      copyPolicy
          .entrySet()
          .removeIf(entry -> entry.getKey().equals(ContextPath.ListImpl.DEFAULT_NAME));

      if (copyPolicy.equals(copyPolicyToMatch)
          && pathsToMatch.containsAll(paths)
          && paths.containsAll(pathsToMatch)) {
        return true;
      }
    }

    return false;
  }

  public void waitForWhiteList(List<String> expectedWhiteList) {
    await("Successfully retrieve expected white list.")
        .atMost(30L, TimeUnit.SECONDS)
        .until(
            () -> {
              List<String> retrievedWhiteList = getWhiteListContexts();

              boolean conditionMet =
                  retrievedWhiteList != null
                      && expectedWhiteList.containsAll(retrievedWhiteList)
                      && retrievedWhiteList.containsAll(expectedWhiteList);

              if (!conditionMet) {
                LOGGER.info("Expecting configs:\n{}", GSON.toJson(expectedWhiteList));
                LOGGER.info("Received:\n{}", GSON.toJson(retrievedWhiteList));
              }

              return conditionMet;
            });
  }

  public List<String> getWhiteListContexts() {
    ExtractableResponse response =
        requestFactory.createRequest().usingQuery("GetWhiteListed.graphql").send().getResponse();

    if (responseHasErrors(response)) {
      return Collections.emptyList();
    }

    return response.jsonPath().get("data.wcpm.whitelisted");
  }

  public List<Map<String, Object>> getContextPolicies() {
    ExtractableResponse response =
        requestFactory.createRequest().usingQuery("GetPolicies.graphql").send().getResponse();

    if (responseHasErrors(response)) {
      return Collections.emptyList();
    }

    return response.jsonPath().get("data.wcpm.policies");
  }

  public void saveWhiteListContexts(List<String> toSaveWhiteList) {
    try {

      ExtractableResponse response =
          requestFactory
              .createRequest()
              .usingMutation("SaveWhiteListed.graphql")
              .addArgument("whitelistContexts", toSaveWhiteList)
              .send()
              .getResponse();

      List errors = response.jsonPath().getList("errors");

      assertThat(errors, is(nullValue()));
    } catch (Exception e) {
      fail("Something went wrong saving white list contexts query.\n" + e);
      resetWhiteList();
    }
  }

  public void saveContextPolicies(List<Map<String, Object>> policies) {
    try {
      List errors =
          requestFactory
              .createRequest()
              .usingMutation("SavePolicies.graphql")
              .addArgument("policies", policies)
              .send()
              .getResponse()
              .jsonPath()
              .get("errors");

      assertThat(errors, is(nullValue()));
    } catch (Exception e) {
      fail("Something went wrong saving white list contexts query.\n" + e);
      resetWhiteList();
    }
  }

  public List<String> getAuthType() {
    ExtractableResponse response =
        requestFactory.createRequest().usingQuery("GetAuthTypes.graphql").send().getResponse();

    if (responseHasErrors(response)) {
      return Collections.emptyList();
    }

    return response.jsonPath().get("data.wcpm.authTypes");
  }

  public List<String> getRealms() {
    ExtractableResponse response =
        requestFactory.createRequest().usingQuery("GetRealms.graphql").send().getResponse();

    if (responseHasErrors(response)) {
      return Collections.emptyList();
    }

    return response.jsonPath().get("data.wcpm.realms");
  }

  private boolean responseHasErrors(ExtractableResponse response) {
    return response != null && response.jsonPath().get("errors") != null;
  }

  public List<String> getInitialWhiteList() {
    return originalWhitelist;
  }

  public List<Map<String, Object>> getInitialPolicies() {
    return originalPolicies;
  }
}
