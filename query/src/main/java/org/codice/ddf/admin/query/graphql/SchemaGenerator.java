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
package org.codice.ddf.admin.query.graphql;

import static graphql.introspection.IntrospectionQuery.INTROSPECTION_QUERY;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.codice.ddf.admin.query.connection.ConnectionGraphQLProvider;
import org.codice.ddf.admin.query.ldap.LdapGraphQLProvider;
import org.codice.ddf.admin.query.sources.SourceGraphQLProvider;
import org.codice.ddf.admin.query.sts.StsGraphQLProvider;
import org.codice.ddf.admin.query.wcpm.WcpmGraphQLProvider;

import com.google.common.collect.ImmutableList;

import graphql.servlet.GraphQLMutationProvider;
import graphql.servlet.GraphQLQueryProvider;
import graphql.servlet.GraphQLServlet;

public class SchemaGenerator {

    private static final List<GraphQLQueryProvider> QUERIES =
            ImmutableList.of(new StsGraphQLProvider(),
                    new ConnectionGraphQLProvider(),
                    new LdapGraphQLProvider(),
                    new SourceGraphQLProvider(),
                    new WcpmGraphQLProvider());

    private static final List<GraphQLMutationProvider> MUTATIONS =
            ImmutableList.of(new StsGraphQLProvider(),
                    new ConnectionGraphQLProvider(),
                    new LdapGraphQLProvider(),
                    new SourceGraphQLProvider(),
                    new WcpmGraphQLProvider());

    public static void main(String[] args) throws IOException, URISyntaxException {
        GraphQLServlet servlet = new GraphQLServlet();
        QUERIES.stream()
                .forEach(query -> servlet.bindQueryProvider(query));
        MUTATIONS.stream()
                .forEach(mute -> servlet.bindMutationProvider(mute));

//        File file = new File(Paths.get(System.getProperty("target.path"),"schema.json"));
        String schemaResult = servlet.executeQuery(INTROSPECTION_QUERY);
        Files.write(Paths.get(System.getProperty("target.path"),"schema.json"), schemaResult.getBytes());
    }
}
