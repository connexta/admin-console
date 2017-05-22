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
package org.codice.ddf.admin.graphql;

import java.util.Collection;
import java.util.HashMap;

import org.codice.ddf.admin.api.FieldProvider;
import org.codice.ddf.admin.graphql.common.GraphQLTransformOutput;

import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;
import graphql.servlet.GraphQLMutationProvider;
import graphql.servlet.GraphQLQueryProvider;

public class GraphQLProviderImpl implements GraphQLMutationProvider, GraphQLQueryProvider {

    private FieldProvider creator;

    private GraphQLTransformOutput transformOutput;

    public GraphQLProviderImpl(FieldProvider creator, GraphQLTransformOutput transformOutput) {
        this.creator = creator;
        this.transformOutput = transformOutput;
    }

    @Override
    public Collection<GraphQLFieldDefinition> getMutations() {
        return transformOutput.fieldsToGraphQLFieldDefinition(creator.getMutationFunctions());
    }

    @Override
    public GraphQLObjectType getQuery() {
        return transformOutput.queryProviderToGraphQLObjectType(creator);
    }

    @Override
    public String getName() {
        return creator.fieldName();
    }

    @Override
    public Object context() {
        return new HashMap<>();
    }
}
