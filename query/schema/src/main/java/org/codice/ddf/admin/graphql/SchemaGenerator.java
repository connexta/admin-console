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
import org.codice.ddf.admin.graphql.test.TestFieldProvider;

import com.google.common.collect.ImmutableList;

import graphql.introspection.IntrospectionQuery;

public class SchemaGenerator {

    public static void main(String[] args) throws IOException, URISyntaxException {
        GraphQLServletImpl servlet = new GraphQLServletImpl();
//        final List<FieldProvider> GRAPHQL_PROVIDERS = ImmutableList.of(
//                new StsFieldProvider(new ConfiguratorFactoryImpl()),
//                new ConnectionFieldProvider(),
//                new LdapFieldProvider(new ConfiguratorFactoryImpl()),
//                new WcpmFieldProvider(new ConfiguratorFactoryImpl()),
//                new CswFieldProvider(),
//                new WfsFieldProvider(),
//                new OpenSearchFieldProvider()
//        );

        final List<FieldProvider> GRAPHQL_PROVIDERS = ImmutableList.of(
                new TestFieldProvider()
        );

        servlet.setFieldProviders(GRAPHQL_PROVIDERS);
        String schemaResult = servlet.executeQuery(IntrospectionQuery.INTROSPECTION_QUERY);
        Files.write(Paths.get(System.getProperty("target.path"), "schema.json"),
                schemaResult == null ? "".getBytes() : schemaResult.getBytes());
    }
}
