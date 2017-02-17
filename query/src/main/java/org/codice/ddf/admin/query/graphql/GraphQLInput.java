package org.codice.ddf.admin.query.graphql;

import static org.codice.ddf.admin.query.graphql.GraphQLCommons.capitalize;
import static org.codice.ddf.admin.query.graphql.GraphQLCommons.enumFieldToGraphQLEnumType;
import static graphql.Scalars.GraphQLInt;
import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLInputObjectField.newInputObjectField;
import static graphql.schema.GraphQLInputObjectType.newInputObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.codice.ddf.admin.query.api.field.Field;
import org.codice.ddf.admin.query.commons.field.BaseFields;

import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLInputObjectField;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLList;

public class GraphQLInput {


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

    public static GraphQLInputType fieldTypeToGraphQLInputType(Field field) {
        switch (field.fieldBaseType()) {
        case OBJECT:
            return objectFieldToGraphQLInputType((BaseFields.ObjectField) field);
        case ENUM:
            return enumFieldToGraphQLEnumType((BaseFields.EnumField) field);
        case LIST:
            return listFieldToGraphQLInputType((BaseFields.ListField)field);
        case INTEGER:
            return GraphQLInt;
        case STRING:
            return GraphQLString;
        }
        return GraphQLString;
    }

    public static GraphQLInputType objectFieldToGraphQLInputType(
            BaseFields.ObjectField field) {
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

    public static GraphQLInputType listFieldToGraphQLInputType(BaseFields.ListField listField) {
        return new GraphQLList(fieldTypeToGraphQLInputType(listField.getListValueField()));
    }
}
