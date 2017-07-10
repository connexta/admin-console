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
package org.codice.ddf.admin.graphql.transform;

import org.codice.ddf.admin.api.fields.EnumField;
import org.codice.ddf.admin.api.fields.EnumValue;
import org.codice.ddf.admin.graphql.GraphQLTypesProviderImpl;

import graphql.schema.GraphQLEnumType;
import graphql.servlet.GraphQLTypesProvider;

public class GraphQLTransformEnum {

    private GraphQLTypesProviderImpl enumTypeProvider;

    public GraphQLTransformEnum() {
        this.enumTypeProvider = new GraphQLTypesProviderImpl();
    }

    public GraphQLEnumType enumFieldToGraphQLEnumType(EnumField<Object, EnumValue<Object>> field) {

        if(enumTypeProvider.isTypePresent(field.fieldTypeName())) {
            return enumTypeProvider.getType(field.fieldTypeName());
        }

        GraphQLEnumType.Builder builder = GraphQLEnumType.newEnum()
                .name(GraphQLTransformCommons.capitalize(field.fieldTypeName()))
                .description(field.description());

        field.getEnumValues()
                .forEach(val -> builder.value(val.enumTitle(), val.value(), val.description()));

        GraphQLEnumType newEnum = builder.build();
        enumTypeProvider.addType(field.fieldTypeName(), newEnum);
        return newEnum;
    }

    public GraphQLTypesProvider getEnumTypeProvider() {
        return enumTypeProvider;
    }
}
