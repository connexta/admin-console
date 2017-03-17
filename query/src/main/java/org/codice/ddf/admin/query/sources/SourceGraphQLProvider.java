package org.codice.ddf.admin.query.sources;

import static org.codice.ddf.admin.query.graphql.GraphQLCommons.actionCreatorToGraphQLObjectType;
import static org.codice.ddf.admin.query.graphql.GraphQLCommons.actionsToGraphQLFieldDef;
import static org.codice.ddf.admin.query.graphql.GraphQLCommons.fieldToGraphQLObjectType;
import static org.codice.ddf.admin.query.graphql.GraphQLCommons.fieldsToGraphQLFieldDefinition;

import java.util.Collection;
import java.util.HashMap;

import org.codice.ddf.admin.query.api.action.ActionCreator;

import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;
import graphql.servlet.GraphQLMutationProvider;
import graphql.servlet.GraphQLQueryProvider;

public class SourceGraphQLProvider implements GraphQLQueryProvider, GraphQLMutationProvider {

    public static final ActionCreator SOURCE_ACTION_CREATOR = new SourceDelegateActionHandler();
    @Override
    public GraphQLObjectType getQuery() {
        return actionCreatorToGraphQLObjectType(SOURCE_ACTION_CREATOR, SOURCE_ACTION_CREATOR.getDiscoveryActions());
    }

    @Override
    public Collection<GraphQLFieldDefinition> getMutations() {
        return actionsToGraphQLFieldDef(SOURCE_ACTION_CREATOR, SOURCE_ACTION_CREATOR.getPersistActions());
    }

    @Override
    public String getName() {
        return SOURCE_ACTION_CREATOR.name();
    }

    @Override
    public Object context() {
        return new HashMap<>();
    }
}
