package org.codice.ddf.admin.query.ldap;

import static org.codice.ddf.admin.query.graphql.GraphQLCommons.handlerActionToGraphQLFieldDefinition;
import static org.codice.ddf.admin.query.graphql.GraphQLCommons.handlerActionsToGraphQLFieldDefinition;
import static org.codice.ddf.admin.query.graphql.GraphQLCommons.handlerToGraphQLObject;

import java.util.Collection;
import java.util.HashMap;

import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;
import graphql.servlet.GraphQLMutationProvider;
import graphql.servlet.GraphQLQueryProvider;

public class LdapGraphQLProvider implements GraphQLQueryProvider, GraphQLMutationProvider {

    @Override
    public Collection<GraphQLFieldDefinition> getMutations() {
        return handlerActionsToGraphQLFieldDefinition(new LdapActionHandler().getPersistActions());
    }

    @Override
    public GraphQLObjectType getQuery() {
        return handlerToGraphQLObject(new LdapActionHandler());
    }

    @Override
    public Object context() {
        return new HashMap<>();
    }
}
