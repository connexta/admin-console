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
package org.codice.ddf.admin.graphql.service;

import static org.codice.ddf.admin.graphql.common.GraphQLCommons.actionCreatorToGraphQLObjectType;
import static org.codice.ddf.admin.graphql.common.GraphQLCommons.actionsToGraphQLFieldDef;

import java.util.Collection;
import java.util.HashMap;

import org.codice.ddf.admin.api.action.ActionCreator;

import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;
import graphql.servlet.GraphQLMutationProvider;
import graphql.servlet.GraphQLQueryProvider;

public class GraphQLProviderImpl implements GraphQLMutationProvider, GraphQLQueryProvider {

    private ActionCreator creator;

    public GraphQLProviderImpl(ActionCreator creator) {
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
