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

import static org.codice.ddf.admin.query.commons.fields.base.BaseUnionField.FIELD_TYPE_NAME_KEY;
import static org.codice.ddf.admin.query.graphql.GraphQLCommons.enumFieldToGraphQLEnumType;
import static org.codice.ddf.admin.query.graphql.GraphQLCommons.fieldToGraphQLObjectType;
import static graphql.Scalars.GraphQLBoolean;
import static graphql.Scalars.GraphQLInt;
import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLUnionType.newUnionType;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.api.fields.ListField;
import org.codice.ddf.admin.query.api.fields.UnionField;
import org.codice.ddf.admin.query.commons.fields.base.BaseEnumField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import graphql.schema.GraphQLList;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLOutputType;
import graphql.schema.GraphQLScalarType;
import graphql.schema.TypeResolver;

public class GraphQLOutput {

    private static final Logger LOGGER = LoggerFactory.getLogger(GraphQLOutput.class);

    public static GraphQLOutputType fieldToGraphQLOutputType(Field field) {
        switch (field.fieldBaseType()) {
        case OBJECT:
            return fieldToGraphQLObjectType(field);
        case ENUM:
            return enumFieldToGraphQLEnumType((BaseEnumField) field);
        case LIST:
            return new GraphQLList(fieldToGraphQLOutputType(((ListField)field).getListFieldType()));
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
        case UNION:
            return unionToGraphQLOutputType((UnionField) field);
        }

        return GraphQLString;
    }

//    public static GraphQLOutputType interfaceToGraphQLOutputType(InterfaceField field) {
//        return newInterface().name(field.fieldTypeName())
//                .description(field.description())
//                .typeResolver(new UnionTypeResolver())
//                .fields(fieldsToGraphQLFieldDefinition(field.getFields()))
//                .build();
//    }

    public static GraphQLOutputType unionToGraphQLOutputType(UnionField field) {
        GraphQLObjectType[] unionValues = field.getUnionTypes()
                .stream()
                .map(GraphQLCommons::fieldToGraphQLObjectType)
                .toArray(GraphQLObjectType[]::new);

        return newUnionType().name(field.fieldTypeName())
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
            if(!(object instanceof Map) || ((Map)object).get(FIELD_TYPE_NAME_KEY) == null) {
                LOGGER.error("Cannot handle supposed union object: " + object.toString());
                throw new RuntimeException("Cannot handle supposed union object: " + object.toString());
            }

            String fieldTypeName = (String) ((Map<String, Object>) object).get(FIELD_TYPE_NAME_KEY);
            String payloadFieldTypeName = fieldTypeName + "Payload";
            Optional<GraphQLObjectType> foundUnionType = supportedTypes.stream()
                    .filter(type -> type.getName()
                            .equals(payloadFieldTypeName))
                    .findFirst();

            if(!foundUnionType.isPresent()) {
                LOGGER.error("UNKNOWN UNION TYPE: " + fieldTypeName);
                throw new RuntimeException("UNKNOWN UNION TYPE: " + payloadFieldTypeName);
            }

            return foundUnionType.get();
        }
    }
}