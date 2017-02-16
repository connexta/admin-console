package org.codice.ddf.admin.query.graphql;

import static org.codice.ddf.admin.query.graphql.GraphQLCommons.capitalize;
import static org.codice.ddf.admin.query.graphql.GraphQLCommons.enumFieldToGraphQLEnumType;
import static graphql.Scalars.GraphQLInt;
import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLInputObjectField.newInputObjectField;
import static graphql.schema.GraphQLInputObjectType.newInputObject;

import java.util.List;
import java.util.stream.Collectors;

import org.codice.ddf.admin.query.api.field.Field;
import org.codice.ddf.admin.query.commons.field.BaseFields;

import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLInputObjectField;
import graphql.schema.GraphQLInputType;

public class GraphQLInput {

    public static GraphQLInputType fieldTypeToGraphQLInputType(Field field) {
        switch (field.fieldType()) {
        case OBJECT:
            return objectFieldToGraphQLInputType((BaseFields.ObjectField) field);
        case ENUM:
            return enumFieldToGraphQLEnumType((BaseFields.EnumField) field);
        case INTEGER:
            return GraphQLInt;
        case STRING:
            return GraphQLString;
        }
        return GraphQLString;
    }

    public static GraphQLArgument fieldToGraphQLArgument(Field field) {
        return newArgument().name(field.fieldName())
                .description(field.description())
                .type(fieldTypeToGraphQLInputType(field))
                .build();
    }

    public static GraphQLInputObjectField fieldToGraphQLInputFieldDefinition(Field field) {
        return newInputObjectField().name(field.fieldName())
                .description(field.description())
                .type(fieldTypeToGraphQLInputType(field))
                .build();
    }

    public static GraphQLInputType objectFieldToGraphQLInputType(
            BaseFields.ObjectField field) {
        List<GraphQLInputObjectField> fieldDefinitions = field.getFields()
                .stream()
                .map(f -> fieldToGraphQLInputFieldDefinition(f))
                .collect(Collectors.toList());

        return newInputObject().name(capitalize(field.fieldName()))
                .description(field.description())
                .fields(fieldDefinitions)
                .build();
    }
}
