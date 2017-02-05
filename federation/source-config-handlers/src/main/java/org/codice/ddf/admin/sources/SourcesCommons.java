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

    public static CloseableHttpClient getCloseableHttpClient(boolean trustAnyCA)
            throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
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
