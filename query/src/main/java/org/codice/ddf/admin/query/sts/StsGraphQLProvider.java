package org.codice.ddf.admin.query.sts;

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

public class StsGraphQLProvider implements GraphQLQueryProvider, GraphQLMutationProvider {

    public static final StsActionHandler STS_ACTION_CREATOR = new StsActionHandler();

    @Override
    public GraphQLObjectType getQuery() {
        return actionCreatorToGraphQLObjectType(STS_ACTION_CREATOR, STS_ACTION_CREATOR.getDiscoveryActions());
    }

    @Override
    public Collection<GraphQLFieldDefinition> getMutations() {
        return actionsToGraphQLFieldDef(STS_ACTION_CREATOR, STS_ACTION_CREATOR.getPersistActions());
    }

    @Override
    public String getName() {
        return STS_ACTION_CREATOR.name();
    }

    @Override
    public Object context() {
        return new HashMap<>();
    }


}
