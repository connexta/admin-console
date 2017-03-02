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

    private static final List<GraphQLQueryProvider> querys = ImmutableList.of(new StsGraphQLProvider(), new ConnectionGraphQLProvider(), new LdapGraphQLProvider(), new SourceGraphQLProvider(), new WcpmGraphQLProvider());
    private static final List<GraphQLMutationProvider> mutations = ImmutableList.of(new StsGraphQLProvider(), new ConnectionGraphQLProvider(), new LdapGraphQLProvider(), new SourceGraphQLProvider(), new WcpmGraphQLProvider());

    public static void main(String[] args) throws IOException, URISyntaxException {
        GraphQLServlet servlet = new GraphQLServlet();
        querys.stream().forEach(query -> servlet.bindQueryProvider(query));
        mutations.stream().forEach(mute -> servlet.bindMutationProvider(mute));

//        File file = new File(Paths.get(System.getProperty("target.path"),"schema.json"));
        String schemaResult = servlet.executeQuery(INTROSPECTION_QUERY);
        Files.write(Paths.get(System.getProperty("target.path"),"schema.json"), schemaResult.getBytes());
    }
}
