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
package org.codice.ddf.admin.query.graphql;

import static org.codice.ddf.admin.query.graphql.GraphQLCommons.capitalize;
import static org.codice.ddf.admin.query.graphql.GraphQLCommons.enumFieldToGraphQLEnumType;
import static graphql.Scalars.GraphQLBoolean;
import static graphql.Scalars.GraphQLInt;
import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLInputObjectField.newInputObjectField;
import static graphql.schema.GraphQLInputObjectType.newInputObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.api.fields.ListField;
import org.codice.ddf.admin.query.commons.fields.base.BaseEnumField;
import org.codice.ddf.admin.query.commons.fields.base.BaseObjectField;

import graphql.schema.GraphQLInputObjectField;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLScalarType;

public class GraphQLInput {

    public static GraphQLInputObjectField fieldToGraphQLInputFieldDefinition(Field field) {
        return newInputObjectField().name(field.fieldName())
                .description(field.description())
                .type(fieldTypeToGraphQLInputType(field))
                .build();
    }

    public static GraphQLInputType fieldTypeToGraphQLInputType(Field field) {
        switch (field.fieldBaseType()) {
        case OBJECT:
            return objectFieldToGraphQLInputType((BaseObjectField) field);
        case ENUM:
            return enumFieldToGraphQLEnumType((BaseEnumField) field);
        case LIST:
            return listFieldToGraphQLInputType((ListField)field);
        case INTEGER:
            if(field.fieldTypeName() == null) {
                return GraphQLInt;
            }
            return new GraphQLScalarType(field.fieldTypeName(), field.description(), GraphQLInt.getCoercing());
        case BOOLEAN:
            if(field.fieldTypeName() == null) {
                return GraphQLBoolean;
            }
            return new GraphQLScalarType(field.fieldTypeName(), field.description(), GraphQLBoolean.getCoercing());
        case STRING:
            if(field.fieldTypeName() == null) {
                return GraphQLString;
            }
            return new GraphQLScalarType(field.fieldTypeName(), field.description(), GraphQLString.getCoercing());
        }
        return null;
    }

    public static GraphQLInputType objectFieldToGraphQLInputType(
            BaseObjectField field) {
        List<GraphQLInputObjectField> fieldDefinitions = new ArrayList<>();
        if(field.getFields() != null) {
            fieldDefinitions = field.getFields()
                    .stream()
                    .map(GraphQLInput::fieldToGraphQLInputFieldDefinition)
                    .collect(Collectors.toList());
        }

        return newInputObject().name(capitalize(field.fieldTypeName()))
                .description(field.description())
                .fields(fieldDefinitions)
                .build();
    }

    public static GraphQLInputType listFieldToGraphQLInputType(ListField listField) {
        return new GraphQLList(fieldTypeToGraphQLInputType(listField.getListFieldType()));
    }
}