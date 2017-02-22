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
package org.codice.ddf.admin.sources;

import static org.codice.ddf.admin.api.handler.commons.SourceHandlerCommons.PING_TIMEOUT;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContexts;

public class SourcesCommons {

    public static CloseableHttpClient getCloseableHttpClient(boolean trustAnyCA) throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        HttpClientBuilder builder = HttpClientBuilder.create().disableAutomaticRetries().setDefaultRequestConfig(
                RequestConfig.custom().setConnectTimeout(PING_TIMEOUT).build());
        if (trustAnyCA) {
            builder.setSSLSocketFactory(new SSLConnectionSocketFactory(SSLContexts.custom()
                    .loadTrustMaterial(null, (chain, authType) -> true)
                    .build()));
        }
        return builder.build();
    }

    public static void closeClientAndResponse(CloseableHttpClient client, CloseableHttpResponse response) {
        try {
            if (client != null) {
                client.close();
            }
        } catch (Exception e) {
        }
        try {
            if (response != null) {
                response.close();
            }
        } catch (Exception e) {
        }
    }
}
