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
package org.codice.ddf.admin.sources.wfs;

import static java.net.HttpURLConnection.HTTP_OK;
import static org.codice.ddf.admin.api.handler.commons.SourceHandlerCommons.PING_TIMEOUT;
import static org.codice.ddf.admin.api.handler.commons.SourceHandlerCommons.SOURCES_NAMESPACE_CONTEXT;
import static org.codice.ddf.admin.api.services.WfsServiceProperties.WFS1_FACTORY_PID;
import static org.codice.ddf.admin.api.services.WfsServiceProperties.WFS2_FACTORY_PID;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

import javax.net.ssl.SSLPeerUnverifiedException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContexts;
import org.codice.ddf.admin.api.config.sources.WfsSourceConfiguration;
import org.codice.ddf.admin.api.handler.commons.UrlAvailability;
import org.w3c.dom.Document;

import com.google.common.collect.ImmutableList;
import com.google.common.net.HttpHeaders;

public class WfsSourceUtils {

    public static final String GET_CAPABILITIES_PARAMS = "?service=WFS&request=GetCapabilities";

    private static final List<String> WFS_MIME_TYPES = ImmutableList.of("text/xml",
            "application/xml");

    private static final String ACCEPT_VERSION_PARAMS = "&AcceptVersions=2.0.0,1.0.0";

    private static final List<String> URL_FORMATS = ImmutableList.of("https://%s:%d/services/wfs",
            "https://%s:%d/wfs");
    //TODO: Add these when enabling http is an option
//            "http://%s:%d/services/wfs",
//            "http://%s:%d/wfs");

    public UrlAvailability confirmEndpointUrl(WfsSourceConfiguration config) {
        Optional<UrlAvailability> result = URL_FORMATS.stream()
                .map(formatUrl -> String.format(formatUrl,
                        config.sourceHostName(),
                        config.sourcePort()))
                .map(url -> getUrlAvailability(url, config.sourceUserName(), config.sourceUserPassword()))
                .filter(avail -> avail.isAvailable() || avail.isCertError())
                .findFirst();
        return result.isPresent() ? result.get() : null;
    }

    public UrlAvailability getUrlAvailability(String url, String un, String pw) {
        UrlAvailability result = new UrlAvailability(url);
        int status;
        String contentType;
        url += GET_CAPABILITIES_PARAMS;
        HttpGet request = new HttpGet(url);
        if (un != null && pw != null) {
            byte[] auth = Base64.encodeBase64((un + ":" + pw).getBytes());
            request.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + new String(auth));
        }
        try {
            HttpResponse response = getCloseableHttpClient(false).execute(request);
            status = response.getStatusLine()
                    .getStatusCode();
            contentType = response.getEntity()
                    .getContentType()
                    .getValue();
            if (status == HTTP_OK && WFS_MIME_TYPES.contains(contentType)) {
                return result.trustedCertAuthority(true)
                        .certError(false)
                        .available(true);
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
                if (status == HTTP_OK && WFS_MIME_TYPES.contains(contentType)) {
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

    public Optional<WfsSourceConfiguration> getPreferredConfig(
            WfsSourceConfiguration configuration) {
        WfsSourceConfiguration config = new WfsSourceConfiguration(configuration);
        String wfsVersionExp = "/wfs:WFS_Capabilities/attribute::version";
        HttpGet getCapabilitiesRequest = new HttpGet(
                config.endpointUrl() + GET_CAPABILITIES_PARAMS + ACCEPT_VERSION_PARAMS);
        if (config.sourceUserName() != null && config.sourceUserPassword() != null) {
            byte[] auth = Base64.encodeBase64((config.sourceUserName() + ":" + config.sourceUserPassword()).getBytes());
            getCapabilitiesRequest.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + new String(auth));
        }
        XPath xpath = XPathFactory.newInstance()
                .newXPath();
        xpath.setNamespaceContext(SOURCES_NAMESPACE_CONTEXT);
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document capabilitiesXml = builder.parse(getCloseableHttpClient(true).execute(getCapabilitiesRequest)
                    .getEntity()
                    .getContent());
            String wfsVersion = xpath.compile(wfsVersionExp)
                    .evaluate(capabilitiesXml);
            switch (wfsVersion) {
            case "2.0.0":
                return Optional.of((WfsSourceConfiguration) config.factoryPid(WFS2_FACTORY_PID));
            case "1.0.0":
                return Optional.of((WfsSourceConfiguration) config.factoryPid(WFS1_FACTORY_PID));
            default:
                return Optional.empty();
            }
        } catch (Exception e) {
            return Optional.empty();
        }
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
