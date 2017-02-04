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
package org.codice.ddf.admin.sources.csw;

import static java.net.HttpURLConnection.HTTP_OK;
import static org.codice.ddf.admin.api.handler.commons.SourceHandlerCommons.SOURCES_NAMESPACE_CONTEXT;
import static org.codice.ddf.admin.api.services.CswServiceProperties.CSW_GMD_FACTORY_PID;
import static org.codice.ddf.admin.api.services.CswServiceProperties.CSW_PROFILE_FACTORY_PID;
import static org.codice.ddf.admin.api.services.CswServiceProperties.CSW_SPEC_FACTORY_PID;
import static org.codice.ddf.admin.sources.SourcesCommons.closeClientAndResponse;
import static org.codice.ddf.admin.sources.SourcesCommons.getCloseableHttpClient;

import java.util.List;
import java.util.Optional;

import javax.net.ssl.SSLPeerUnverifiedException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.codice.ddf.admin.api.config.sources.CswSourceConfiguration;
import org.codice.ddf.admin.api.handler.commons.UrlAvailability;
import org.w3c.dom.Document;

import com.google.common.collect.ImmutableList;
import com.google.common.net.HttpHeaders;

public class CswSourceUtils {

    public static final String GET_CAPABILITIES_PARAMS = "?service=CSW&request=GetCapabilities";

    private static final List<String> URL_FORMATS = ImmutableList.of("https://%s:%d/services/csw",
            "https://%s:%d/csw",
            "http://%s:%d/services/csw",
            "http://%s:%d/csw");

    private static final List<String> CSW_MIME_TYPES = ImmutableList.of("text/xml",
            "application/xml");

    private static final String GMD_OUTPUT_SCHEMA = "http://www.isotc211.org/2005/gmd";

    private static final String HAS_CATALOG_METACARD_EXP =
            "//ows:OperationsMetadata//ows:Operation[@name='GetRecords']/ows:Parameter[@name='OutputSchema' or @name='outputSchema']/ows:Value/text()='urn:catalog:metacard'";

    private static final String HAS_GMD_ISO_EXP =
            "//ows:OperationsMetadata/ows:Operation[@name='GetRecords']/ows:Parameter[@name='OutputSchema' or @name='outputSchema']/ows:Value/text()='http://www.isotc211.org/2005/gmd'";

    private static final String GET_FIRST_OUTPUT_SCHEMA =
            "//ows:OperationsMetadata/ows:Operation[@name='GetRecords']/ows:Parameter[@name='OutputSchema' or @name='outputSchema']/ows:Value[1]/text()";

    // Given a config with an endpoint URL, determines if that URL is a functional CSW endpoint.
    public UrlAvailability getUrlAvailability(String url, String un, String pw) {
        UrlAvailability result = new UrlAvailability(url);
        String contentType;
        int status;
        url += GET_CAPABILITIES_PARAMS;
        HttpGet request = new HttpGet(url);
        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;
        if (url.startsWith("https") && un != null && pw != null) {
            byte[] auth = Base64.encodeBase64((un + ":" + pw).getBytes());
            request.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + new String(auth));
        }
        try {
            client = getCloseableHttpClient(false);
            response = client.execute(request);
            status = response.getStatusLine().getStatusCode();
            contentType = response.getEntity().getContentType().getValue();
            if (status == HTTP_OK && CSW_MIME_TYPES.contains(contentType)) {
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
                // We want to trust any root CA, but maintain all other standard SSL checks
                client = getCloseableHttpClient(true);
                response = client.execute(request);
                status = response.getStatusLine().getStatusCode();
                contentType = response.getEntity().getContentType().getValue();
                if (status == HTTP_OK && CSW_MIME_TYPES.contains(contentType)) {
                    return result.trustedCertAuthority(false)
                            .certError(false)
                            .available(true);
                }
            } catch (Exception e1) {
                return result.trustedCertAuthority(false)
                        .certError(false)
                        .available(false);
            }
        } finally {
            closeClientAndResponse(client, response);
        }
        return result;
    }

    // Given a configuration, determines the preferred CSW source type and output schema and returns
    // a config with the appropriate factoryPid and Output Schema.
    public Optional<CswSourceConfiguration> getPreferredConfig(CswSourceConfiguration config) {
        CswSourceConfiguration preferred = new CswSourceConfiguration(config);
        HttpGet getCapabilitiesRequest = new HttpGet(
                preferred.endpointUrl() + GET_CAPABILITIES_PARAMS);
        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;
        if (config.endpointUrl().startsWith("https") && config.sourceUserName() != null && config.sourceUserPassword() != null) {
            byte[] auth = Base64.encodeBase64((config.sourceUserName() + ":" + config.sourceUserPassword()).getBytes());
            getCapabilitiesRequest.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + new String(auth));
        }
        XPath xpath = XPathFactory.newInstance().newXPath();
        xpath.setNamespaceContext(SOURCES_NAMESPACE_CONTEXT);
        try {
            client = getCloseableHttpClient(true);
            response = client.execute(getCapabilitiesRequest);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document capabilitiesXml = builder.parse(response
                    .getEntity()
                    .getContent());
            if ((Boolean) xpath.compile(HAS_CATALOG_METACARD_EXP)
                    .evaluate(capabilitiesXml, XPathConstants.BOOLEAN)) {
                return Optional.of((CswSourceConfiguration) preferred.factoryPid(
                        CSW_PROFILE_FACTORY_PID));
            } else if ((Boolean) xpath.compile(HAS_GMD_ISO_EXP)
                    .evaluate(capabilitiesXml, XPathConstants.BOOLEAN)) {
                return Optional.of(((CswSourceConfiguration) preferred.factoryPid(
                        CSW_GMD_FACTORY_PID)).outputSchema(GMD_OUTPUT_SCHEMA));
            } else {
                return Optional.of(((CswSourceConfiguration) (preferred.factoryPid(
                        CSW_SPEC_FACTORY_PID))).outputSchema(xpath.compile(GET_FIRST_OUTPUT_SCHEMA)
                        .evaluate(capabilitiesXml)));
            }
        } catch (Exception e) {
            return Optional.empty();
        } finally {
            closeClientAndResponse(client, response);
        }
    }

    // Determines the correct CSW endpoint URL format given a config with a Hostname and Port
    public UrlAvailability confirmEndpointUrl(CswSourceConfiguration config) {
        Optional<UrlAvailability> result =  URL_FORMATS.stream()
                .map(formatUrl -> String.format(formatUrl,
                        config.sourceHostName(),
                        config.sourcePort()))
                .map(url -> getUrlAvailability(url, config.sourceUserName(), config.sourceUserPassword()))
                .filter(avail -> avail.isAvailable() || avail.isCertError())
                .findFirst();
        return result.isPresent() ? result.get() : null;
    }
}
