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

import java.util.HashMap;
import java.util.Map;

import org.codice.ddf.admin.api.DataType;
import org.codice.ddf.admin.api.fields.EnumField;

import graphql.schema.GraphQLEnumType;

public class GraphQLTransformEnum {
    private Map<String, GraphQLEnumType> predefinedEnums;

    public GraphQLTransformEnum() {
        this.predefinedEnums = new HashMap<>();
    }

    public GraphQLEnumType enumFieldToGraphQLEnumType(EnumField field) {

        if(predefinedEnums.containsKey(field.fieldTypeName())) {
            return predefinedEnums.get(field.fieldTypeName());
        }

        GraphQLEnumType.Builder builder = GraphQLEnumType.newEnum()
                .name(GraphQLTransformCommons.capitalize(field.fieldTypeName()))
                .description(field.description());

        field.getEnumValues()
                .forEach(val -> builder.value(((DataType) val).fieldName(),
                        ((DataType) val).getValue(),
                        ((DataType) val).description()));

        GraphQLEnumType newEnum = builder.build();
        predefinedEnums.put(field.fieldTypeName(), newEnum);
        return newEnum;
    }
}
