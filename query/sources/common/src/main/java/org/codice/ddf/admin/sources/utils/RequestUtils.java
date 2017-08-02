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
import org.codice.ddf.admin.common.fields.common.CredentialsField;
import org.codice.ddf.admin.common.fields.common.ResponseField;
import org.codice.ddf.admin.common.fields.common.UrlField;
import org.codice.ddf.admin.common.report.ReportImpl;
import org.codice.ddf.admin.common.report.ReportWithResultImpl;
import org.codice.ddf.cxf.SecureCxfClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestUtils.class);

    private static final int CLIENT_TIMEOUT_MILLIS = 10000;

    /**
     * Creates a secure CXF {@code WebClient} and sends a GET request to the URL given by the clientUrl and
     * optional queryParams.
     * <p>
     * Possible Error Codes to be returned
     * - {@link org.codice.ddf.admin.common.report.message.DefaultMessages#CANNOT_CONNECT}
     *
     * @param requestUrl  url to send GET request to
     * @param creds       optional credentials for basic authentication
     * @param queryParams optional query parameters
     * @return {@link ReportWithResultImpl} containing a {@link ResponseField}, or containing an {@link org.codice.ddf.admin.api.report.ErrorMessage}
     */
    public ReportWithResultImpl<ResponseField> sendGetRequest(UrlField requestUrl,
            CredentialsField creds, Map<String, Object> queryParams) {
        WebClient webClient = createWebClientBuilder(requestUrl.getValue(),
                creds.username(),
                creds.realPassword()).queryParams(queryParams)
                .build();

        return sendGetRequest(webClient, requestUrl);
    }

    /**
     * Sends a GET request with the given {@code WebClient}
     * <p>
     * Possible Error Codes to return
     * - {@link org.codice.ddf.admin.common.report.message.DefaultMessages#CANNOT_CONNECT}
     *
     * @param webClient {@code WebClient} to send a GET request with
     * @param urlField  the original request url
     * @return {@link Response} of the request
     */
    public ReportWithResultImpl<ResponseField> sendGetRequest(WebClient webClient,
            UrlField urlField) {
        ReportWithResultImpl<ResponseField> responseResult = new ReportWithResultImpl<>();
        responseResult.addMessages(endpointIsReachable(urlField));
        if (responseResult.containsErrorMsgs()) {
            return responseResult;
        }

        try {
            Response response = webClient.get();
            responseResult.result(responseFieldFromResponse(response, urlField));
            return responseResult;

        } catch (ProcessingException e) {
            responseResult.addArgumentMessage(cannotConnectError(urlField.path()));
            return responseResult;
        }
    }

    /**
     * Sends a POST request to the specified url.
     * <p>
     * Possible Error Codes to be returned
     * - {@link org.codice.ddf.admin.common.report.message.DefaultMessages#CANNOT_CONNECT}
     *
     * @param urlField    URL to send Post request to
     * @param creds       optional credentials consisting of a username and password
     * @param contentType Mime type of the post body
     * @param content     Body of the post request
     * @return a {@link ReportWithResultImpl} containing a {@link ResponseField} or an {@link org.codice.ddf.admin.api.report.ErrorMessage} on failure.
     */
    public ReportWithResultImpl<ResponseField> sendPostRequest(UrlField urlField,
            CredentialsField creds, String contentType, String content) {
        WebClient webClient = createWebClientBuilder(urlField.getValue(),
                creds.username(),
                creds.realPassword()).contentType(contentType)
                .build();

        return sendPostRequest(webClient, urlField, content);
    }

    /**
     * Sends a POST request with the given {@code WebClient}
     * <p>
     * Possible Error Codes to return
     * - {@link org.codice.ddf.admin.common.report.message.DefaultMessages#CANNOT_CONNECT}
     *
     * @param webClient {@code WebClient} to send POST request with
     * @param urlField  original request url field
     * @param content   Body of the post request
     * @return a {@link ReportWithResultImpl} containing a {@link ResponseField} or an {@link org.codice.ddf.admin.api.report.ErrorMessage} on failure.
     */
    public ReportWithResultImpl<ResponseField> sendPostRequest(WebClient webClient,
            UrlField urlField, String content) {
        ReportWithResultImpl<ResponseField> responseResult = new ReportWithResultImpl<>();
        responseResult.addMessages(endpointIsReachable(urlField));
        if (responseResult.containsErrorMsgs()) {
            return responseResult;
        }

        try {
            Response response = webClient.post(content);
            responseResult.result(responseFieldFromResponse(response, urlField));
            return responseResult;

        } catch (ProcessingException e) {
            responseResult.addArgumentMessage(cannotConnectError(urlField.path()));
            return responseResult;
        }
    }

    /**
     * Attempts to open a connection to a URL.
     * <p>
     * Possible Error Codes to be returned
     * - {@link org.codice.ddf.admin.common.report.message.DefaultMessages#CANNOT_CONNECT}
     *
     * @param urlField {@link UrlField} containing the URL to connect to
     * @return a {@link ReportImpl} containing no messages on success, or containing {@link org.codice.ddf.admin.api.report.ErrorMessage}s on failure.
     */
    public ReportImpl endpointIsReachable(UrlField urlField) {
        ReportImpl report = new ReportImpl();
        URLConnection urlConnection = null;
        try {
            urlConnection = new URL(urlField.getValue()).openConnection();
            urlConnection.setConnectTimeout(CLIENT_TIMEOUT_MILLIS);
            urlConnection.connect();
            LOGGER.debug("Successfully reached {}.", urlField);
        } catch (IOException e) {
            LOGGER.debug("Failed to reach {}, returning an error.", urlField, e);
            report.addArgumentMessage(cannotConnectError(urlField.path()));
        } finally {
            try {
                if (urlConnection != null) {
                    urlConnection.getInputStream()
                            .close();
                }
            } catch (IOException e) {
                LOGGER.debug("Error closing connection stream.");
            }
        }
        return report;
    }

    public class WebClientBuilder {

        private final WebClient webClient;

        private WebClientBuilder(String url, String username, String password) {
            this(url, username, password, WebClient.class);
        }

        private WebClientBuilder(String url, String username, String password,
                Class clientServiceClass) {
            SecureCxfClientFactory<WebClient> clientFactory =
                    StringUtils.isEmpty(username) || StringUtils.isEmpty(password) ?
                            new SecureCxfClientFactory<>(url, clientServiceClass) :
                            new SecureCxfClientFactory<>(url,
                                    clientServiceClass,
                                    username,
                                    password);

            webClient = clientFactory.getWebClient();
        }

        public WebClientBuilder queryParams(Map<String, Object> queryParams) {
            queryParams.entrySet()
                    .forEach(entry -> webClient.query(entry.getKey(), entry.getValue()));

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
            headers.entrySet()
                    .forEach(entry -> webClient.header(entry.getKey(), entry.getValue()));

            return this;
        }

        public WebClient build() {
            return webClient;
        }
    }

    public WebClientBuilder createWebClientBuilder(String url, String username, String password) {
        return new WebClientBuilder(url, username, password);
    }

    public WebClientBuilder createWebClientBuilder(String url, String username, String password,
            Class serviceClass) {
        return new WebClientBuilder(url, username, password, serviceClass);
    }

    private ResponseField responseFieldFromResponse(Response response, UrlField requestUrl) {
        String contentType = response.getMediaType() == null ?
                null :
                response.getMediaType()
                        .toString();

        return new ResponseField().responseBody(response.readEntity(String.class))
                .statusCode(response.getStatus())
                .requestUrlField(requestUrl)
                .contentType(contentType);
    }
}
