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
package org.codice.ddf.admin.graphql.common;

import static org.codice.ddf.admin.graphql.common.GraphQLTransformCommons.enumFieldToGraphQLEnumType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.codice.ddf.admin.api.DataType;
import org.codice.ddf.admin.api.fields.EnumField;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.api.fields.ListField;
import org.codice.ddf.admin.api.fields.ObjectField;

import graphql.Scalars;
import graphql.schema.GraphQLInputObjectField;
import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLScalarType;

public class GraphQLTransformInput {

    public GraphQLInputType fieldTypeToGraphQLInputType(DataType field) {
        GraphQLInputType type = null;
        switch (field.baseDataType()) {
        case OBJECT:
            type = objectFieldToGraphQLInputType((ObjectField) field);
            break;
        case ENUM:
            type = enumFieldToGraphQLEnumType((EnumField) field);
            break;
        case LIST:
            type = new GraphQLList(fieldTypeToGraphQLInputType(((ListField) field).getListFieldType()));
            break;
        case INTEGER:
            if (field.fieldTypeName() == null) {
                type = Scalars.GraphQLInt;
            } else {
                type = new GraphQLScalarType(field.fieldTypeName(),
                        field.description(),
                        Scalars.GraphQLInt.getCoercing());
            }
            break;
        case BOOLEAN:
            if (field.fieldTypeName() == null) {
                type = Scalars.GraphQLBoolean;
            } else {
                type = new GraphQLScalarType(field.fieldTypeName(),
                        field.description(),
                        Scalars.GraphQLBoolean.getCoercing());
            }
            break;
        case STRING:
            if (field.fieldTypeName() == null) {
                type = Scalars.GraphQLString;
            } else {
                type = new GraphQLScalarType(field.fieldTypeName(),
                        field.description(),
                        Scalars.GraphQLString.getCoercing());
            }
            break;
        }

        if (type == null) {
            throw new RuntimeException(
                    "Error transforming input field to GraphQLInputType. Unknown field type: "
                            + field.baseDataType());
        }

        return type;
    }

    public GraphQLInputType objectFieldToGraphQLInputType(ObjectField field) {
        List<GraphQLInputObjectField> fieldDefinitions = new ArrayList<>();

        if (field.getFields() != null) {
            fieldDefinitions = field.getFields()
                    .stream()
                    .filter(input -> !(input instanceof FunctionField))
                    .map(input -> fieldToGraphQLInputObjectFieldDefinition((DataType) input))
                    .collect(Collectors.toList());
        }

        return GraphQLInputObjectType.newInputObject()
                .name(GraphQLTransformCommons.capitalize(field.fieldTypeName()))
                .description(field.description())
                .fields(fieldDefinitions)
                .build();
    }

    public GraphQLInputObjectField fieldToGraphQLInputObjectFieldDefinition(DataType field) {
        return GraphQLInputObjectField.newInputObjectField()
                .name(field.fieldName())
                .description(field.description())
                .type(fieldTypeToGraphQLInputType(field))
                .build();
    }
}