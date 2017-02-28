package org.codice.ddf.admin.query.graphql;

import static org.codice.ddf.admin.query.graphql.GraphQLCommons.capitalize;
import static org.codice.ddf.admin.query.graphql.GraphQLCommons.enumFieldToGraphQLEnumType;
import static org.codice.ddf.admin.query.graphql.GraphQLCommons.fieldsToGraphQLFieldDefinition;
import static graphql.Scalars.GraphQLInt;
import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLInterfaceType.newInterface;
import static graphql.schema.GraphQLObjectType.newObject;
import static graphql.schema.GraphQLUnionType.newUnionType;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.api.fields.InterfaceField;
import org.codice.ddf.admin.query.api.fields.ObjectField;
import org.codice.ddf.admin.query.api.fields.UnionField;
import org.codice.ddf.admin.query.commons.fields.base.BaseEnumField;
import org.codice.ddf.admin.query.commons.fields.base.ListField;

import graphql.schema.GraphQLList;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLOutputType;
import graphql.schema.GraphQLScalarType;

public class GraphQLOutput {

    public static GraphQLOutputType fieldToGraphQLOutputType(Field field) {
        switch (field.fieldBaseType()) {
        case OBJECT:
            return objectFieldToGraphQLOutputType((ObjectField) field);
        case ENUM:
            return enumFieldToGraphQLEnumType((BaseEnumField) field);
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
        case INTERFACE:
            return interfaceToGraphQLOutputType((InterfaceField) field);
        case UNION:
            return unionToGraphQLOutputType((UnionField) field);
        }

        return GraphQLString;
    }

    public static GraphQLObjectType objectFieldToGraphQLOutputType(ObjectField field) {
        return newObject().name(capitalize(field.fieldTypeName()) + "Payload")
                .description(field.description())
                .fields(fieldsToGraphQLFieldDefinition(field.getFields()))
                .build();
    }

    public static GraphQLOutputType interfaceToGraphQLOutputType(InterfaceField field) {
        return newInterface().name(field.fieldTypeName())
                .description(field.description())
                .typeResolver(new GraphQLCommons.UnionTypeResolver())
                .fields(fieldsToGraphQLFieldDefinition(field.getFields()))
                .build();
    }

    public static GraphQLOutputType unionToGraphQLOutputType(UnionField field) {
        GraphQLObjectType[] unionValues = field.getUnionTypes()
                .stream()
                .map(GraphQLOutput::objectFieldToGraphQLOutputType)
                .toArray(GraphQLObjectType[]::new);

        return newUnionType().name(field.fieldTypeName())
                .description(field.description())
                .typeResolver(new GraphQLCommons.UnionTypeResolver(unionValues))
                .possibleTypes(unionValues)
                .build();
    }

    public static GraphQLOutputType listFieldToGraphQLOutputType(ListField listField) {
        return new GraphQLList(fieldToGraphQLOutputType(listField.getListValueField()));
    }
}
