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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.client.WebClient;
import org.codice.ddf.admin.common.fields.common.CredentialsField;
import org.codice.ddf.admin.common.fields.common.HostField;
import org.codice.ddf.admin.common.fields.common.ResponseField;
import org.codice.ddf.admin.common.fields.common.UrlField;
import org.codice.ddf.admin.common.report.ReportImpl;
import org.codice.ddf.admin.common.report.ReportWithResultImpl;
import org.codice.ddf.cxf.SecureCxfClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestUtils.class);

    private static final int CLIENT_TIMEOUT_MILLIS =
            new Long(TimeUnit.SECONDS.toMillis(10)).intValue();

    /**
     * Takes a list of url formats, for example "https://%s:%d/wfs", formats them together with the
     * hostField name and port, then sends GET requests to those URLs. If a request URL is returned in the
     * {@code ResponseField}, it will not have the same path as the {@code hostField}.
     * <p>
     * Possible Error Codes to be returned
     * - {@link org.codice.ddf.admin.common.report.message.DefaultMessages#CANNOT_CONNECT}
     *
     * @param hostField   host field containing the host name and port
     * @param urlFormats  list of url formats to format with the hostField
     * @param creds       credentials for basic authentication
     * @param queryParams additional query params
     * @return a {@link ReportWithResultImpl} containing a {@link ResponseField} on success, or {@link org.codice.ddf.admin.api.report.ErrorMessage}s on failure
     */
    public ReportWithResultImpl<ResponseField> discoverUrlFromHost(HostField hostField,
            List<String> urlFormats, CredentialsField creds, Map<String, String> queryParams) {
        for (String formatUrl : urlFormats) {
            UrlField clientUrl = new UrlField();
            clientUrl.setValue(String.format(formatUrl, hostField.hostname(), hostField.port()));

            ReportWithResultImpl<ResponseField> responseReport = sendGetRequest(clientUrl,
                    creds,
                    queryParams);
            if (!responseReport.containsErrorMsgs()) {
                return responseReport;
            }
        }

        return new ReportWithResultImpl<ResponseField>().addArgumentMessage(cannotConnectError(
                hostField.path()));
    }

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
            CredentialsField creds, Map<String, String> queryParams) {
        ReportWithResultImpl<ResponseField> responseResult = new ReportWithResultImpl<>();
        responseResult.addMessages(endpointIsReachable(requestUrl));
        if (responseResult.containsErrorMsgs()) {
            return responseResult;
        }

        ReportWithResultImpl<Response> httpResponse = executeGetRequest(requestUrl,
                creds,
                queryParams);
        if (httpResponse.containsErrorMsgs()) {
            responseResult.addMessages(httpResponse);
            return responseResult;
        }

        Response response = httpResponse.result();
        ResponseField responseField =
                new ResponseField().responseBody(response.readEntity(String.class))
                        .statusCode(response.getStatus())
                        .requestUrlField(requestUrl);

        ReportWithResultImpl<ResponseField> result = new ReportWithResultImpl<>();
        result.result(responseField);
        return result;
    }

    /**
     * Executes a request by creating a Secure CXF Client from the provided url, credentials, and query params.
     * <p>
     * Possible Error Codes to return
     * - {@link org.codice.ddf.admin.common.report.message.DefaultMessages#CANNOT_CONNECT}
     *
     * @param clientUrl   url to send GET request to
     * @param creds       optional basic authentication
     * @param queryParams additional query parameters
     * @return {@link Response} of the request
     */
    public ReportWithResultImpl<Response> executeGetRequest(UrlField clientUrl,
            CredentialsField creds, Map<String, String> queryParams) {
        WebClient client = generateClient(clientUrl.getValue(), creds, queryParams);
        ReportWithResultImpl<Response> report = new ReportWithResultImpl<>();
        Response response;
        try {
            response = client.get();
        } catch (ProcessingException e) {
            report.addResultMessage(cannotConnectError());
            return report;
        }

        report.result(response);
        return report;
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
        ReportWithResultImpl<ResponseField> responseResult = new ReportWithResultImpl<>();
        responseResult.addMessages(endpointIsReachable(urlField));
        if (responseResult.containsErrorMsgs()) {
            return responseResult;
        }

        WebClient client = generateClient(urlField.getValue(), creds, Collections.emptyMap());
        Response response = client.type(contentType)
                .post(content);

        ResponseField responseField = new ResponseField().statusCode(response.getStatus())
                .responseBody(response.readEntity(String.class))
                .requestUrlField(urlField);

        responseResult.result(responseField);
        return responseResult;
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

    private WebClient generateClient(String url, CredentialsField creds,
            Map<String, String> queryParams) {
        String username = creds == null ? null : creds.username();
        String password = creds == null ? null : creds.password();
        SecureCxfClientFactory<WebClient> clientFactory =
                username == null && password == null ? new SecureCxfClientFactory<>(url,
                        WebClient.class) : new SecureCxfClientFactory<>(url,
                        WebClient.class,
                        username,
                        password);

        WebClient client = clientFactory.getClient();
        if (queryParams != null) {
            queryParams.entrySet()
                    .forEach(entry -> client.query(entry.getKey(), entry.getValue()));
        }
        return client;
    }
}
