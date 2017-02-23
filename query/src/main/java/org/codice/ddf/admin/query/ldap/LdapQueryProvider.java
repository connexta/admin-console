package org.codice.ddf.admin.query.ldap;

import static org.codice.ddf.admin.query.graphql.GraphQLCommons.handlerToGraphQLObject;

import java.util.HashMap;

import graphql.schema.GraphQLObjectType;
import graphql.servlet.GraphQLQueryProvider;

public class LdapQueryProvider implements GraphQLQueryProvider {

    @Override
    public GraphQLObjectType getQuery() {
        return handlerToGraphQLObject(new LdapActionHandler());
    }

    @Override
    public Object context() {
        return new HashMap<>();
    }
}
