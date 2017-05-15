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
package org.codice.ddf.admin.sources.commons.utils;

import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static org.codice.ddf.admin.common.message.DefaultMessages.cannotConnectError;
import static org.codice.ddf.admin.common.message.DefaultMessages.unauthorizedError;
import static org.codice.ddf.admin.common.message.DefaultMessages.unknownEndpointError;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.client.WebClient;
import org.codice.ddf.admin.common.Report;
import org.codice.ddf.admin.common.ReportWithResult;
import org.codice.ddf.admin.common.fields.common.CredentialsField;
import org.codice.ddf.admin.common.fields.common.HostField;
import org.codice.ddf.admin.common.fields.common.UrlField;
import org.codice.ddf.cxf.SecureCxfClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestUtils.class);

    /**
     * Takes a list of url formats, for example "https://%s:%d/wfs", formats them together with the
     * hostField name and port, then sends GET requests to those URLs. If an HTTP 200 and response body is returned on one the the formatted
     * URLs, then a {@link UrlField} whose value is the formatted URL is returned. The {@code UrlField} returned
     * will have the same path and field name as the hostField passed in. On failure, the {@link ReportWithResult}
     * will contain {@link org.codice.ddf.admin.common.message.ErrorMessage}s.
     *
     * @param hostField host field containing the host name and port
     * @param urlFormats list of url formats to format with the hostField
     * @param creds credentials for basic authentication
     * @param queryParams additional query params
     * @return a {@code ReportWithResult} containing a UrlField on success, or {@code ErrorMessage}s on failure
     */
    public ReportWithResult<UrlField> discoverUrlFromHost(HostField hostField, List<String> urlFormats, CredentialsField creds,
            Map<String, String> queryParams) {
        ReportWithResult<UrlField> responseBody = new ReportWithResult<>();
        for(String formatUrl : urlFormats) {
            UrlField clientUrl = new UrlField();
            clientUrl.fieldName(hostField.fieldName());
            clientUrl.updatePath(hostField.path().subList(0, hostField.path().size() - 1));
            clientUrl.setValue(String.format(formatUrl, hostField.name(), hostField.port()));

            ReportWithResult<String> body = sendGetRequest(clientUrl, creds, queryParams);
            if(!body.containsErrorMsgs()) {
                responseBody.result(clientUrl);
                return responseBody;
            } else {
                responseBody.addMessages(body);
            }
        }
        return new ReportWithResult<UrlField>().argumentMessage(responseBody.messages().get(0));
    }

    /**
     * Creates a secure CXF {@code WebClient} and sends a request to the URL given by the clientUrl and
     * optional queryParams. Returns a {@link ReportWithResult} containing the body of the response returned,
     * or containing an {@link org.codice.ddf.admin.common.message.ErrorMessage} if the response was empty
     * or not a HTTP 200.
     *
     * @param clientUrl url to send GET request to
     * @param creds optional credentials for basic authentication
     * @param queryParams optional query parameters
     * @return {@link ReportWithResult} containing the body of the response on success, or containing an {@link org.codice.ddf.admin.common.message.ErrorMessage}
     */
    public ReportWithResult<String> sendGetRequest(UrlField clientUrl, CredentialsField creds, Map<String, String> queryParams) {
        WebClient client = generateClient(clientUrl.getValue(), creds, queryParams);
        ReportWithResult<String> body = new ReportWithResult<>();
        Response response;
        try {
            response = client.get();
        } catch (ProcessingException e) {
            body.argumentMessage(cannotConnectError(clientUrl.path()));
            return body;
        }

        if (response != null) {
            String responseString = response.readEntity(String.class);
            if (response.getStatus() == HTTP_OK && !responseString.equals("")) {
                body.result(responseString);
            } else if(response.getStatus() == HTTP_UNAUTHORIZED) {
                body.argumentMessage(unauthorizedError(creds.path()));
            } else {
                body.argumentMessage(unknownEndpointError(clientUrl.path()));
            }
        }
        return body;
    }

    /**
     * Sends a post request to the specified url.
     *
     * @param urlField    URL to send Post request to
     * @param creds       optional credentials consisting of a username and password
     * @param contentType Mime type of the post body
     * @param content     Body of the post request
     * @return a {@link ReportWithResult} containing the POST request response body or an {@link org.codice.ddf.admin.common.message.ErrorMessage} on failure.
     */
    public ReportWithResult<String> sendPostRequest(UrlField urlField, CredentialsField creds,
            String contentType, String content) {
        WebClient client = generateClient(urlField.getValue(), creds, Collections.emptyMap());
        Response response = client.type(contentType)
                .post(content);

        ReportWithResult<String> responseBodyResult = new ReportWithResult<>();
        if (response.getStatus() != HTTP_OK || response.readEntity(String.class)
                .equals("")) {
            LOGGER.debug("Bad or empty response received from sending POST to {}.",
                    urlField.getValue());
            responseBodyResult.argumentMessage(cannotConnectError(urlField.path()));
            return responseBodyResult;
        }

        responseBodyResult.result(response.readEntity(String.class));
        return responseBodyResult;
    }

    /**
     * Attempts to open a connection to a URL.
     *
     * @param urlField {@link UrlField} containing the URL to connect to
     * @return a {@link Report} containing no messages on success, or containing {@link org.codice.ddf.admin.common.message.ErrorMessage}s on failure.
     */
    public Report endpointIsReachable(UrlField urlField) {
        Report report = new Report();
        try {
            URLConnection urlConnection = (new URL(urlField.getValue()).openConnection());
            urlConnection.setConnectTimeout(500);
            urlConnection.connect();
            LOGGER.debug("Successfully reached {}.", urlField);
        } catch (IOException e) {
            LOGGER.debug("Failed to reach {}, returning an error.", urlField, e);
            report.argumentMessage(cannotConnectError(urlField.path()));
        }
        return report;
    }

    public WebClient generateClient(String url, CredentialsField creds, Map<String, String> queryParams) {
        String username = creds == null ? null : creds.username();
        String password = creds == null ? null : creds.password();
        SecureCxfClientFactory<WebClient> clientFactory =
                username == null && password == null ? new SecureCxfClientFactory<>(url,
                        WebClient.class) : new SecureCxfClientFactory<>(url,
                        WebClient.class,
                        username,
                        password);

        WebClient client = clientFactory.getClient();
        if(queryParams != null) {
            queryParams.entrySet()
                    .forEach(entry -> client.query(entry.getKey(), entry.getValue()));
        }
        return client;
    }
}
