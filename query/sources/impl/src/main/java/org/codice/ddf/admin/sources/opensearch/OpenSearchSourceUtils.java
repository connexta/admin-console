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
package org.codice.ddf.admin.sources.opensearch;

import static java.net.HttpURLConnection.HTTP_OK;
import static org.codice.ddf.admin.common.report.message.DefaultMessages.unknownEndpointError;
import static org.codice.ddf.admin.common.services.ServiceCommons.FLAG_PASSWORD;
import static org.codice.ddf.admin.sources.utils.SourceUtilCommons.SOURCES_NAMESPACE_CONTEXT;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.codice.ddf.admin.api.report.Report;
import org.codice.ddf.admin.common.PrioritizedBatchExecutor;
import org.codice.ddf.admin.common.fields.common.CredentialsField;
import org.codice.ddf.admin.common.fields.common.HostField;
import org.codice.ddf.admin.common.fields.common.ResponseField;
import org.codice.ddf.admin.common.fields.common.UrlField;
import org.codice.ddf.admin.common.report.Reports;
import org.codice.ddf.admin.sources.fields.type.OpenSearchSourceConfigurationField;
import org.codice.ddf.admin.sources.utils.RequestUtils;
import org.codice.ddf.admin.sources.utils.SourceTaskCallable;
import org.codice.ddf.admin.sources.utils.SourceTaskHandler;
import org.codice.ddf.admin.sources.utils.SourceUtilCommons;
import org.codice.ddf.internal.admin.configurator.actions.ConfiguratorSuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

public class OpenSearchSourceUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(OpenSearchSourceUtils.class);

  private static final List<List<String>> URL_FORMATS =
      ImmutableList.of(
          ImmutableList.of("https://%s:%d/services/catalog/query", "https://%s:%d/catalog/query"),
          ImmutableList.of("http://%s:%d/services/catalog/query", "http://%s:%d/catalog/query"));

  public static final Map<String, Object> GET_CAPABILITIES_PARAMS =
      ImmutableMap.of("q", "test", "mr", "1", "src", "local");

  private static final String TOTAL_RESULTS_XPATH = "//os:totalResults|//opensearch:totalResults";

  private static final int THREAD_POOL_SIZE = 2;

  private final SourceUtilCommons sourceUtilCommons;

  private RequestUtils requestUtils;

  public OpenSearchSourceUtils(ConfiguratorSuite configuratorSuite) {
    this.requestUtils = new RequestUtils();
    this.sourceUtilCommons = new SourceUtilCommons(configuratorSuite);
  }

  /**
   * Attempts to discover an OpenSearch endpoint from the given hostname and port
   *
   * <p>Possible Error Codes to be returned - {@link
   * org.codice.ddf.admin.common.report.message.DefaultMessages#UNKNOWN_ENDPOINT}
   *
   * @param hostField hostname and port to probe for OpenSearch capabilities
   * @param creds optional credentials for authentication
   * @return a {@link Report} containing the discovered {@link OpenSearchSourceConfigurationField}
   *     on success, or containing {@link org.codice.ddf.admin.api.report.ErrorMessage}s on failure.
   */
  public Report<OpenSearchSourceConfigurationField> getOpenSearchConfigFromHost(
      HostField hostField, CredentialsField creds) {
    List<List<SourceTaskCallable<OpenSearchSourceConfigurationField>>> taskList = new ArrayList<>();

    for (List<String> urlFormats : URL_FORMATS) {
      List<SourceTaskCallable<OpenSearchSourceConfigurationField>> callables =
          urlFormats
              .stream()
              .map(
                  urlFormat ->
                      new SourceTaskCallable<>(
                          urlFormat, hostField, creds, this::getOpenSearchConfigFromUrl))
              .collect(Collectors.toList());
      taskList.add(callables);
    }

    PrioritizedBatchExecutor<
            Report<OpenSearchSourceConfigurationField>, Report<OpenSearchSourceConfigurationField>>
        prioritizedExecutor =
            new PrioritizedBatchExecutor(
                THREAD_POOL_SIZE,
                taskList,
                new SourceTaskHandler<OpenSearchSourceConfigurationField>());

    Optional<Report<OpenSearchSourceConfigurationField>> result = prioritizedExecutor.getFirst();

    if (result.isPresent()) {
      return result.get();
    } else {
      return Reports.from(unknownEndpointError(hostField.getPath()));
    }
  }

  public Report<OpenSearchSourceConfigurationField> getOpenSearchConfigFromUrl(
      UrlField urlField, CredentialsField creds) {
    Report<ResponseField> responseResult =
        requestUtils.sendGetRequest(urlField, creds, GET_CAPABILITIES_PARAMS);

    if (responseResult.containsErrorMessages()) {
      return Reports.from(responseResult.getErrorMessages());
    }

    return getOpenSearchConfigFromResponse(responseResult.getResult(), creds);
  }

  /**
   * Attempts to create an OpenSearch configuration with and OpenSearch capabilities response
   *
   * <p>Possible Error Codes to be returned - {@link
   * org.codice.ddf.admin.common.report.message.DefaultMessages#UNKNOWN_ENDPOINT}
   *
   * @param responseField The URL to probe for OpenSearch capabilities
   * @param creds optional credentials used in the original capabilities request
   * @return a {@link Report} containing the {@link OpenSearchSourceConfigurationField} or
   *     containing {@link org.codice.ddf.admin.api.report.ErrorMessage}s on failure.
   */
  private Report<OpenSearchSourceConfigurationField> getOpenSearchConfigFromResponse(
      ResponseField responseField, CredentialsField creds) {

    String responseBody = responseField.responseBody();
    int statusCode = responseField.statusCode();

    if (statusCode != HTTP_OK || responseBody.length() < 1) {
      return Reports.from(unknownEndpointError(responseField.requestUrlField().getPath()));
    }

    Document capabilitiesXml;
    try {
      capabilitiesXml = sourceUtilCommons.createDocument(responseBody);
    } catch (Exception e) {
      LOGGER.debug("Failed to read response from OpenSearch endpoint.");
      return Reports.from(unknownEndpointError(responseField.requestUrlField().getPath()));
    }

    XPath xpath = XPathFactory.newInstance().newXPath();
    xpath.setNamespaceContext(SOURCES_NAMESPACE_CONTEXT);

    try {
      if ((Boolean)
          xpath.compile(TOTAL_RESULTS_XPATH).evaluate(capabilitiesXml, XPathConstants.BOOLEAN)) {
        OpenSearchSourceConfigurationField config = new OpenSearchSourceConfigurationField();
        config
            .endpointUrl(responseField.requestUrl())
            .credentials()
            .username(creds.username())
            .password(FLAG_PASSWORD);

        return Reports.from(config);
      } else {
        return Reports.from(unknownEndpointError(responseField.requestUrlField().getPath()));
      }
    } catch (XPathExpressionException e) {
      LOGGER.debug("Failed to compile OpenSearch totalResults XPath.");
      return Reports.from(unknownEndpointError(responseField.requestUrlField().getPath()));
    }
  }

  @SuppressWarnings(
      "squid:UnusedPrivateMethod" /* For testing purposes only. Groovy can access private methods. */)
  private void setRequestUtils(RequestUtils requestUtils) {
    this.requestUtils = requestUtils;
  }
}
