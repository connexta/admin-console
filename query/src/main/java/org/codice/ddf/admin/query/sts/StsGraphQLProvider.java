package org.codice.ddf.admin.query.sts;

import static org.codice.ddf.admin.query.graphql.GraphQLCommons.handlerToGraphQLObject;

import java.util.HashMap;

import graphql.schema.GraphQLObjectType;
import graphql.servlet.GraphQLQueryProvider;

public class StsGraphQLProvider implements GraphQLQueryProvider {
    @Override
    public GraphQLObjectType getQuery() {
        return handlerToGraphQLObject(new StsActionHandler());
    }

    @Override
    public Object context() {
        return new HashMap<>();
    }
}
