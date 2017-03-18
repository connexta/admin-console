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

import org.codice.ddf.admin.ldap.actions.LdapActionCreator;
import org.codice.ddf.admin.security.sts.StsActionCreator;
import org.codice.ddf.admin.security.wcpm.actions.WcpmActionCreator;
import org.codice.ddf.admin.sources.SourceActionCreator;
import org.codice.ddf.admin.utils.conn.ConnectionActionCreator;

import com.google.common.collect.ImmutableList;

import graphql.introspection.IntrospectionQuery;
import graphql.servlet.GraphQLServlet;

public class SchemaGenerator {

    private static final List<GraphQLProviderImpl> GRAPHQL_PROVIDERS =
            ImmutableList.of(
                    new GraphQLProviderImpl(new StsActionCreator()),
                    new GraphQLProviderImpl(new ConnectionActionCreator()),
                    new GraphQLProviderImpl(new LdapActionCreator()),
                    new GraphQLProviderImpl(new SourceActionCreator()),
                    new GraphQLProviderImpl(new WcpmActionCreator()));

    public static void main(String[] args) throws IOException, URISyntaxException {
        GraphQLServlet servlet = new GraphQLServlet();
        GRAPHQL_PROVIDERS.stream()
                .forEach(query -> servlet.bindQueryProvider(query));
        GRAPHQL_PROVIDERS.stream()
                .forEach(mute -> servlet.bindMutationProvider(mute));
        servlet.bindQueryProvider(new RelayGraphQLProvider());
        String schemaResult = servlet.executeQuery(IntrospectionQuery.INTROSPECTION_QUERY);
        Files.write(Paths.get(System.getProperty("target.path"), "schema.json"),
                schemaResult.getBytes());
    }
}
