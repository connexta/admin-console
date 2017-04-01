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

import static org.codice.ddf.admin.api.fields.UnionField.FIELD_TYPE_NAME_KEY;
import static org.codice.ddf.admin.graphql.common.GraphQLTransformCommons.capitalize;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.codice.ddf.admin.api.fields.EnumField;
import org.codice.ddf.admin.api.fields.Field;
import org.codice.ddf.admin.api.fields.ListField;
import org.codice.ddf.admin.api.fields.ObjectField;
import org.codice.ddf.admin.api.fields.UnionField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import graphql.Scalars;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLOutputType;
import graphql.schema.GraphQLScalarType;
import graphql.schema.GraphQLUnionType;
import graphql.schema.TypeResolver;

public class GraphQLTransformOutput {

    private static final Logger LOGGER = LoggerFactory.getLogger(GraphQLTransformOutput.class);

    public static GraphQLOutputType fieldToGraphQLOutputType(Field field) {
        switch (field.fieldBaseType()) {
        case OBJECT:
            return fieldToGraphQLObjectType(field);
        case ENUM:
            return GraphQLTransformCommons.enumFieldToGraphQLEnumType((EnumField) field);
        case LIST:
            return new GraphQLList(fieldToGraphQLOutputType(((ListField) field).getListFieldType()));
        case INTEGER:
            if (field.fieldTypeName() == null) {
                return Scalars.GraphQLInt;
            }
            return new GraphQLScalarType(field.fieldTypeName(),
                    field.description(),
                    Scalars.GraphQLInt.getCoercing());
        case BOOLEAN:
            if (field.fieldTypeName() == null) {
                return Scalars.GraphQLBoolean;
            }
            return new GraphQLScalarType(field.fieldTypeName(),
                    field.description(),
                    Scalars.GraphQLBoolean.getCoercing());
        case STRING:
            if (field.fieldTypeName() == null) {
                return Scalars.GraphQLString;
            }
            return new GraphQLScalarType(field.fieldTypeName(),
                    field.description(),
                    Scalars.GraphQLString.getCoercing());
        case UNION:
            return unionToGraphQLOutputType((UnionField) field);
        }

        return Scalars.GraphQLString;
    }

    public static GraphQLFieldDefinition fieldToGraphQLFieldDefinition(Field field) {
        switch (field.fieldBaseType()) {
        case UNION:
            return GraphQLFieldDefinition.newFieldDefinition()
                    .name(field.fieldName())
                    .description(field.description())
                    .type(GraphQLTransformOutput.fieldToGraphQLOutputType(field))
                    //                    .dataFetcher(fetcher -> unionTypeDataFetch(field))
                    .build();
        default:
            return GraphQLFieldDefinition.newFieldDefinition()
                    .name(field.fieldName())
                    .description(field.description())
                    .type(GraphQLTransformOutput.fieldToGraphQLOutputType(field))
                    .build();
        }
    }

    public static List<GraphQLFieldDefinition> fieldsToGraphQLFieldDefinition(
            List<? extends Field> fields) {
        if (fields == null) {
            return new ArrayList<>();
        }
        return fields.stream()
                .map(field -> fieldToGraphQLFieldDefinition(field))
                .collect(Collectors.toList());
    }

    public static GraphQLObjectType fieldToGraphQLObjectType(Field field) {
        switch (field.fieldBaseType()) {
        case OBJECT:
            //Add on Payload to avoid collision between an input and output field type name;
            return GraphQLObjectType.newObject()
                    .name(capitalize(field.fieldTypeName()) + "Payload")
                    .description(field.description())
                    .fields(fieldsToGraphQLFieldDefinition(((ObjectField) field).getFields()))
                    .build();
        default:
            throw new RuntimeException("Unknown ObjectType: " + field.fieldBaseType());
        }
    }

    public static GraphQLOutputType unionToGraphQLOutputType(UnionField field) {
        GraphQLObjectType[] unionValues = field.getUnionTypes()
                .stream()
                .map(GraphQLTransformOutput::fieldToGraphQLObjectType)
                .toArray(GraphQLObjectType[]::new);

        return GraphQLUnionType.newUnionType()
                .name(field.fieldTypeName())
                .description(field.description())
                .typeResolver(new UnionTypeResolver(unionValues))
                .possibleTypes(unionValues)
                .build();
    }

    public static class UnionTypeResolver implements TypeResolver {

        List<GraphQLObjectType> supportedTypes;

        //The graphql library requires the same object reference it was given to build the schema
        //So we have to keep track of the objects and match them after being processed by the action datafetcher
        public UnionTypeResolver(GraphQLObjectType... supportedTypes) {
            this.supportedTypes = Arrays.asList(supportedTypes);
        }

        @Override
        public GraphQLObjectType getType(Object object) {
            if (!(object instanceof Map) || ((Map) object).get(FIELD_TYPE_NAME_KEY) == null) {
                LOGGER.error("Cannot handle supposed union object: " + object.toString());
                throw new RuntimeException(
                        "Cannot handle supposed union object: " + object.toString());
            }

            String fieldTypeName = (String) ((Map<String, Object>) object).get(FIELD_TYPE_NAME_KEY);
            String payloadFieldTypeName = fieldTypeName + "Payload";
            Optional<GraphQLObjectType> foundUnionType = supportedTypes.stream()
                    .filter(type -> type.getName()
                            .equals(payloadFieldTypeName))
                    .findFirst();

            if (!foundUnionType.isPresent()) {
                LOGGER.error("UNKNOWN UNION TYPE: " + fieldTypeName);
                throw new RuntimeException("UNKNOWN UNION TYPE: " + payloadFieldTypeName);
            }

            return foundUnionType.get();
        }
    }
}