/**
 * Copyright (c) Codice Foundation
 *
 * <p>This is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version.
 *
 * <p>This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details. A copy of the GNU Lesser General Public
 * License is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 */
package org.codice.ddf.admin.graphql.transform;

import com.google.common.collect.ImmutableList;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLOutputType;
import graphql.schema.GraphQLTypeReference;
import graphql.servlet.GraphQLTypesProvider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.api.FieldProvider;
import org.codice.ddf.admin.api.fields.EnumField;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.api.fields.ListField;
import org.codice.ddf.admin.api.fields.ObjectField;
import org.codice.ddf.admin.api.fields.ScalarField;
import org.codice.ddf.admin.api.report.FunctionReport;
import org.codice.ddf.admin.graphql.GraphQLTypesProviderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GraphQLTransformOutput {

  private static final Logger LOGGER = LoggerFactory.getLogger(GraphQLTransformOutput.class);
  private GraphQLTransformInput inputTransformer;
  private GraphQLTransformScalar transformScalar;
  private GraphQLTransformEnum transformEnum;
  private GraphQLTypesProviderImpl<GraphQLOutputType> outputTypeProvider;
  private GraphQLTypesProviderImpl<GraphQLTypeReference> referenceTypeProvider;

  public GraphQLTransformOutput() {
    transformScalar = new GraphQLTransformScalar();
    transformEnum = new GraphQLTransformEnum();
    inputTransformer = new GraphQLTransformInput(transformScalar, transformEnum);
    outputTypeProvider = new GraphQLTypesProviderImpl<>();
    referenceTypeProvider = new GraphQLTypesProviderImpl<>();
  }

  public GraphQLOutputType fieldToGraphQLOutputType(Field field) {
    if (outputTypeProvider.isTypePresent(field.getFieldType())) {
      return outputTypeProvider.getType(field.getFieldType());
    }

    GraphQLOutputType type = null;

    if (field instanceof ObjectField) {
      type = fieldToGraphQLObjectType((ObjectField) field);
    } else if (field instanceof EnumField) {
      type = transformEnum.enumFieldToGraphQLEnumType((EnumField) field);
    } else if (field instanceof ListField) {
      try {
        type =
            new GraphQLList(fieldToGraphQLOutputType(((ListField<Field>) field).createListEntry()));
      } catch (Exception e) {
        throw new RuntimeException(
            "Unable to build field list content type for output type: " + field.getFieldName());
      }
    } else if (field instanceof ScalarField) {
      type = transformScalar.resolveScalarType((ScalarField) field);
    }

    if (type == null) {
      throw new RuntimeException(
          "Error transforming output field to GraphQLOutputType. Unknown field base type: "
              + field.getClass());
    }

    outputTypeProvider.addType(field.getFieldType(), type);
    return type;
  }

  public GraphQLOutputType fieldToGraphQLObjectType(ObjectField field) {
    // Check if the objectField is recursive, if so bail early
    if (referenceTypeProvider.isTypePresent(field.getFieldType())) {
      return referenceTypeProvider.getType(field.getFieldType());
    }

    // Field provider names should be unique and looks pretty without "Payload" added to the name
    String typeName =
        field instanceof FieldProvider
            ? field.getFieldType()
            : createOutputObjectFieldTypeName(field.getFieldType());

    List<GraphQLFieldDefinition> innerFields = fieldsToGraphQLFieldDefinition(field.getFields());

    // Skip mutations on field provider
    if (field instanceof FieldProvider) {
      innerFields.addAll(
          functionsToGraphQLFieldDefinition(((FieldProvider) field).getDiscoveryFunctions()));
    }

    // Add a GraphQLTypeReference to support recursion
    referenceTypeProvider.addType(field.getFieldType(), new GraphQLTypeReference(typeName));
    return GraphQLObjectType.newObject()
        .name(typeName)
        .description(field.getDescription())
        .fields(innerFields)
        .build();
  }

  public List<GraphQLFieldDefinition> fieldsToGraphQLFieldDefinition(List<? extends Field> fields) {
    if (fields == null) {
      return new ArrayList<>();
    }
    return fields.stream().map(this::fieldToGraphQLFieldDefinition).collect(Collectors.toList());
  }

  public List<GraphQLFieldDefinition> functionsToGraphQLFieldDefinition(
      List<? extends FunctionField> funcFields) {
    if (funcFields == null) {
      return new ArrayList<>();
    }
    return funcFields
        .stream()
        .map(this::functionToGraphQLFieldDefinition)
        .collect(Collectors.toList());
  }

  public GraphQLFieldDefinition fieldToGraphQLFieldDefinition(Field field) {
    List<GraphQLArgument> graphQLArgs = new ArrayList<>();

    return GraphQLFieldDefinition.newFieldDefinition()
        .name(field.getFieldName())
        .description(field.getDescription())
        .type(fieldToGraphQLOutputType(field))
        .argument(graphQLArgs)
        .dataFetcher(env -> fieldDataFetcher(env, field))
        .build();
  }

  public GraphQLFieldDefinition functionToGraphQLFieldDefinition(FunctionField function) {
    List<GraphQLArgument> graphQLArgs = new ArrayList<>();

    if (function.getArguments() != null) {
      function
          .getArguments()
          .forEach(f -> graphQLArgs.add(inputTransformer.fieldToGraphQLArgument((Field) f)));
    }

    return GraphQLFieldDefinition.newFieldDefinition()
        .name(function.getFunctionName())
        .description(function.getDescription())
        .type(fieldToGraphQLOutputType(function.getReturnType()))
        .argument(graphQLArgs)
        .dataFetcher((env -> functionDataFetcher(env, function)))
        .build();
  }

  public Object functionDataFetcher(DataFetchingEnvironment env, FunctionField<Field> field) {
    Map<String, Object> args = new HashMap<>();
    if (env.getArguments() != null) {
      args.putAll(env.getArguments());
    }

    FunctionField<Field> funcField = field.newInstance();
    FunctionReport<Field> result =
        funcField.execute(args, env.getFieldTypeInfo().getPath().toList());

    if (!result.getErrorMessages().isEmpty()) {
      throw new FunctionDataFetcherException(
          funcField.getFunctionName(),
          funcField
              .getArguments()
              .stream()
              .map(Field::getSanitizedValue)
              .collect(Collectors.toList()),
          result.getErrorMessages());
    } else if (result.isResultPresent()) {
      return result.getResult().getSanitizedValue();
    }

    return null;
  }

  public Object fieldDataFetcher(DataFetchingEnvironment env, Field field) {
    Object source = env.getSource();
    // If no values are passed for the source, return a field definition to continue the execution
    // strategy instead of returning null. This is an expansion of the PropertyDataFetcher
    if (source instanceof Map) {
      if (!((Map) source).isEmpty()) {
        return ((Map<?, ?>) source).get(field.getFieldName());
      }
    }

    return field.getSanitizedValue();
  }

  // Add on Payload to avoid collision between an input and output field type name;
  public String createOutputObjectFieldTypeName(String fieldTypeName) {
    return GraphQLTransformCommons.capitalize(fieldTypeName) + "Payload";
  }

  // Omit the referenceTypeProvider intentionally since all the types should already be defined by
  // the other providers
  public List<GraphQLTypesProvider> getTypeProviders() {
    return ImmutableList.of(
        inputTransformer.getInputTypeProvider(),
        transformScalar.getScalarTypesProvider(),
        transformEnum.getEnumTypeProvider(),
        outputTypeProvider);
  }
}
