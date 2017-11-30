/**
 * Copyright (c) Codice Foundation
 *
 * <p>This is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version.
 *
 * <p>This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details. A copy of the GNU Lesser General Public
 * License is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 */
package org.codice.ddf.admin.sources.utils;

import static org.codice.ddf.admin.common.services.ServiceCommons.FLAG_PASSWORD;

import ddf.catalog.service.ConfiguredService;
import ddf.catalog.source.ConnectedSource;
import ddf.catalog.source.FederatedSource;
import ddf.catalog.source.Source;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.lang.StringUtils;
import org.codice.ddf.admin.common.fields.base.scalar.BooleanField;
import org.codice.ddf.admin.common.fields.common.PidField;
import org.codice.ddf.admin.sources.fields.type.SourceConfigField;
import org.codice.ddf.internal.admin.configurator.actions.ConfiguratorSuite;
import org.codice.ddf.platform.util.XMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class SourceUtilCommons {

  private static final Logger LOGGER = LoggerFactory.getLogger(SourceUtilCommons.class);

  private static final XMLUtils XML_UTILS = XMLUtils.getInstance();

  private final ConfiguratorSuite configuratorSuite;

  public static final NamespaceContext SOURCES_NAMESPACE_CONTEXT =
      new NamespaceContext() {
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

  public SourceUtilCommons(ConfiguratorSuite configuratorSuite) {
    this.configuratorSuite = configuratorSuite;
  }

  public Document createDocument(String body)
      throws ParserConfigurationException, IOException, SAXException {
    return XML_UTILS.getSecureDocumentBuilder(true).parse(new InputSource(new StringReader(body)));
  }

  public List<Source> getAllSourceReferences() {
    List<Source> sources = new ArrayList<>();
    sources.addAll(configuratorSuite.getServiceReader().getServices(FederatedSource.class, null));
    sources.addAll(configuratorSuite.getServiceReader().getServices(ConnectedSource.class, null));
    return sources;
  }

  /**
   * Gets the configurations for the given factoryPids using the actions provided. A mapper is used
   * to transform the service properties to a {@link SourceConfigField}. Providing the pid parameter
   * will return only the configuration with that pid.
   *
   * @param factoryPids factory pids to lookup configurations for
   * @param mapper a {@link Function} taking a map of string to objects and returning a {@code
   *     SourceConfigField}
   * @param pid a servicePid to select a single configuration, returns all configs when null or
   *     empty
   * @return a list of {@code SourceInfoField}s configured in the system
   */
  public <T extends SourceConfigField> List<T> getSourceConfigurations(
      List<String> factoryPids, Function<Map<String, Object>, T> mapper, String pid) {
    List<T> sourceConfigs = new ArrayList<>();
    if (StringUtils.isNotEmpty(pid)) {
      T config = mapper.apply(configuratorSuite.getServiceActions().read(pid));
      config.credentials().password(FLAG_PASSWORD);
      sourceConfigs.add(config);
      return sourceConfigs;
    }

    factoryPids
        .stream()
        .flatMap(
            factoryPid ->
                configuratorSuite.getManagedServiceActions().read(factoryPid).values().stream())
        .map(mapper)
        .forEach(sourceConfigs::add);

    sourceConfigs.forEach(config -> config.credentials().password(FLAG_PASSWORD));
    return sourceConfigs;
  }

  @SuppressWarnings("squid:S135" /* Two break statements required */)
  public void populateAvailability(BooleanField availability, PidField pid) {
    for (Source source : getAllSourceReferences()) {
      if (source instanceof ConfiguredService) {
        ConfiguredService service = (ConfiguredService) source;
        String servicePid = service.getConfigurationPid();
        if (servicePid != null && servicePid.equals(pid.getValue())) {
          availability.setValue(source.isAvailable());
          break;
        } else {
          LOGGER.debug("Unable to determine availability for source with pid [{}]", pid.getValue());
          availability.setValue(false);
          break;
        }
      }
    }
  }
}
