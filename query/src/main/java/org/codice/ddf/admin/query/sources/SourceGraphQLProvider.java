package org.codice.ddf.admin.query.sources;

import static org.codice.ddf.admin.query.graphql.GraphQLCommons.fieldToGraphQLObjectType;
import static org.codice.ddf.admin.query.graphql.GraphQLCommons.fieldsToGraphQLFieldDefinition;

import java.util.Collection;
import java.util.HashMap;

import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;
import graphql.servlet.GraphQLMutationProvider;
import graphql.servlet.GraphQLQueryProvider;

public class SourceGraphQLProvider implements GraphQLQueryProvider, GraphQLMutationProvider {

    @Override
    public Collection<GraphQLFieldDefinition> getMutations() {
        return fieldsToGraphQLFieldDefinition(new SourceDelegateActionHandler().getPersistActions());
    }

    @Override
    public GraphQLObjectType getQuery() {
        return fieldToGraphQLObjectType(new SourceDelegateActionHandler());
    }

    @Override
    public String getName() {
        return new SourceDelegateActionHandler().fieldName();
    }

    @Override
    public Object context() {
        return new HashMap<>();
    }
}
