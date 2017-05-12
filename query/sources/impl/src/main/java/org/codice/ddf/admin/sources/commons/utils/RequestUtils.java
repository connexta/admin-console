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
import static org.codice.ddf.admin.common.message.DefaultMessages.cannotConnectError;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.client.WebClient;
import org.codice.ddf.admin.common.Report;
import org.codice.ddf.admin.common.ReportWithResult;
import org.codice.ddf.admin.common.fields.common.CredentialsField;
import org.codice.ddf.admin.common.fields.common.UrlField;
import org.codice.ddf.cxf.SecureCxfClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestUtils.class);

    /**
     * Sends a get request to the specified URL and returns the content of the response.
     *
     * @param urlField contains the URL to send the get request to
     * @param creds    option credentials consisting of a username and password
     * @return a {@link ReportWithResult} containing the GET request response body or an {@link org.codice.ddf.admin.common.message.ErrorMessage} on failure.
     */
    public ReportWithResult<String> sendGetRequest(UrlField urlField, CredentialsField creds,
            Map<String, String> queryParams) {
        WebClient client = generateClient(urlField, creds);
        queryParams.entrySet()
                .forEach(entry -> client.query(entry.getKey(), entry.getValue()));

        Response response;
        ReportWithResult<String> responseBody = new ReportWithResult<>();
        try {
            response = client.get();
        } catch (ProcessingException e) {
            LOGGER.debug("Processing exception while sending GET request to [{}].",
                    urlField.getValue(),
                    e);
            responseBody.argumentMessage(cannotConnectError(urlField.path()));
            return responseBody;
        }

        String responseString = response.readEntity(String.class);
        if (response.getStatus() != HTTP_OK || responseString.equals("")) {
            LOGGER.debug("Bad or empty response received from sending GET to {}.",
                    urlField.getValue());
            responseBody.argumentMessage(cannotConnectError(urlField.path()));
            return responseBody;
        }
        responseBody.result(responseString);
        return responseBody;
    }

    /**
     * Sends a post request to the specified url. Does not check response code or body.
     *
     * @param urlField    URL to send Post request to
     * @param creds       optional credentials consisting of a username and password
     * @param contentType Mime type of the post body
     * @param content     Body of the post request
     * @return a {@link ReportWithResult} containing the POST request response body or an {@link org.codice.ddf.admin.common.message.ErrorMessage} on failure.
     */
    public ReportWithResult<String> sendPostRequest(UrlField urlField, CredentialsField creds,
            String contentType, String content) {
        WebClient client = generateClient(urlField, creds);
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

    private WebClient generateClient(UrlField url, CredentialsField creds) {
        String username = creds == null ? null : creds.username();
        String password = creds == null ? null : creds.password();

        SecureCxfClientFactory<WebClient> clientFactory =
                username == null && password == null ? new SecureCxfClientFactory<>(url.getValue(),
                        WebClient.class) : new SecureCxfClientFactory<>(url.getValue(),
                        WebClient.class,
                        username,
                        password);
        return clientFactory.getClient();
    }
}
