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
 **/
package org.codice.ddf.admin.graphql;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.codice.ddf.admin.api.FieldProvider;
import org.codice.ddf.admin.configurator.impl.BundleOperation;
import org.codice.ddf.admin.configurator.impl.ConfiguratorFactoryImpl;
import org.codice.ddf.admin.configurator.impl.FeatureOperation;
import org.codice.ddf.admin.configurator.impl.ManagedServiceOperation;
import org.codice.ddf.admin.configurator.impl.PropertyOperation;
import org.codice.ddf.admin.configurator.impl.ServiceOperation;
import org.codice.ddf.admin.configurator.impl.ServiceReaderImpl;
import org.codice.ddf.admin.ldap.LdapFieldProvider;
import org.codice.ddf.admin.security.sts.StsFieldProvider;
import org.codice.ddf.admin.security.wcpm.WcpmFieldProvider;
import org.codice.ddf.admin.sources.csw.CswFieldProvider;
import org.codice.ddf.admin.sources.opensearch.OpenSearchFieldProvider;
import org.codice.ddf.admin.sources.wfs.WfsFieldProvider;
import org.codice.ddf.admin.utils.conn.ConnectionFieldProvider;

import com.google.common.collect.ImmutableList;

import graphql.introspection.IntrospectionQuery;

public class SchemaGenerator {

    public static void main(String[] args) throws IOException, URISyntaxException {
        GraphQLServletImpl servlet = new GraphQLServletImpl();
        final List<FieldProvider> GRAPHQL_PROVIDERS =
                ImmutableList.of(new StsFieldProvider(new ServiceOperation.Actions()),
                        new ConnectionFieldProvider(),
                        new LdapFieldProvider(new ConfiguratorFactoryImpl(),
                                new FeatureOperation.Actions(),
                                new ManagedServiceOperation.Actions(),
                                new PropertyOperation.Actions()),
                        new WcpmFieldProvider(new ConfiguratorFactoryImpl(),
                                new ServiceOperation.Actions(),
                                new BundleOperation.Actions(),
                                new ManagedServiceOperation.Actions(),
                                new ServiceReaderImpl()),
                        new CswFieldProvider(new ConfiguratorFactoryImpl(),
                                new ServiceOperation.Actions(),
                                new ManagedServiceOperation.Actions(),
                                new ServiceReaderImpl()),
                        new WfsFieldProvider(new ConfiguratorFactoryImpl(),
                                new ServiceOperation.Actions(),
                                new ManagedServiceOperation.Actions(),
                                new ServiceReaderImpl()),
                        new OpenSearchFieldProvider(new ConfiguratorFactoryImpl(),
                                new ServiceOperation.Actions(),
                                new ManagedServiceOperation.Actions(),
                                new ServiceReaderImpl()));

        servlet.setFieldProviders(GRAPHQL_PROVIDERS);
        String schemaResult = servlet.executeQuery(IntrospectionQuery.INTROSPECTION_QUERY);
        Files.write(Paths.get(System.getProperty("target.path"), "schema.json"),
                schemaResult == null ? "".getBytes() : schemaResult.getBytes());
    }
}
