package org.codice.ddf.admin.query.connection;

import static org.codice.ddf.admin.query.graphql.GraphQLCommons.fieldToGraphQLObjectType;
import static org.codice.ddf.admin.query.graphql.GraphQLCommons.fieldsToGraphQLFieldDefinition;

import java.util.Collection;
import java.util.HashMap;

import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;
import graphql.servlet.GraphQLMutationProvider;
import graphql.servlet.GraphQLQueryProvider;

public class ConnectionGraphQLProvider implements GraphQLQueryProvider, GraphQLMutationProvider {

    @Override
    public Collection<GraphQLFieldDefinition> getMutations() {
        return fieldsToGraphQLFieldDefinition(new ConnectionActionHandler().getPersistActions());
    }

    @Override
    public GraphQLObjectType getQuery() {
        return fieldToGraphQLObjectType(new ConnectionActionHandler());
    }

    @Override
    public String getName() {
        return new ConnectionActionHandler().fieldName();
    }

    @Override
    public Object context() {
        return new HashMap<>();
    }
}