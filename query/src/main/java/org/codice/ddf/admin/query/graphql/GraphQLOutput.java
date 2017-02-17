package org.codice.ddf.admin.query.graphql;

import static org.codice.ddf.admin.query.graphql.GraphQLCommons.capitalize;
import static org.codice.ddf.admin.query.graphql.GraphQLCommons.enumFieldToGraphQLEnumType;
import static graphql.Scalars.GraphQLInt;
import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.codice.ddf.admin.query.api.field.Field;
import org.codice.ddf.admin.query.commons.field.BaseFields;

import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLOutputType;

public class GraphQLOutput {

    public static GraphQLFieldDefinition fieldToGraphQLFieldDefinition(Field field) {
        return newFieldDefinition().name(field.fieldName())
                .description(field.description())
                .type(fieldToGraphQLOutputType(field))
                .build();
    }

    public static GraphQLOutputType fieldToGraphQLOutputType(Field field) {
        switch (field.fieldBaseType()) {
        case OBJECT:
            return objectFieldToGraphQLOutputType((BaseFields.ObjectField) field);
        case ENUM:
            return enumFieldToGraphQLEnumType((BaseFields.EnumField) field);
        case LIST:
            return listFieldToGraphQLOutputType((BaseFields.ListField)field);
        case INTEGER:
            return GraphQLInt;
        case STRING:
            return GraphQLString;
        }
        return GraphQLString;
    }

    public static GraphQLOutputType objectFieldToGraphQLOutputType(BaseFields.ObjectField field) {
        List<GraphQLFieldDefinition> fieldDefinitions = new ArrayList<>();
        if(field.getFields() != null) {
            fieldDefinitions = field.getFields()
                    .stream()
                    .map(GraphQLOutput::fieldToGraphQLFieldDefinition)
                    .collect(Collectors.toList());
        }
        return newObject().name(capitalize(field.fieldTypeName()))
                .description(field.description())
                .fields(fieldDefinitions)
                .build();
    }

    public static GraphQLOutputType listFieldToGraphQLOutputType(BaseFields.ListField listField) {
        return new GraphQLList(fieldToGraphQLOutputType(listField.getListValueField()));
    }
}
