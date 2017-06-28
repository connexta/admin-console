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
package org.codice.ddf.admin.sources.utils;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.codice.ddf.admin.common.fields.base.scalar.BooleanField;
import org.codice.ddf.admin.common.fields.common.PidField;
import org.codice.ddf.admin.common.services.ServiceCommons;
import org.codice.ddf.admin.configurator.ConfiguratorFactory;
import org.codice.ddf.admin.sources.fields.type.SourceConfigField;
import org.codice.ddf.internal.admin.configurator.actions.ManagedServiceActions;
import org.codice.ddf.internal.admin.configurator.actions.ServiceActions;
import org.codice.ddf.internal.admin.configurator.actions.ServiceReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import ddf.catalog.service.ConfiguredService;
import ddf.catalog.source.ConnectedSource;
import ddf.catalog.source.FederatedSource;
import ddf.catalog.source.Source;

public class SourceUtilCommons {

    private static final Logger LOGGER = LoggerFactory.getLogger(SourceUtilCommons.class);

    private ServiceCommons serviceCommons;

    private ManagedServiceActions managedServiceActions;

    private ServiceActions serviceActions;

    private ServiceReader serviceReader;

    private ConfiguratorFactory configuratorFactory;

    public SourceUtilCommons() {
    }

    /**
     * @param managedServiceActions service to interact with managed service configurations
     * @param serviceActions        service to interact with admin configurations
     * @param serviceReader         service to query service state
     * @param configuratorFactory   service to create {@link org.codice.ddf.admin.configurator.Configurator}s
     */
    public SourceUtilCommons(ManagedServiceActions managedServiceActions,
            ServiceActions serviceActions, ServiceReader serviceReader,
            ConfiguratorFactory configuratorFactory) {
        this.managedServiceActions = managedServiceActions;
        this.serviceActions = serviceActions;
        this.serviceReader = serviceReader;
        this.configuratorFactory = configuratorFactory;

        serviceCommons = new ServiceCommons(managedServiceActions,
                serviceActions,
                serviceReader,
                configuratorFactory);
    }

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

    public Document createDocument(String body)
            throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        return factory.newDocumentBuilder()
                .parse(new InputSource(new StringReader(body)));
    }

    public List<Source> getAllSourceReferences() {
        if (serviceReader == null) {
            LOGGER.debug("Unable to get source references due to missing serviceReader");
            return Collections.emptyList();
        }

        List<Source> sources = new ArrayList<>();
        sources.addAll(serviceReader.getServices(FederatedSource.class, null));
        sources.addAll(serviceReader.getServices(ConnectedSource.class, null));
        return sources;
    }

    /**
     * Gets the configurations for the given factoryPids using the actions provided. A mapper is used
     * to transform the service properties to a {@link SourceConfigField}. Providing the pid parameter
     * will return only the configuration with that pid.
     *
     * @param factoryPids factory pids to lookup configurations for
     * @param mapper      a {@link Function} taking a map of string to objects and returning a {@code SourceConfigField}
     * @param pid         a servicePid to select a single configuration, returns all configs when null or empty
     * @return a list of {@code SourceInfoField}s configured in the system
     */
    public List<SourceConfigField> getSourceConfigurations(List<String> factoryPids,
            Function<Map<String, Object>, SourceConfigField> mapper, String pid) {
        if (serviceActions == null || managedServiceActions == null) {
            LOGGER.debug(
                    "Unable to get source configurations due to missing serviceActions or managedServiceActions");
            return Collections.emptyList();
        }

        List<SourceConfigField> sourceInfoListField = new ArrayList<>();
        if (StringUtils.isNotEmpty(pid)) {
            SourceConfigField config = mapper.apply(serviceActions.read(pid));
            sourceInfoListField.add(config);
            return sourceInfoListField;
        }

        factoryPids.stream()
                .flatMap(factoryPid -> managedServiceActions.read(factoryPid)
                        .values()
                        .stream())
                .map(mapper)
                .forEach(sourceInfoListField::add);

        return sourceInfoListField;
    }

    public void populateAvailability(BooleanField availability, PidField pid) {
        for (Source source : getAllSourceReferences()) {
            if (source instanceof ConfiguredService) {
                ConfiguredService service = (ConfiguredService) source;
                if (service.getConfigurationPid()
                        .equals(pid.getValue())) {
                    availability.setValue(source.isAvailable());
                    break;
                }
            }
        }
    }

    public void setManagedServiceActions(ManagedServiceActions managedServiceActions) {
        this.managedServiceActions = managedServiceActions;
    }

    public void setServiceActions(ServiceActions serviceActions) {
        this.serviceActions = serviceActions;
    }

    public void setServiceReader(ServiceReader serviceReader) {
        this.serviceReader = serviceReader;
    }

    public void setConfiguratorFactory(ConfiguratorFactory configuratorFactory) {
        this.configuratorFactory = configuratorFactory;
    }
}
