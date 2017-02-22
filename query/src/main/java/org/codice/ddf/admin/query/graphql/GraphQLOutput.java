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

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.commons.fields.base.EnumField;
import org.codice.ddf.admin.query.commons.fields.base.ListField;
import org.codice.ddf.admin.query.commons.fields.base.ObjectField;

import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLOutputType;
import graphql.schema.GraphQLScalarType;

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
            return objectFieldToGraphQLOutputType((ObjectField) field);
        case ENUM:
            return enumFieldToGraphQLEnumType((EnumField) field);
        case LIST:
            return listFieldToGraphQLOutputType((ListField)field);
        case INTEGER:
            if(field.fieldTypeName() == null) {
                return GraphQLInt;
            }
            return new GraphQLScalarType(field.fieldTypeName(), field.description(), GraphQLInt.getCoercing());

        case STRING:
            if(field.fieldTypeName() == null) {
                return GraphQLString;
            }
            return new GraphQLScalarType(field.fieldTypeName(), field.description(), GraphQLString.getCoercing());

        }
        return GraphQLString;
    }

    public static GraphQLOutputType objectFieldToGraphQLOutputType(ObjectField field) {
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

    public static GraphQLOutputType listFieldToGraphQLOutputType(ListField listField) {
        return new GraphQLList(fieldToGraphQLOutputType(listField.getListValueField()));
    }
}
