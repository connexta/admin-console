package org.codice.ddf.admin.query.relay;

import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

import java.util.HashMap;

import graphql.schema.GraphQLNonNull;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLTypeReference;
import graphql.servlet.GraphQLQueryProvider;

public class RelayGraphQLProvider implements GraphQLQueryProvider {
    @Override
    public GraphQLObjectType getQuery() {
        return newObject().name("Relay")
                .description(
                        "Workaround for https://github.com/facebook/relay/issues/112 re-exposing the root query object")
                .field(newFieldDefinition().name("query")
                        .description("The query root of the GraphQL interface.")
                        .type((new GraphQLTypeReference("query")))
                        .staticValue(new HashMap<>()))
                .build();
    }

    @Override
    public String getName() {
        return "relay";
    }

    @Override
    public Object context() {
        return new HashMap<>();
    }
}


