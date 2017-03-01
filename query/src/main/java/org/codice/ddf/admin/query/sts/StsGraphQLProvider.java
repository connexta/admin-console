package org.codice.ddf.admin.query.sts;

import static org.codice.ddf.admin.query.graphql.GraphQLCommons.fieldToGraphQLObjectType;

import java.util.HashMap;

import graphql.schema.GraphQLObjectType;
import graphql.servlet.GraphQLQueryProvider;

public class StsGraphQLProvider implements GraphQLQueryProvider {
    @Override
    public GraphQLObjectType getQuery() {
        return fieldToGraphQLObjectType(new StsActionHandler());
    }

    @Override
    public String getName() {
        return new StsActionHandler().fieldName();
    }

    @Override
    public Object context() {
        return new HashMap<>();
    }
}
