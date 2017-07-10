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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.codice.ddf.admin.api.DataType;
import org.codice.ddf.admin.api.fields.EnumField;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.api.fields.ListField;
import org.codice.ddf.admin.api.fields.ObjectField;
import org.codice.ddf.admin.api.fields.ScalarField;
import org.codice.ddf.admin.graphql.GraphQLTypesProviderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLInputObjectField;
import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLNonNull;
import graphql.servlet.GraphQLTypesProvider;

public class GraphQLTransformInput {

    private static final Logger LOGGER = LoggerFactory.getLogger(GraphQLTransformInput.class);

    private GraphQLTransformScalar transformScalars;
    private GraphQLTransformEnum transformEnum;
    private GraphQLTypesProviderImpl inputTypesProvider;

    public GraphQLTransformInput(GraphQLTransformScalar transformScalars, GraphQLTransformEnum transformEnum) {
        inputTypesProvider = new GraphQLTypesProviderImpl();
        this.transformScalars = transformScalars;
        this.transformEnum = transformEnum;
    }


    public GraphQLArgument fieldToGraphQLArgument(DataType field) {
        GraphQLInputType graphqlInputType = fieldTypeToGraphQLInputType(field);

        return GraphQLArgument.newArgument()
                .name(field.fieldName())
                .description(field.description())
                .type(field.isRequired() ? new GraphQLNonNull(graphqlInputType) : graphqlInputType)
                .build();
    }

    public GraphQLInputType fieldTypeToGraphQLInputType(DataType field) {
        if(inputTypesProvider.isTypePresent(field.fieldTypeName())) {
            return inputTypesProvider.getType(field.fieldTypeName());
        }

        GraphQLInputType type = null;

        if(field instanceof ObjectField) {
            type = objectFieldToGraphQLInputType((ObjectField) field);
        } else if(field instanceof EnumField) {
            type = transformEnum.enumFieldToGraphQLEnumType((EnumField) field);
        } else if(field instanceof ListField) {
            try {
                type = new GraphQLList(fieldTypeToGraphQLInputType(((ListField<DataType>) field).createListEntry()));
            } catch (Exception e) {
                throw new RuntimeException("Unable to build field list content type for input type: " + field.fieldName());
            }
        } else if(field instanceof ScalarField){
            type = transformScalars.resolveScalarType((ScalarField) field);
        }

        if (type == null) {
            throw new RuntimeException(
                    "Error transforming input field to GraphQLInputType. Unknown field type: "
                            + field.getClass());
        }

        if(field.fieldTypeName() != null) {
            inputTypesProvider.addType(field.fieldTypeName(), type);
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

    public GraphQLTypesProvider getInputTypeProvider() {
        return inputTypesProvider;
    }
}