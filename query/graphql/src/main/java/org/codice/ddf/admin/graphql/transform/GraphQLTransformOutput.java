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

import static org.codice.ddf.admin.api.fields.UnionField.FIELD_TYPE_NAME_KEY;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.codice.ddf.admin.api.DataType;
import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.api.FieldProvider;
import org.codice.ddf.admin.api.fields.EnumField;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.api.fields.ListField;
import org.codice.ddf.admin.api.fields.ObjectField;
import org.codice.ddf.admin.api.fields.ScalarField;
import org.codice.ddf.admin.api.fields.UnionField;
import org.codice.ddf.admin.api.report.FunctionReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import graphql.TypeResolutionEnvironment;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLOutputType;
import graphql.schema.GraphQLTypeReference;
import graphql.schema.GraphQLUnionType;
import graphql.schema.TypeResolver;

public class GraphQLTransformOutput {

    private static final Logger LOGGER = LoggerFactory.getLogger(GraphQLTransformOutput.class);
    private GraphQLTransformInput inputTransformer;
    private GraphQLTransformScalar transformScalar;
    private GraphQLTransformEnum transformEnum;

    private Map<String, GraphQLOutputType> predefinedOutputTypes;

    public GraphQLTransformOutput() {
        transformScalar = new GraphQLTransformScalar();
        transformEnum = new GraphQLTransformEnum();
        inputTransformer = new GraphQLTransformInput(transformScalar, transformEnum);
        predefinedOutputTypes = new HashMap<>();
    }

    public GraphQLOutputType fieldToGraphQLOutputType(DataType field) {
        if (field.fieldTypeName() != null && predefinedOutputTypes.containsKey(field.fieldTypeName())) {
            return predefinedOutputTypes.get(field.fieldTypeName());
        }

        GraphQLOutputType type = null;

        if (field instanceof UnionField) {
            type = unionToGraphQLOutputType((UnionField) field);
        } else if (field instanceof ObjectField) {
            type = fieldToGraphQLObjectType((ObjectField)field);
        } else if (field instanceof EnumField) {
            type = transformEnum.enumFieldToGraphQLEnumType((EnumField) field);
        } else if (field instanceof ListField) {
            type = new GraphQLList(fieldToGraphQLOutputType(((ListField) field).getListFieldType()));
        } else if(field instanceof ScalarField) {
            type = transformScalar.resolveScalarType((ScalarField) field);
        }

        if (type == null) {
            throw new RuntimeException(
                    "Error transforming output field to GraphQLOutputType. Unknown field base type: "
                            + field.getClass());
        }

        if (field.fieldTypeName() != null && !predefinedOutputTypes.containsKey(field.fieldTypeName())) {
            predefinedOutputTypes.put(field.fieldTypeName(), type);
        }

        return type;
    }

    public GraphQLOutputType fieldToGraphQLObjectType(ObjectField field) {
        //Field provider names should be unique and looks pretty without "Payload" added to the name
        String typeName = field instanceof FieldProvider ?
                field.fieldTypeName() :
                createOutputObjectFieldTypeName(field.fieldTypeName());

        //Skip mutations on field provider
        List<Field> innerFields = field instanceof FieldProvider ?
                ((FieldProvider) field).getDiscoveryFields() :
                field.getFields();

        //Add a GraphQLTypeReference to support recursion
        predefinedOutputTypes.put(field.fieldTypeName(), new GraphQLTypeReference(typeName));
        return GraphQLObjectType.newObject()
                .name(typeName)
                .description(field.description())
                .fields(fieldsToGraphQLFieldDefinition(innerFields))
                .build();
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
                        .forEach(f -> graphQLArgs.add(inputTransformer.fieldToGraphQLArgument((DataType) f)));
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

    public Object functionDataFetcher(DataFetchingEnvironment env, FunctionField<DataType> field) {
        Map<String, Object> args = new HashMap<>();
        if (env.getArguments() != null) {
            args.putAll(env.getArguments());
        }

        FunctionField<DataType> funcField = field.newInstance();

        //Remove the field name of the function from that path since the update is using a subpath
        List<String> fixedPath = Lists.newArrayList(field.path());
        fixedPath.remove(fixedPath.size() - 1);
        funcField.updatePath(fixedPath);
        funcField.setValue(args);
        FunctionReport<DataType> result = funcField.getValue();

        if(!result.messages().isEmpty()) {
            throw new FunctionDataFetcherException(funcField.fieldName(), args, result.messages());
        } else if(result.isResultPresent()){
            return result.result().getValue();
        }

        return null;
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

    // TODO: tbatie - 5/15/17 - Replace unions with interface
    public GraphQLOutputType unionToGraphQLOutputType(UnionField field) {
        GraphQLObjectType[] unionValues = field.getUnionTypes()
                .stream()
                .map(fi -> fieldToGraphQLObjectType((ObjectField) fi))
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
        public GraphQLObjectType getType(TypeResolutionEnvironment typeResolutionEnvironment) {
            // TODO: tbatie - 6/7/17 -  See if you can return a type reference instad
            if (!(typeResolutionEnvironment.getObject() instanceof Map) || ((Map) typeResolutionEnvironment.getObject()).get(FIELD_TYPE_NAME_KEY) == null) {
                LOGGER.error("Cannot handle supposed union object: " + typeResolutionEnvironment.getObject().toString());
                throw new RuntimeException(
                        "Cannot handle supposed union object: " + typeResolutionEnvironment.getObject().toString());
            }

            String fieldTypeName = (String) ((Map<String, Object>) typeResolutionEnvironment.getObject()).get(FIELD_TYPE_NAME_KEY);
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


    //Add on Payload to avoid collision between an input and output field type name;
    public String createOutputObjectFieldTypeName(String fieldTypeName) {
        return GraphQLTransformCommons.capitalize(fieldTypeName) + "Payload";
    }
}