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
package org.codice.ddf.admin.sources.commons;

import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class SourceUtilCommons {

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
        return factory.newDocumentBuilder()
                .parse(new InputSource(new StringReader(str)));
    }
}
