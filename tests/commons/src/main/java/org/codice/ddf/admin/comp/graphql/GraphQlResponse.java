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
package org.codice.ddf.admin.comp.graphql;

import com.google.common.collect.ImmutableList;
import com.jayway.restassured.response.ExtractableResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import org.apache.commons.collections.CollectionUtils;
import org.codice.ddf.admin.api.report.ErrorMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GraphQlResponse {

  private static final Logger LOGGER = LoggerFactory.getLogger(GraphQlResponse.class);

  public static final String GRAPHQL_ERRORS = "errors";

  private final List<ErrorMessage> errors;

  private final ExtractableResponse response;

  /**
   * Creates a GraphQL response from an {@link ExtractableResponse}. If there are errors in the
   * response, errors in the {@code GraphQlResponse} will be populated.
   *
   * @param response {@link ExtractableResponse} of the GraphQL request
   */
  public GraphQlResponse(@Nonnull final ExtractableResponse response) {
    errors = new ArrayList<>();
    this.response = response;

    try {
      if (response.jsonPath().get(GRAPHQL_ERRORS) != null) {
        LOGGER.debug("GraphQL response had errors.\n{}", response.body().asString());
        populateErrors(response);
      }
    } catch (Exception e) {
      LOGGER.debug("Error parsing the JSON response. No error key in the response.", e);
    }
  }

  public boolean hasErrors() {
    return CollectionUtils.isNotEmpty(errors);
  }

  public List<ErrorMessage> getErrors() {
    return ImmutableList.copyOf(errors);
  }

  public <T> T getResult(String jsonPath) {
    try {
      return response.jsonPath().get(jsonPath);
    } catch (Exception e) {
      LOGGER.debug(
          "Error parsing the JSON response. Verify the jsonPath maps to a key in the JSON response.",
          e);
    }
    return null;
  }

  public ExtractableResponse getResponse() {
    return response;
  }

  private void populateErrors(ExtractableResponse response) {
    List<Map<String, Object>> errors = response.jsonPath().get(GRAPHQL_ERRORS);
    for (Map<String, Object> error : errors) {
      String errorCode = (String) error.get("message");
      List<Object> path = (List<Object>) error.get("path");
      this.errors.add(new Error(errorCode, path));
    }
  }

  public class Error implements ErrorMessage {

    String code;
    List<Object> path;

    public Error(String code, List<Object> path) {
      this.code = code;
      this.path = path;
    }

    @Override
    public String getCode() {
      return code;
    }

    @Override
    public List<Object> getPath() {
      return path;
    }

    @Override
    public ErrorMessage setPath(List<Object> path) {
      this.path = path;
      return this;
    }
  }
}
