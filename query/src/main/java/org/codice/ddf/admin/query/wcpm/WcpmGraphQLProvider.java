package org.codice.ddf.admin.query.wcpm;

import static org.codice.ddf.admin.query.graphql.GraphQLCommons.actionCreatorToGraphQLObjectType;
import static org.codice.ddf.admin.query.graphql.GraphQLCommons.actionsToGraphQLFieldDef;

import java.util.Collection;
import java.util.HashMap;

import org.codice.ddf.admin.query.api.action.ActionCreator;
import org.codice.ddf.admin.query.wcpm.actions.WcpmActionCreator;

import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;
import graphql.servlet.GraphQLMutationProvider;
import graphql.servlet.GraphQLQueryProvider;

public class WcpmGraphQLProvider implements GraphQLQueryProvider, GraphQLMutationProvider {

    public static final ActionCreator WCPM_ACTION_CREATOR = new WcpmActionCreator();
    @Override
    public GraphQLObjectType getQuery() {
        return actionCreatorToGraphQLObjectType(WCPM_ACTION_CREATOR, WCPM_ACTION_CREATOR.getDiscoveryActions());
    }

    public Collection<GraphQLFieldDefinition> getMutations() {
        return actionsToGraphQLFieldDef(WCPM_ACTION_CREATOR, WCPM_ACTION_CREATOR.getPersistActions());
    }

    @Override
    public String getName() {
        return WCPM_ACTION_CREATOR.name();
    }

    @Override
    public Object context() {
        return new HashMap<>();
    }
}
