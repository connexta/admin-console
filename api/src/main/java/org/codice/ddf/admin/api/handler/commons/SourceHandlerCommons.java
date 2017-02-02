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
package org.codice.ddf.admin.api.handler.commons;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;

public class SourceHandlerCommons {

    //Common probe return types
    public static final String DISCOVERED_SOURCES = "discoveredSources";

    //Common probe, persist and test id's
    public static final String DISCOVER_SOURCES_ID = "discover-sources";

    public static final String CONFIG_FROM_URL_ID = "config-from-url";

    public static final String VALID_URL_TEST_ID = "valid-url";

    //Common success types
    public static final String CONFIG_CREATED = "CONFIG_CREATED";

    public static final String VERIFIED_URL = "VERIFIED_URL";

    public static final String REACHED_URL = "REACHED_URL";

    //Common warning types
    public static final String UNTRUSTED_CA = "UNTRUSTED_CA";

    //Common failed types
    public static final String CANNOT_CONNECT = "CANNOT_CONNECT";

    public static final String UNKNOWN_ENDPOINT = "UNKNOWN_ENDPOINT";

    public static final String CERT_ERROR = "CERT_ERROR";

    public static final String BAD_CONFIG = "BAD_CONFIG";

    public static final int PING_TIMEOUT = 500;

    public static String endpointIsReachable(String hostname, int port) {
        try (Socket connection = new Socket()) {
            connection.connect(new InetSocketAddress(hostname, port), PING_TIMEOUT);
            connection.close();
            return REACHED_URL;

        } catch (IOException e) {
            return CANNOT_CONNECT;
        }
    }

    public static String endpointIsReachable(String url) {
        try {
            URLConnection urlConnection = (new URL(url).openConnection());
            urlConnection.setConnectTimeout(PING_TIMEOUT);
            urlConnection.connect();
            return REACHED_URL;
        } catch (Exception e) {
            return CANNOT_CONNECT;
        }
    }

    /*********************************************************
     * NamespaceContext for Xpath queries
     *********************************************************/
    public static final NamespaceContext OWS_NAMESPACE_CONTEXT = new NamespaceContext() {
        @Override
        public String getNamespaceURI(String prefix) {
            return prefix.equals("ows") ? "http://www.opengis.net/ows" : null;
        }

        @Override
        public String getPrefix(String namespaceURI) {
            return null;
        }

        @Override
        public Iterator getPrefixes(String namespaceURI) {
            return null;
        }
    };
}
