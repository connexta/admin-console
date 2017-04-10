package org.codice.ddf.admin.query.wcpm;

import static org.codice.ddf.admin.query.graphql.GraphQLCommons.fieldToGraphQLObjectType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;
import graphql.servlet.GraphQLMutationProvider;
import graphql.servlet.GraphQLQueryProvider;

public class WcpmGraphQLProvider implements GraphQLQueryProvider, GraphQLMutationProvider {
    @Override
    public GraphQLObjectType getQuery() {
        return fieldToGraphQLObjectType(new WcpmActionHandler());
    }

    @Override
    public Object context() {
        return new HashMap<>();
    }

    @Override
    public String getName() {
        return new WcpmActionHandler().fieldName();
    }

    @Override
    public Collection<GraphQLFieldDefinition> getMutations() {
        return new ArrayList<>();
    }
}
