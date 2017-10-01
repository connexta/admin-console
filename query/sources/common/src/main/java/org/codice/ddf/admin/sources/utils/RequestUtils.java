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
package org.codice.ddf.admin.sources.utils;

import static org.codice.ddf.admin.common.report.message.DefaultMessages.cannotConnectError;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.codice.ddf.admin.api.report.Report;
import org.codice.ddf.admin.common.fields.common.CredentialsField;
import org.codice.ddf.admin.common.fields.common.ResponseField;
import org.codice.ddf.admin.common.fields.common.UrlField;
import org.codice.ddf.admin.common.report.Reports;
import org.codice.ddf.cxf.SecureCxfClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(RequestUtils.class);

  private static final int CLIENT_TIMEOUT_MILLIS = 10000;

  /**
   * Creates a secure CXF {@code WebClient} and sends a GET request to the URL given by the
   * clientUrl and optional queryParams.
   *
   * <p>Possible Error Codes to be returned - {@link
   * org.codice.ddf.admin.common.report.message.DefaultMessages#CANNOT_CONNECT}
   *
   * @param requestUrl url to send GET request to
   * @param creds optional credentials for basic authentication
   * @param queryParams optional query parameters
   * @return {@link Report} containing a {@link ResponseField}, or containing an {@link
   *     org.codice.ddf.admin.api.report.ErrorMessage}
   */
  public Report<ResponseField> sendGetRequest(
      UrlField requestUrl, CredentialsField creds, Map<String, Object> queryParams) {
    WebClient webClient =
        createWebClientBuilder(requestUrl.getValue(), creds.username(), creds.password())
            .queryParams(queryParams)
            .build();

    return sendGetRequest(webClient, requestUrl);
  }

  /**
   * Sends a GET request with the given {@code WebClient}
   *
   * <p>Possible Error Codes to return - {@link
   * org.codice.ddf.admin.common.report.message.DefaultMessages#CANNOT_CONNECT}
   *
   * @param webClient {@code WebClient} to send a GET request with
   * @param urlField the original request url
   * @return {@link Response} of the request
   */
  public Report<ResponseField> sendGetRequest(WebClient webClient, UrlField urlField) {
    Report<ResponseField> responseResult = Reports.fromErrors(endpointIsReachable(urlField));
    if (responseResult.containsErrorMessages()) {
      return responseResult;
    }

    try {
      Response response = webClient.get();
      return Reports.from(responseFieldFromResponse(response, urlField));

    } catch (ProcessingException e) {
      return Reports.from(cannotConnectError(urlField.getPath()));
    }
  }

  /**
   * Sends a POST request to the specified url.
   *
   * <p>Possible Error Codes to be returned - {@link
   * org.codice.ddf.admin.common.report.message.DefaultMessages#CANNOT_CONNECT}
   *
   * @param urlField URL to send Post request to
   * @param creds optional credentials consisting of a username and password
   * @param contentType Mime type of the post body
   * @param content Body of the post request
   * @return a {@link Report} containing a {@link ResponseField} or an {@link
   *     org.codice.ddf.admin.api.report.ErrorMessage} on failure.
   */
  public Report<ResponseField> sendPostRequest(
      UrlField urlField, CredentialsField creds, String contentType, String content) {
    WebClient webClient =
        createWebClientBuilder(urlField.getValue(), creds.username(), creds.password())
            .contentType(contentType)
            .build();

    return sendPostRequest(webClient, urlField, content);
  }

  /**
   * Sends a POST request with the given {@code WebClient}
   *
   * <p>Possible Error Codes to return - {@link
   * org.codice.ddf.admin.common.report.message.DefaultMessages#CANNOT_CONNECT}
   *
   * @param webClient {@code WebClient} to send POST request with
   * @param urlField original request url field
   * @param content Body of the post request
   * @return a {@link Report} containing a {@link ResponseField} or an {@link
   *     org.codice.ddf.admin.api.report.ErrorMessage} on failure.
   */
  public Report<ResponseField> sendPostRequest(
      WebClient webClient, UrlField urlField, String content) {
    Report endpointIsReachableReport = endpointIsReachable(urlField);
    if (endpointIsReachableReport.containsErrorMessages()) {
      return Reports.fromErrors(endpointIsReachable(urlField));
    }
    try {
      Response response = webClient.post(content);
      return Reports.from(responseFieldFromResponse(response, urlField));

    } catch (ProcessingException e) {
      return Reports.from(cannotConnectError(urlField.getPath()));
    }
  }

  /**
   * Attempts to open a connection to a URL.
   *
   * <p>Possible Error Codes to be returned - {@link
   * org.codice.ddf.admin.common.report.message.DefaultMessages#CANNOT_CONNECT}
   *
   * @param urlField {@link UrlField} containing the URL to connect to
   * @return a {@link Report} containing no messages on success, or containing {@link
   *     org.codice.ddf.admin.api.report.ErrorMessage}s on failure.
   */
  public Report<Void> endpointIsReachable(UrlField urlField) {
    URLConnection urlConnection = null;
    try {
      urlConnection = new URL(urlField.getValue()).openConnection();
      urlConnection.setConnectTimeout(CLIENT_TIMEOUT_MILLIS);
      urlConnection.connect();
      LOGGER.debug("Successfully reached {}.", urlField);
    } catch (IOException e) {
      LOGGER.debug("Failed to reach {}, returning an error.", urlField, e);
      return Reports.from(cannotConnectError(urlField.getPath()));
    } finally {
      try {
        if (urlConnection != null) {
          urlConnection.getInputStream().close();
        }
      } catch (IOException e) {
        LOGGER.debug("Error closing connection stream.");
      }
    }
    return Reports.emptyReport();
  }

  public class WebClientBuilder {

    private final WebClient webClient;

    private WebClientBuilder(String url, String username, String password) {
      this(url, username, password, WebClient.class);
    }

    private WebClientBuilder(
        String url, String username, String password, Class clientServiceClass) {
      SecureCxfClientFactory<WebClient> clientFactory =
          StringUtils.isEmpty(username) || StringUtils.isEmpty(password)
              ? new SecureCxfClientFactory<>(url, clientServiceClass)
              : new SecureCxfClientFactory<>(url, clientServiceClass, username, password);

      webClient = clientFactory.getWebClient();
    }

    public WebClientBuilder queryParams(Map<String, Object> queryParams) {
      queryParams.entrySet().forEach(entry -> webClient.query(entry.getKey(), entry.getValue()));

      return this;
    }

    public WebClientBuilder path(String path) {
      webClient.path(path);
      return this;
    }

    public WebClientBuilder contentType(String contentType) {
      webClient.type(contentType);
      return this;
    }

    public WebClientBuilder accept(String... mediaTypes) {
      webClient.accept(mediaTypes);
      return this;
    }

    public WebClientBuilder encoding(String encoding) {
      webClient.encoding(encoding);
      return this;
    }

    public WebClientBuilder acceptEncoding(String... acceptEncodings) {
      webClient.acceptEncoding(acceptEncodings);
      return this;
    }

    public WebClientBuilder header(String header, Object... values) {
      webClient.header(header, values);
      return this;
    }

    public WebClientBuilder headers(Map<String, Object> headers) {
      headers.entrySet().forEach(entry -> webClient.header(entry.getKey(), entry.getValue()));

      return this;
    }

    public WebClient build() {
      return webClient;
    }
  }

  public WebClientBuilder createWebClientBuilder(String url, String username, String password) {
    return new WebClientBuilder(url, username, password);
  }

  public WebClientBuilder createWebClientBuilder(
      String url, String username, String password, Class serviceClass) {
    return new WebClientBuilder(url, username, password, serviceClass);
  }

  private ResponseField responseFieldFromResponse(Response response, UrlField requestUrl) {
    String contentType =
        response.getMediaType() == null ? null : response.getMediaType().toString();

    return new ResponseField()
        .responseBody(response.readEntity(String.class))
        .statusCode(response.getStatus())
        .requestUrlField(requestUrl)
        .contentType(contentType);
  }
}
