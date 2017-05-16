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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.client.WebClient;
import org.codice.ddf.admin.api.action.Message;
import org.codice.ddf.admin.common.fields.common.CredentialsField;
import org.codice.ddf.admin.common.fields.common.UrlField;
import org.codice.ddf.cxf.SecureCxfClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestUtils.class);

    public static final String CONTENT = "content";

    public static final String STATUS_CODE = "statusCode";

    private static final int PING_TIMEOUT = 500;

    /**
     * Sends a get request to the specified URL. It does NOT check the response status code or body.
     *
     * @param urlField contains the URL to send the get request to
     * @param creds    option credentials consisting of a username and password
     * @return A {@link DiscoveredUrl} containing containing a discovered URL on success,
     * or an {@link org.codice.ddf.admin.common.message.ErrorMessage} on failure.
     */
    public DiscoveredUrl sendGetRequest(UrlField urlField, CredentialsField creds,
            Map<String, String> queryParams) {
        WebClient client = generateClient(urlField, creds);

        for (Map.Entry<String, String> e : queryParams.entrySet()) {
            client.query(e.getKey(), e.getValue());
        }

        Response response;
        try {
            response = client.get();
        } catch(ProcessingException e) {
            LOGGER.debug("Processing exception while sending GET request to [{}].", urlField.getValue(), e);
            return new DiscoveredUrl(Collections.singletonList(cannotConnectError(urlField.fieldName())));
        }

        if (response.getStatus() != HTTP_OK || response.readEntity(String.class)
                .equals("")) {
            LOGGER.debug("Bad or empty response received from sending GET to {}.",
                    urlField.getValue());
            return new DiscoveredUrl(Collections.singletonList(cannotConnectError(urlField.fieldName())));
        }
        return new DiscoveredUrl(responseToMap(response));
    }

    /**
     * Sends a post request to the specified url. Does not check response code or body.
     *
     * @param urlField    URL to send Post request to
     * @param creds       optional credentials consisting of a username and password
     * @param contentType Mime type of the post body
     * @param content     Body of the post request
     * @return A {@link DiscoveredUrl} containing containing a discovered URL on success,
     * or an {@link org.codice.ddf.admin.common.message.ErrorMessage} on failure.
     */
    public DiscoveredUrl sendPostRequest(UrlField urlField, CredentialsField creds,
            String contentType, String content) {
        WebClient client = generateClient(urlField, creds);

        Response response = client.type(contentType)
                .post(content);

        if (response.getStatus() != HTTP_OK || response.readEntity(String.class)
                .equals("")) {
            LOGGER.debug("Bad or empty response received from sending POST to {}.", urlField.getValue());
            return new DiscoveredUrl(Collections.singletonList(cannotConnectError(urlField.fieldName())));
        }

        return new DiscoveredUrl(responseToMap(response));
    }

    /**
     * Attempts to open a connection to a URL.
     *
     * @param urlField {@link UrlField} containing the URL to connect to
     * @return A {@link DiscoveredUrl} containing containing a discovered URL on success,
     * or an {@link org.codice.ddf.admin.common.message.ErrorMessage} on failure.
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
            errors.add(cannotConnectError(urlField.fieldName()));
        }
        return errors;
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

    protected Map<String, Object> responseToMap(Response response) {
        Map<String, Object> requestResults = new HashMap<>();
        requestResults.put(STATUS_CODE, response.getStatus());
        requestResults.put(CONTENT, response.readEntity(String.class));
        return requestResults;
    }
}
