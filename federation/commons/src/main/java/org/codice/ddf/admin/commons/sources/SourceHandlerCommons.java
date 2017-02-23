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
package org.codice.ddf.admin.commons.sources;

import static org.codice.ddf.admin.api.handler.commons.HandlerCommons.FAILED_CREATE;
import static org.codice.ddf.admin.api.handler.commons.HandlerCommons.SUCCESSFUL_CREATE;

import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.codice.ddf.admin.api.handler.ConfigurationMessage;
import org.codice.ddf.admin.api.handler.MessageBuilder;
import org.codice.ddf.admin.commons.requests.RequestUtils;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.google.common.collect.ImmutableMap;

public class SourceHandlerCommons {

    //Common probe keys
    public static final String DISCOVERED_SOURCES = "discoveredSources";
    public static final String DISCOVERED_URL = "discoveredUrl";

    //Common probe id's
    public static final String DISCOVER_SOURCES_ID = "discover-sources";
    public static final String SOURCE_NAME_EXISTS_TEST_ID = "source-name-exists";


    //Common failure types
    public static final String UNKNOWN_ENDPOINT = "UNKNOWN_ENDPOINT";
    private static final Map<String, String> FAILURE_DESCRIPTIONS = ImmutableMap.<String, String>builder().putAll(
            RequestUtils.getRequestSubtypeDescriptions(RequestUtils.CANNOT_CONNECT, RequestUtils.CERT_ERROR))
                    .put(UNKNOWN_ENDPOINT, "The endpoint does not appear to have the specified capabilities.")
                    .put(FAILED_CREATE, "Failed to create source configuration.").build();

    //Common warning types
    private static final Map<String, String> WARNING_DESCRIPTIONS = ImmutableMap.copyOf(
            RequestUtils.getRequestSubtypeDescriptions(RequestUtils.UNTRUSTED_CA));

    //Common success types
    public static final String DISCOVERED_SOURCE = "DISCOVERED_SOURCE";
    public static final String DELETED_SOURCE = "DELETED_SOURCE";
    public static final String CREATED_SOURCE = "CREATED_SOURCE";
    public static final String VERIFIED_CAPABILITIES = "VERIFIED_CAPABILITIES";
    private static final Map<String, String> SUCCESS_DESCRIPTIONS = ImmutableMap.of(DISCOVERED_SOURCE, "Successfully discovered source from a url.",
            VERIFIED_CAPABILITIES, "Verified endpoint has specified capabilities",
            SUCCESSFUL_CREATE, "Successfully created a source configuration.",
            DELETED_SOURCE, "Successfully deleted source configuration.");

    private static final MessageBuilder SOURCES_MESSAGE_BUILDER = new MessageBuilder(
            SUCCESS_DESCRIPTIONS,
            FAILURE_DESCRIPTIONS,
            WARNING_DESCRIPTIONS);

    /*********************************************************
     * NamespaceContext for Xpath queries
     *********************************************************/
    public static final NamespaceContext SOURCES_NAMESPACE_CONTEXT = new NamespaceContext() {
        @Override
        public String getNamespaceURI(String prefix) {
            switch (prefix) {
            case "ows":
                return "http://www.opengis.net/ows";
            case "wfs":
                return "http://www.opengis.net/wfs/2.0";
            case "os":
            case "opensearch":
                return "http://a9.com/-/spec/opensearch/1.1/";
            default:
                return null;
            }
        }
        @Override
        public String getPrefix(String namespaceURI) {
            switch (namespaceURI) {
            case "http://www.opengis.net/ows":
                return "ows";
            case "http://www.opengis.net/wfs/2.0":
                return "wfs";
            case "http://a9.com/-/spec/opensearch/1.1/":
                return "os";
            default:
                return null;
            }
        }
        @Override
        public Iterator getPrefixes(String namespaceURI) {
            return null;
        }
    };

    public static Document createDocument(String str)
            throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        return factory.newDocumentBuilder().parse(new InputSource(new StringReader(str)));
    }

    public static Map<String, String> getCommonSourceSubtypeDescriptions(String... subtypeKeys) {
        return SOURCES_MESSAGE_BUILDER.getDescriptions(subtypeKeys);
    }

    public static ConfigurationMessage createCommonSourceConfigMsg(String result){
        return SOURCES_MESSAGE_BUILDER.buildMessage(result);
    }
}
