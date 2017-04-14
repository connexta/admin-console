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

package org.codice.ddf.admin.sources.opensearch;

import static org.codice.ddf.admin.api.services.OpenSearchServiceProperties.OPENSEARCH_FACTORY_PID;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.codice.ddf.admin.api.config.ConfigurationType;
import org.codice.ddf.admin.api.config.sources.OpenSearchSourceConfiguration;
import org.codice.ddf.admin.api.config.sources.SourceConfiguration;
import org.codice.ddf.admin.api.handler.ConfigurationHandler;
import org.codice.ddf.admin.api.handler.DefaultConfigurationHandler;
import org.codice.ddf.admin.api.handler.SourceConfigurationHandler;
import org.codice.ddf.admin.api.handler.method.PersistMethod;
import org.codice.ddf.admin.api.handler.method.ProbeMethod;
import org.codice.ddf.admin.api.handler.method.TestMethod;
import org.codice.ddf.admin.api.handler.report.ProbeReport;
import org.codice.ddf.admin.api.handler.report.Report;
import org.codice.ddf.admin.api.services.OpenSearchServiceProperties;
import org.codice.ddf.admin.configurator.ConfigReader;
import org.codice.ddf.admin.configurator.ConfiguratorFactory;
import org.codice.ddf.admin.sources.opensearch.persist.CreateOpenSearchSourcePersistMethod;
import org.codice.ddf.admin.sources.opensearch.persist.DeleteOpenSearchSourcePersistMethod;
import org.codice.ddf.admin.sources.opensearch.probe.DiscoverOpenSearchSourceProbeMethod;
import org.codice.ddf.admin.sources.opensearch.test.SourceNameExistsOpenSearchTestMethod;

public class OpenSearchSourceConfigurationHandler
        extends DefaultConfigurationHandler<SourceConfiguration>
        implements SourceConfigurationHandler<SourceConfiguration> {

    public static final String OPENSEARCH_SOURCE_CONFIGURATION_HANDLER_ID =
            OpenSearchSourceConfiguration.CONFIGURATION_TYPE;

    private final ConfigurationHandler handler;

    private final ConfiguratorFactory configuratorFactory;

    public OpenSearchSourceConfigurationHandler(ConfigurationHandler handler,
            ConfiguratorFactory configuratorFactory) {
        this.handler = handler;
        this.configuratorFactory = configuratorFactory;
    }

    @Override
    public List<ProbeMethod> getProbeMethods() {
        return Collections.singletonList(new DiscoverOpenSearchSourceProbeMethod());
    }

    @Override
    public List<TestMethod> getTestMethods() {
        return Collections.singletonList(new SourceNameExistsOpenSearchTestMethod(
                configuratorFactory));
    }

    @Override
    public List<PersistMethod> getPersistMethods() {
        return Arrays.asList(new CreateOpenSearchSourcePersistMethod(handler, configuratorFactory),
                new DeleteOpenSearchSourcePersistMethod(configuratorFactory));
    }

    @Override
    public ProbeReport probe(String probeId, SourceConfiguration configuration) {
        return super.probe(probeId, new OpenSearchSourceConfiguration(configuration));
    }

    @Override
    public Report test(String testId, SourceConfiguration configuration) {
        return super.test(testId, new OpenSearchSourceConfiguration(configuration));
    }

    @Override
    public Report persist(String persistId, SourceConfiguration configuration) {
        return super.persist(persistId, new OpenSearchSourceConfiguration(configuration));
    }

    @Override
    public List<SourceConfiguration> getConfigurations() {
        ConfigReader configReader = configuratorFactory.getConfigReader();
        return configReader.getManagedServiceConfigs(OPENSEARCH_FACTORY_PID)
                .values()
                .stream()
                .map(OpenSearchServiceProperties::servicePropsToOpenSearchConfig)
                .map(config -> config.sourceUserPassword("*****"))
                .collect(Collectors.toList());
    }

    @Override
    public String getConfigurationHandlerId() {
        return OPENSEARCH_SOURCE_CONFIGURATION_HANDLER_ID;
    }

    @Override
    public ConfigurationType getConfigurationType() {
        return new OpenSearchSourceConfiguration().getConfigurationType();
    }
}
