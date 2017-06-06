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
package org.codice.ddf.admin.sources.commons.utils;

import static org.codice.ddf.admin.common.services.ServiceCommons.FLAG_PASSWORD;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.codice.ddf.admin.api.fields.ListField;
import org.codice.ddf.admin.common.fields.base.ListFieldImpl;
import org.codice.ddf.admin.sources.fields.SourceInfoField;
import org.codice.ddf.admin.sources.fields.type.SourceConfigUnionField;
import org.codice.ddf.internal.admin.configurator.actions.ManagedServiceActions;
import org.codice.ddf.internal.admin.configurator.actions.ServiceActions;
import org.codice.ddf.internal.admin.configurator.actions.ServiceReader;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import ddf.catalog.service.ConfiguredService;
import ddf.catalog.source.ConnectedSource;
import ddf.catalog.source.FederatedSource;
import ddf.catalog.source.Source;

public class SourceUtilCommons {

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

    public static Document createDocument(String body)
            throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        return factory.newDocumentBuilder()
                .parse(new InputSource(new StringReader(body)));
    }

    public static List<Source> getAllSourceReferences(ServiceReader serviceReader) {
        List<Source> sources = new ArrayList<>();
        sources.addAll(serviceReader.getServices(FederatedSource.class, null));
        sources.addAll(serviceReader.getServices(ConnectedSource.class, null));
        return sources;
    }

    public static SourceInfoField createSourceInfoField(boolean isAvailable,
            SourceConfigUnionField config) {
        config.credentials()
                .password(FLAG_PASSWORD);
        SourceInfoField sourceInfoField = new SourceInfoField();
        sourceInfoField.isAvaliable(isAvailable);
        sourceInfoField.configuration(config);
        return sourceInfoField;
    }

    /**
     * Gets the configurations for the given factoryPids using the actions provided. A mapper is used
     * to transform the service properties to a {@link SourceConfigUnionField}. Providing the pid parameter
     * will return only the configuration with that pid.
     *
     * @param factoryPids           factory pids to lookup configurations for
     * @param mapper                a {@link Function} taking a map of string to objects and returning a {@code SourceConfigUnionField}
     * @param pid                   a servicePid to select a single configuration, returns all configs when null or empty
     * @param serviceActions          service to interact with admin configurations
     * @param managedServiceActions service to interact with managed service configurations
     * @param serviceReader         service to query service state
     * @return a list of {@code SourceInfoField}s configured in the system
     */
    public static ListField<SourceInfoField> getSourceConfigurations(List<String> factoryPids,
            Function<Map<String, Object>, SourceConfigUnionField> mapper, String pid,
            ServiceActions serviceActions, ManagedServiceActions managedServiceActions,
            ServiceReader serviceReader) {
        ListFieldImpl<SourceInfoField> sourceInfoListField =
                new ListFieldImpl<>(SourceInfoField.class);

        if (StringUtils.isNotEmpty(pid)) {

            SourceConfigUnionField config = mapper.apply(serviceActions.read(pid));
            sourceInfoListField.add(createSourceInfoField(true, config));
            populateSourceAvailability(sourceInfoListField.getList(), serviceReader);
            return sourceInfoListField;
        }

        factoryPids.stream()
                .flatMap(factoryPid -> managedServiceActions.read(factoryPid)
                        .values()
                        .stream())
                .map(mapper)
                .forEach(config -> sourceInfoListField.add(createSourceInfoField(false, config)));

        populateSourceAvailability(sourceInfoListField.getList(), serviceReader);
        return sourceInfoListField;
    }

    private static void populateSourceAvailability(List<SourceInfoField> sourceInfoList,
            ServiceReader serviceReader) {
        List<Source> sources = getAllSourceReferences(serviceReader);
        for (SourceInfoField sourceInfoField : sourceInfoList) {
            for (Source source : sources) {
                if (source instanceof ConfiguredService) {
                    ConfiguredService service = (ConfiguredService) source;
                    if (service.getConfigurationPid()
                            .equals(sourceInfoField.config()
                                    .pid())) {
                        sourceInfoField.isAvaliable(source.isAvailable());
                        break;
                    }
                }
            }
        }
    }
}
