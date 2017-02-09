package org.codice.ddf.admin.commons.requests;

import static java.net.HttpURLConnection.HTTP_OK;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLPeerUnverifiedException;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.codice.ddf.admin.api.handler.ConfigurationMessage;
import org.codice.ddf.admin.api.handler.MessageBuilder;
import org.codice.ddf.admin.api.handler.report.ProbeReport;
import org.codice.ddf.admin.api.handler.report.Report;

import com.google.common.collect.ImmutableMap;
import com.google.common.net.HttpHeaders;

public class RequestUtils {

    //Probe return types
    public static final String CONTENT_TYPE = "contentType";
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

    private static final String HTTPS = "https";
    private static final String NONE = "NONE";
    public static final int PING_TIMEOUT = 500;

    /**
     * Sends a get request to the specified url. It does NOT check the response status code or body.
     * SUCCESS TYPES - EXECUTED_REQUEST
     * WARNING TYPES - UNTRUSTED_CA
     * FAILURE TYPES - CANNOT_CONNECT, CERT_ERROR
     * RETURN TYPES -  CONTENT_TYPE, CONTENT, STATUS_CODE
     *
     * @param url
     * @param userName - username to add to basic auth
     * @param password - user password to add to basic auth
     * @return report
     */
    public static ProbeReport sendGetRequest(String url, String userName, String password) {
        Report connectReport = endpointIsReachable(url);
        if(connectReport.containsFailureMessages()) {
            return new ProbeReport(connectReport.messages());
        }
        HttpGet request = new HttpGet(url);

        if (url.startsWith(HTTPS) && userName != null && password != null) {
            String auth = Base64.getEncoder().encodeToString((userName + ":" + password).getBytes());
            request.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + auth);
        }
        return sendHttpRequest(request);
    }

    /**
     * Sends a post request to the specified url. Does not check response code or body.
     * @param url
     * @param username - username to add to basic auth
     * @param password - password to add to basic auth
     * @param contentType - Mime type of the post body
     * @param content - body of the post request
     * @return report
     */
    public static ProbeReport sendPostRequest(String url, String username, String password,
            String contentType, String content) {
        Report connectReport = endpointIsReachable(url);
        if (connectReport.containsFailureMessages()) {
            return new ProbeReport(connectReport.messages());
        }
        HttpPost post = new HttpPost(url);
        post.setHeader("Content-Type", contentType);
        if (url.startsWith("https") && username != null && password != null) {
            String auth = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
            post.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + auth);
        }
        try {
            post.setEntity(new StringEntity(content));
        } catch (UnsupportedEncodingException e) {
            return new ProbeReport(ConfigurationMessage.createInternalErrorMsg("Error encoding query."));
        }
        return sendHttpRequest(post);
    }

    private static ProbeReport sendHttpRequest(HttpRequestBase request) {
        ProbeReport results = new ProbeReport();
        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;
        try {
            client = getHttpClient(false);
            response = client.execute(request);
            if (response.getStatusLine().getStatusCode() == HTTP_OK) {
                return new ProbeReport().addMessage(createRequestConfigMsg(EXECUTED_REQUEST))
                        .probeResults(responseToMap(response));
            }
            else {
                return new ProbeReport().addMessage(createRequestConfigMsg(CANNOT_CONNECT));
            }
        } catch (SSLPeerUnverifiedException e) {
            return results.addMessage(createRequestConfigMsg(CERT_ERROR));
        } catch (IOException e) {
            closeClientAndResponse(client, response);
            try {
                client = getHttpClient(true);
                response = client.execute(request);
                results.addMessage(createRequestConfigMsg(UNTRUSTED_CA));
                results.addMessage(createRequestConfigMsg(EXECUTED_REQUEST));
                return results.probeResults(responseToMap(response));
            } catch (Exception e1){
                return results.addMessage(createRequestConfigMsg(CANNOT_CONNECT));
            }
        } finally {
            closeClientAndResponse(client, response);
        }
    }

    /**
     * Opens a connection with the specified url.
     * SUCCESS TYPES - REACHED_URL
     * FAILURE_TYPES - CANNOT_CONNECT
     * @param url - url to connect to
     * @return report
     */
    public static Report endpointIsReachable(String url) {
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
     * Opens a connection with the specified url.
     * SUCCESS TYPES - REACHED_URL
     * FAILURE_TYPES - CANNOT_CONNECT
     * @param hostname
     * @param port
     * @return report
     */
    public static Report endpointIsReachable(String hostname, int port) {
        try (Socket connection = new Socket()) {
            connection.connect(new InetSocketAddress(hostname, port), PING_TIMEOUT);
            connection.close();
            return new Report(createRequestConfigMsg(CONNECTED));
        } catch (IOException e) {
            return new Report(createRequestConfigMsg(CANNOT_CONNECT));
        }
    }

    private static CloseableHttpClient getHttpClient(boolean trustAnyCA) {
        HttpClientBuilder builder = HttpClientBuilder.create()
                .disableAutomaticRetries()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(PING_TIMEOUT)
                        .build());
        if (trustAnyCA) {
            try {
                builder.setSSLSocketFactory(new SSLConnectionSocketFactory(SSLContexts.custom()
                        .loadTrustMaterial(null, (chain, authType) -> true)
                        .build()));
            } catch (Exception e) {
                // TODO: tbatie - 2/8/17 - Not sure what we should do here
            }
        }
        return builder.build();
    }

    private static Map<String, Object> responseToMap(CloseableHttpResponse response)
            throws IOException {
        Map<String, Object> requestResults = new HashMap<>();
        requestResults.put(STATUS_CODE, response.getStatusLine().getStatusCode());

        String contentType = response.getEntity().getContentType() == null ?
                NONE : response.getEntity().getContentType().getValue();
        requestResults.put(CONTENT_TYPE, contentType);
        requestResults.put(CONTENT, EntityUtils.toString(response.getEntity()));
        return requestResults;
    }

    private static void closeClientAndResponse(CloseableHttpClient client, CloseableHttpResponse response) {
        try {
            if (client != null) {
                client.close();
            }
            if (response != null) {
                response.close();
            }
        } catch (Exception e) {
        }
    }

    public static Map<String, String> getRequestSubtypeDescriptions(String... subtypeKeys) {
        return REQUEST_MESSAGE_BUILDER.getDescriptions(subtypeKeys);
    }

    public static ConfigurationMessage createRequestConfigMsg(String result){
        return REQUEST_MESSAGE_BUILDER.buildMessage(result);
    }
}
