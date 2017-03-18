package org.codice.ddf.admin.graphql;

import static org.codice.ddf.admin.graphql.GraphQLCommons.actionCreatorToGraphQLObjectType;
import static org.codice.ddf.admin.graphql.GraphQLCommons.actionsToGraphQLFieldDef;

import java.util.Collection;
import java.util.HashMap;

import org.codice.ddf.admin.api.action.ActionCreator;

import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;
import graphql.servlet.GraphQLMutationProvider;
import graphql.servlet.GraphQLQueryProvider;

public class GraphQLProviderImpl implements GraphQLMutationProvider, GraphQLQueryProvider {

    private ActionCreator creator;

    public GraphQLProviderImpl(ActionCreator creator){
        this.creator = creator;
    }

    @Override
    public Collection<GraphQLFieldDefinition> getMutations() {
        return actionsToGraphQLFieldDef(creator, creator.getPersistActions());
    }

    @Override
    public GraphQLObjectType getQuery() {
        return actionCreatorToGraphQLObjectType(creator, creator.getDiscoveryActions());
    }

    @Override
    public String getName() {
        return creator.name();
    }

    @Override
    public Object context() {
        return new HashMap<>();
    }
}
