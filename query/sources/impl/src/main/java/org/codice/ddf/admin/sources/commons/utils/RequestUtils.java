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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.client.WebClient;
import org.codice.ddf.admin.api.action.Message;
import org.codice.ddf.admin.common.Result;
import org.codice.ddf.admin.common.fields.common.CredentialsField;
import org.codice.ddf.admin.common.fields.common.UrlField;
import org.codice.ddf.cxf.SecureCxfClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestUtils.class);

    private static final int PING_TIMEOUT = 500;

    private static final Integer CXF_CLIENT_TIMEOUT = 10000;

    /**
     * Sends a get request to the specified URL and returns the content of the response.
     *
     * @param urlField contains the URL to send the get request to
     * @param creds    option credentials consisting of a username and password
     * @return a {@link Result} containing the GET request response body or an {@link org.codice.ddf.admin.common.message.ErrorMessage} on failure.
     */
    public Result<String> sendGetRequest(UrlField urlField, CredentialsField creds,
            Map<String, String> queryParams) {
        WebClient client = generateClient(urlField, creds);
        queryParams.entrySet()
                .forEach(entry -> client.query(entry.getKey(), entry.getValue()));

        Response response;
        Result<String> responseBody = new Result<>();
        try {
            response = client.get();
        } catch (ProcessingException e) {
            // TODO: 4/27/17 phuffer - figure out how cert errors are handled here
            LOGGER.debug("Processing exception while sending GET request to [{}].",
                    urlField.getValue(),
                    e);
            responseBody.argumentMessage(cannotConnectError(urlField.path()));
            return responseBody;
        }

        if (response.getStatus() != HTTP_OK || response.readEntity(String.class)
                .equals("")) {
            LOGGER.debug("Bad or empty response received from sending GET to {}.",
                    urlField.getValue());
            responseBody.argumentMessage(cannotConnectError(urlField.path()));
            return responseBody;
        }
        return responseBody.value(response.readEntity(String.class));
    }

    /**
     * Sends a post request to the specified url. Does not check response code or body.
     *
     * @param urlField    URL to send Post request to
     * @param creds       optional credentials consisting of a username and password
     * @param contentType Mime type of the post body
     * @param content     Body of the post request
     * @return a {@link Result} containing the POST request response body or an {@link org.codice.ddf.admin.common.message.ErrorMessage} on failure.
     */
    public Result<String> sendPostRequest(UrlField urlField, CredentialsField creds,
            String contentType, String content) {
        WebClient client = generateClient(urlField, creds);
        Response response = client.type(contentType)
                .post(content);

        Result<String> responseBodyResult = new Result<>();
        if (response.getStatus() != HTTP_OK || response.readEntity(String.class)
                .equals("")) {
            LOGGER.debug("Bad or empty response received from sending POST to {}.",
                    urlField.getValue());
            responseBodyResult.argumentMessage(cannotConnectError(urlField.path()));
            return responseBodyResult;
        }

        return responseBodyResult.value(response.readEntity(String.class));
    }

    /**
     * Attempts to open a connection to a URL.
     *
     * @param urlField {@link UrlField} containing the URL to connect to
     * @return an empty {@code List} on success, or {@link org.codice.ddf.admin.common.message.ErrorMessage}s on failure.
     */
    public List<Message> endpointIsReachable(UrlField urlField) {
        List<Message> errors = new ArrayList<>();
        try {
            URLConnection urlConnection = (new URL(urlField.getValue()).openConnection());
            urlConnection.setConnectTimeout(PING_TIMEOUT);
            urlConnection.connect();
            LOGGER.debug("Successfully reached {}.", urlField);
        } catch (IOException e) {
            LOGGER.debug("Failed to reach {}, returning an error.", urlField, e);
            errors.add(cannotConnectError(urlField.path()));
        }
        return errors;
    }

    // TODO: 4/28/17 phuffer - explore creating a client with disableCnCheck to true if first attempt at discovery fails
    private WebClient generateClient(UrlField url, CredentialsField creds) {
        String username = creds == null ? null : creds.username();
        String password = creds == null ? null : creds.password();

        SecureCxfClientFactory<WebClient> clientFactory =
                username == null && password == null ? new SecureCxfClientFactory<>(url.getValue(),
                        WebClient.class,
                        null,
                        null,
                        false,
                        false,
                        CXF_CLIENT_TIMEOUT,
                        CXF_CLIENT_TIMEOUT) : new SecureCxfClientFactory<>(url.getValue(),
                        WebClient.class,
                        null,
                        null,
                        false,
                        false,
                        CXF_CLIENT_TIMEOUT,
                        CXF_CLIENT_TIMEOUT,
                        username,
                        password);
        return clientFactory.getClient();
    }
}
