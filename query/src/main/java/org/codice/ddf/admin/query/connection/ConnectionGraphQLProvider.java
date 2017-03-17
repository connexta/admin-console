package org.codice.ddf.admin.query.connection;

import static org.codice.ddf.admin.query.graphql.GraphQLCommons.actionCreatorToGraphQLObjectType;
import static org.codice.ddf.admin.query.graphql.GraphQLCommons.actionsToGraphQLFieldDef;
import static org.codice.ddf.admin.query.graphql.GraphQLCommons.fieldToGraphQLObjectType;
import static org.codice.ddf.admin.query.graphql.GraphQLCommons.fieldsToGraphQLFieldDefinition;

import java.util.Collection;
import java.util.HashMap;

import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;
import graphql.servlet.GraphQLMutationProvider;
import graphql.servlet.GraphQLQueryProvider;

public class ConnectionGraphQLProvider implements GraphQLQueryProvider, GraphQLMutationProvider {

    public static final ConnectionActionCreator CONNECTION_ACTION_CREATOR = new ConnectionActionCreator();
    @Override
    public Collection<GraphQLFieldDefinition> getMutations() {
        return actionsToGraphQLFieldDef(CONNECTION_ACTION_CREATOR, CONNECTION_ACTION_CREATOR.getPersistActions());
    }

    @Override
    public GraphQLObjectType getQuery() {
        return actionCreatorToGraphQLObjectType(CONNECTION_ACTION_CREATOR, CONNECTION_ACTION_CREATOR.getDiscoveryActions());
    }

    @Override
    public String getName() {
        return CONNECTION_ACTION_CREATOR.name();
    }

    @Override
    public Object context() {
        return new HashMap<>();
    }
}