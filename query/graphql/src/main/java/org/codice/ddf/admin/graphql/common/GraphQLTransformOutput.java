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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.codice.ddf.admin.api.DataType;
import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.api.FieldProvider;
import org.codice.ddf.admin.api.fields.EnumField;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.api.fields.ListField;
import org.codice.ddf.admin.api.fields.ObjectField;
import org.codice.ddf.admin.api.fields.UnionField;
import org.codice.ddf.admin.api.report.FunctionReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import graphql.Scalars;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLNonNull;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLOutputType;
import graphql.schema.GraphQLScalarType;
import graphql.schema.GraphQLTypeReference;
import graphql.schema.GraphQLUnionType;
import graphql.schema.TypeResolver;

public class GraphQLTransformOutput {

    private static final Logger LOGGER = LoggerFactory.getLogger(GraphQLTransformOutput.class);
    private GraphQLTransformInput transformInput;
    private Set<String> createdObjectFieldTypeNames;

    public GraphQLTransformOutput() {
        transformInput = new GraphQLTransformInput();
        createdObjectFieldTypeNames = new HashSet<>();
    }

    public GraphQLObjectType queryProviderToGraphQLObjectType(FieldProvider provider) {
        createdObjectFieldTypeNames.add(provider.fieldTypeName());
        return GraphQLObjectType.newObject()
                .name(provider.fieldTypeName())
                .description(provider.description())
                .fields(fieldsToGraphQLFieldDefinition(provider.getDiscoveryFields()))
                .build();
    }

    public GraphQLOutputType fieldToGraphQLOutputType(DataType field) {
        switch (field.baseDataType()) {
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


    public List<GraphQLFieldDefinition> fieldsToGraphQLFieldDefinition(
            List<? extends Field> fields) {
        if (fields == null) {
            return new ArrayList<>();
        }
        return fields.stream()
                .map(field -> fieldToGraphQLFieldDefinition(field))
                .collect(Collectors.toList());
    }
    public GraphQLFieldDefinition fieldToGraphQLFieldDefinition(Field field) {
        List<GraphQLArgument> graphQLArgs = new ArrayList<>();
        DataType returnType;

        if(field instanceof FunctionField) {
            FunctionField function = (FunctionField) field;
            if (function.getArguments() != null) {
                function.getArguments()
                        .forEach(f -> graphQLArgs.add(fieldToGraphQLArgument((DataType) f)));
            }
            returnType = function.getReturnType();
        } else {
            returnType = (DataType) field;
        }

        return GraphQLFieldDefinition.newFieldDefinition()
                    .name(field.fieldName())
                    .description(field.description())
                    .type(fieldToGraphQLOutputType(returnType))
                    .argument(graphQLArgs)
                    .dataFetcher(field instanceof FunctionField ? (env -> functionDataFetcher(env, (FunctionField)field)) : (env -> dataTypeDataFetcher(env, (DataType)field)))
                    .build();
    }

    public FunctionReport functionDataFetcher(DataFetchingEnvironment env, FunctionField field) {
        Map<String, Object> args = new HashMap<>();
        if (env.getArguments() != null) {
            args.putAll(env.getArguments());
        }

        FunctionField<DataType> funcField = field.newInstance();

        //Remove the field name of the function from that path since the update is using a subpath
        List<String> fixedPath = Lists.newArrayList(field.path());
        fixedPath.remove(fixedPath.size() - 1);
        funcField.updatePath(fixedPath);
        funcField.setValue(env.getArguments());
        FunctionReport result = funcField.getValue();
        return result;
    }

    public Object dataTypeDataFetcher(DataFetchingEnvironment env, DataType field) {
        Object source = env.getSource();
        //If no values are passed for the source, return a field definition to continue the execution strategy instead of returning null. This is an expansion of the PropertyDataFetcher
        if (source instanceof Map) {
            if(!((Map) source).isEmpty()) {
                return ((Map<?, ?>) source).get(field.fieldName());
            }
        }

        return field.getValue();
    }

    public GraphQLArgument fieldToGraphQLArgument(DataType field) {
        GraphQLInputType graphqlInputType = transformInput.fieldTypeToGraphQLInputType(field);

        return GraphQLArgument.newArgument()
                .name(field.fieldName())
                .description(field.description())
                .type(field.isRequired() ? new GraphQLNonNull(graphqlInputType) : graphqlInputType)
                .build();
    }

    public GraphQLOutputType fieldToGraphQLObjectType(DataType field) {
        //Checks if it's a recursive reference to the queryProvider
        if(createdObjectFieldTypeNames.contains(field.fieldTypeName())) {
            return new GraphQLTypeReference(field.fieldTypeName());
        }

        String typeName = createOutputObjectFieldTypeName(field.fieldTypeName());
        if (createdObjectFieldTypeNames.contains(typeName)) {
            return new GraphQLTypeReference(typeName);
        } else {
            createdObjectFieldTypeNames.add(typeName);
            return GraphQLObjectType.newObject()
                    .name(typeName)
                    .description(field.description())
                    .fields(fieldsToGraphQLFieldDefinition(((ObjectField) field).getFields()))
                    .build();
        }
    }

    //Add on Payload to avoid collision between an input and output field type name;
    public String createOutputObjectFieldTypeName(String fieldTypeName) {
        return capitalize(fieldTypeName) + "Payload";
    }

    // TODO: tbatie - 5/15/17 - Replace unions with interface
    public GraphQLOutputType unionToGraphQLOutputType(UnionField field) {
        GraphQLObjectType[] unionValues = field.getUnionTypes()
                .stream()
                .map(fi -> fieldToGraphQLObjectType(fi))
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
        //So we have to keep track of the objects and match them after being processed by the datafetcher
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