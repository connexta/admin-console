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
package org.codice.ddf.admin.sources.opensearch;

import static java.net.HttpURLConnection.HTTP_OK;
import static org.codice.ddf.admin.api.handler.commons.SourceHandlerCommons.PING_TIMEOUT;
import static org.codice.ddf.admin.api.handler.commons.SourceHandlerCommons.SOURCES_NAMESPACE_CONTEXT;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

import javax.net.ssl.SSLPeerUnverifiedException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContexts;
import org.codice.ddf.admin.api.config.sources.OpenSearchSourceConfiguration;
import org.codice.ddf.admin.api.handler.commons.UrlAvailability;
import org.w3c.dom.Document;

import com.google.common.collect.ImmutableList;
import com.google.common.net.HttpHeaders;

public class OpenSearchSourceUtils {

    private static final List<String> OPENSEARCH_MIME_TYPES = ImmutableList.of(
            "application/atom+xml");

    private static final List<String> URL_FORMATS = ImmutableList.of(
            "https://%s:%d/services/catalog/query",
            "https://%s:%d/catalog/query");
    // TODO: add these when allowing http is an option
//            "http://%s:%d/services/catalog/query",
//            "http://%s:%d/catalog/query");

    private static final String SIMPLE_QUERY_PARAMS = "?q=test&mr=1";

    private static final String TOTAL_RESULTS_XPATH = "//os:totalResults|//opensearch:totalResults";

    //Given a config, returns the correct URL format for the endpoint if one exists
    public UrlAvailability confirmEndpointUrl(OpenSearchSourceConfiguration config) {
        Optional<UrlAvailability> result = URL_FORMATS.stream()
                .map(formatUrl -> String.format(formatUrl,
                        config.sourceHostName(),
                        config.sourcePort()))
                .map(url -> getUrlAvailability(url, config.sourceUserName(), config.sourceUserPassword()))
                .filter(avail -> avail.isAvailable() || avail.isCertError())
                .findFirst();
        return result.isPresent() ? result.get() : null;
    }

    // Given a configuration with and endpointUrl, determines if that URL is available as an OS source
    public UrlAvailability getUrlAvailability(String url, String un, String pw) {
        UrlAvailability result = new UrlAvailability(url);
        boolean queryResponse;
        int status;
        String contentType;
        HttpGet request = new HttpGet(url + SIMPLE_QUERY_PARAMS);
        if (un != null && pw != null) {
            byte[] auth = Base64.encodeBase64((un + ":" + pw).getBytes());
            request.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + new String(auth));
        }
        XPath xpath = XPathFactory.newInstance().newXPath();
        xpath.setNamespaceContext(SOURCES_NAMESPACE_CONTEXT);
        try {
            HttpResponse response = getCloseableHttpClient(false).execute(request);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document responseXml = builder.parse(response
                    .getEntity()
                    .getContent());
            queryResponse = (Boolean) xpath.compile(TOTAL_RESULTS_XPATH).evaluate(responseXml, XPathConstants.BOOLEAN);
            status = response.getStatusLine().getStatusCode();
            contentType = response.getEntity().getContentType().getValue();
            if (status == HTTP_OK && OPENSEARCH_MIME_TYPES.contains(contentType) && queryResponse) {
                return result.trustedCertAuthority(true).certError(false).available(true);
            } else {
                return result.trustedCertAuthority(true)
                        .certError(false)
                        .available(false);
            }
        } catch (SSLPeerUnverifiedException e) {
            // This is the hostname != cert name case - if this occurs, the URL's SSL cert configuration
            // is incorrect, or a serious network security issue has occurred.
            return result.trustedCertAuthority(false)
                    .certError(true)
                    .available(false);
        } catch (Exception e) {
            try {
                HttpResponse response = getCloseableHttpClient(true).execute(request);
                status = response.getStatusLine()
                        .getStatusCode();
                contentType = response.getEntity()
                        .getContentType()
                        .getValue();
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                factory.setNamespaceAware(true);
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document responseXml = builder.parse(response
                        .getEntity()
                        .getContent());
                queryResponse = (Boolean) xpath.compile(TOTAL_RESULTS_XPATH).evaluate(responseXml, XPathConstants.BOOLEAN);
                if (status == HTTP_OK && OPENSEARCH_MIME_TYPES.contains(contentType) && queryResponse) {
                    return result.trustedCertAuthority(false)
                            .certError(false)
                            .available(true);
                }
            } catch (Exception e1) {
                return result.trustedCertAuthority(false)
                        .certError(false)
                        .available(false);
            }
        }
        return result;
    }

    CloseableHttpClient getCloseableHttpClient(boolean trustAnyCA)
            throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        HttpClientBuilder builder = HttpClientBuilder.create().setDefaultRequestConfig(
                RequestConfig.custom().setConnectTimeout(PING_TIMEOUT).build());
        if (trustAnyCA) {
            builder.setSSLSocketFactory(new SSLConnectionSocketFactory(SSLContexts.custom()
                    .loadTrustMaterial(null, (chain, authType) -> true)
                    .build()));
        }
        return builder.build();
    }
}
