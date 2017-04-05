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
package org.codice.ddf.admin.commons.requests;

import static java.net.HttpURLConnection.HTTP_OK;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.client.WebClient;
import org.codice.ddf.admin.api.handler.ConfigurationMessage;
import org.codice.ddf.admin.api.handler.MessageBuilder;
import org.codice.ddf.admin.api.handler.report.ProbeReport;
import org.codice.ddf.admin.api.handler.report.Report;
import org.codice.ddf.cxf.SecureCxfClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;

public class RequestUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestUtils.class);

    //Probe return types
    public static final String CONTENT = "content";
    public static final String STATUS_CODE = "statusCode";

    //Success types
    public static final String EXECUTED_REQUEST = "EXECUTED_REQUEST";
    public static final String CONNECTED = "CONNECTED";
    private static final Map<String, String> SUCCESS_DESCRIPTIONS = ImmutableMap.of(
            CONNECTED, "Successfully established a connection with the endpoint.",
            EXECUTED_REQUEST, "A request was successfully sent and a response was received from the endpoint.");

    //Failure types
    public static final String CERT_ERROR = "CERT_ERROR";
    public static final String CANNOT_CONNECT = "CANNOT_CONNECT";
    private static final Map<String, String> FAILURE_DESCRIPTIONS = ImmutableMap.of(
            CANNOT_CONNECT, "The URL provided could not be reached.",
            CERT_ERROR, "The discovered source has an incorrectly configured SSL certificate. (Certificate name does not match host)");

    //Warning types
    public static final String UNTRUSTED_CA = "UNTRUSTED_CA";
    public static final Map<String, String> WARNING_DESCRIPTIONS = ImmutableMap.of(UNTRUSTED_CA,
            "The discovered URL has an incorrectly configured SSL certificate and is likely insecure. (Self signed certificate)");

    private static final MessageBuilder REQUEST_MESSAGE_BUILDER = new MessageBuilder(
            SUCCESS_DESCRIPTIONS,
            FAILURE_DESCRIPTIONS,
            WARNING_DESCRIPTIONS);

    public static final int PING_TIMEOUT = 500;

    /**
     * Sends a get request to the specified URL. It does NOT check the response status code or body.
     * SUCCESS TYPES - EXECUTED_REQUEST
     * WARNING TYPES - UNTRUSTED_CA
     * FAILURE TYPES - CANNOT_CONNECT, CERT_ERROR
     * RETURN TYPES -  CONTENT_TYPE, CONTENT, STATUS_CODE
     * @param url URL to send Get request to
     * @param userName - Optional username to add to Basic Auth header
     * @param password - Optional password to add to Basic Auth header
     * @return report
     */
    public ProbeReport sendGetRequest(String url, String userName, String password, Map<String, String> queryParams) {

        WebClient client = generateClient(url, userName, password);

        for (Map.Entry<String, String> e : queryParams.entrySet()) {
            client.query(e.getKey(), e.getValue());
        }
        Response r = client.get();

        if (r.getStatus() != HTTP_OK || r.readEntity(String.class).equals("")) {
            LOGGER.debug("Bad or empty response sending GET to {}.", url);
            return new ProbeReport().addMessage(createRequestConfigMsg(CANNOT_CONNECT));
        }
        return new ProbeReport().addMessage(createRequestConfigMsg(EXECUTED_REQUEST))
                .probeResults(responseToMap(r));
    }

    /**
     * Sends a post request to the specified url. Does not check response code or body.
     * @param url URL to send Post request to
     * @param username - Optional username to add to Basic Auth header
     * @param password - Optional password to add to Basic Auth header
     * @param contentType - Mime type of the post body
     * @param content - Body of the post request
     * @return report
     */
    public ProbeReport sendPostRequest(String url, String username, String password, String contentType, String content) {
        WebClient client = generateClient(url, username, password);

        Response r = client.type(contentType).post(content);
        if (r.getStatus() != HTTP_OK || r.readEntity(String.class).equals("")) {
            LOGGER.debug("Bad or empty response POST to {}.", url);
            return new ProbeReport().addMessage(createRequestConfigMsg(CANNOT_CONNECT));
        }
        return new ProbeReport().addMessage(createRequestConfigMsg(EXECUTED_REQUEST))
                .probeResults(responseToMap(r));
    }

    private WebClient generateClient(String url, String username, String password) {
        SecureCxfClientFactory<WebClient> clientFactory =  username == null && password == null ?
                new SecureCxfClientFactory<>(url, WebClient.class) :
                new SecureCxfClientFactory<>(url, WebClient.class, username, password);
        return clientFactory.getClient();
    }

    /**
     * Opens a connection with the specified url.
     * SUCCESS TYPES - REACHED_URL
     * FAILURE_TYPES - CANNOT_CONNECT
     * @param url - URL to connect to
     * @return report
     */
    public Report endpointIsReachable(String url) {
        try {
            URLConnection urlConnection = (new URL(url).openConnection());
            urlConnection.setConnectTimeout(PING_TIMEOUT);
            urlConnection.connect();
            return new Report(createRequestConfigMsg(CONNECTED));
        } catch (IOException e) {
            return new Report(createRequestConfigMsg(CANNOT_CONNECT));
        }
    }

    /**
     * Opens a connection with the specified hostname and port.
     * SUCCESS TYPES - REACHED_URL
     * FAILURE_TYPES - CANNOT_CONNECT
     * @param hostname Host to attempt to connect to
     * @param port Port over which to connect
     * @return report
     */
    public Report endpointIsReachable(String hostname, int port) {
        try (Socket connection = new Socket()) {
            connection.connect(new InetSocketAddress(hostname, port), PING_TIMEOUT);
            connection.close();
            return new Report(createRequestConfigMsg(CONNECTED));
        } catch (IOException e) {
            return new Report(createRequestConfigMsg(CANNOT_CONNECT));
        }
    }

    protected Map<String, Object> responseToMap(Response response) {
        Map<String, Object> requestResults = new HashMap<>();
        requestResults.put(STATUS_CODE, response.getStatus());
        requestResults.put(CONTENT, response.readEntity(String.class));
        return requestResults;
    }

    public Map<String, String> getRequestSubtypeDescriptions(String... subtypeKeys) {
        return REQUEST_MESSAGE_BUILDER.getDescriptions(subtypeKeys);
    }

    public ConfigurationMessage createRequestConfigMsg(String result) {
        return REQUEST_MESSAGE_BUILDER.buildMessage(result);
    }
}
