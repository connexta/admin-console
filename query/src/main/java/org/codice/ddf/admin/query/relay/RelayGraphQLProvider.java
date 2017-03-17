/**
 * Copyright (c) Codice Foundation
 * <p>
 * This is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details. A copy of the GNU Lesser General Public License
 * is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 **/
package org.codice.ddf.admin.query.relay;

import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

import java.util.HashMap;

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


