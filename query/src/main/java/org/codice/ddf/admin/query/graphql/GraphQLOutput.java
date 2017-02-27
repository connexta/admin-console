package org.codice.ddf.admin.query.graphql;

import static org.codice.ddf.admin.query.graphql.GraphQLCommons.capitalize;
import static org.codice.ddf.admin.query.graphql.GraphQLCommons.enumFieldToGraphQLEnumType;
import static graphql.Scalars.GraphQLInt;
import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLInterfaceType.newInterface;
import static graphql.schema.GraphQLObjectType.newObject;
import static graphql.schema.GraphQLUnionType.newUnionType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.api.fields.InterfaceField;
import org.codice.ddf.admin.query.api.fields.ObjectField;
import org.codice.ddf.admin.query.api.fields.UnionField;
import org.codice.ddf.admin.query.api.fields.UnionValueField;
import org.codice.ddf.admin.query.commons.fields.base.BaseEnumField;
import org.codice.ddf.admin.query.commons.fields.base.ListField;

import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLOutputType;
import graphql.schema.GraphQLScalarType;
import graphql.schema.TypeResolver;

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
        List<GraphQLFieldDefinition> fieldDefinitions = new ArrayList<>();
        if(field.getFields() != null) {
            fieldDefinitions = field.getFields()
                    .stream()
                    .map(GraphQLOutput::fieldToGraphQLFieldDefinition)
                    .collect(Collectors.toList());
        }

        return newObject().name(capitalize(field.fieldTypeName()) + "Payload")
                .description(field.description())
                .fields(fieldDefinitions)
                .build();
    }

    public static GraphQLOutputType interfaceToGraphQLOutputType(InterfaceField field) {
        List<GraphQLFieldDefinition> fieldDefinitions = new ArrayList<>();
        if(field.getFields() != null) {
            fieldDefinitions = field.getFields()
                    .stream()
                    .map(GraphQLOutput::fieldToGraphQLFieldDefinition)
                    .collect(Collectors.toList());
        }

        return newInterface().name(field.fieldTypeName())
                .description(field.description())
                .typeResolver(new UnionTypeResolver())
                .fields(fieldDefinitions)
                .build();
    }

    public static GraphQLOutputType unionToGraphQLOutputType(UnionField field) {

        GraphQLObjectType[] unionValues = field.getUnionTypes()
                .stream()
                .map(GraphQLOutput::objectFieldToGraphQLOutputType)
                .toArray(GraphQLObjectType[]::new);

        return newUnionType().name(field.fieldTypeName())
                .description(field.description())
                .typeResolver(new UnionTypeResolver(unionValues))
                .possibleTypes(unionValues)
                .build();
    }

    public static GraphQLOutputType listFieldToGraphQLOutputType(ListField listField) {
        return new GraphQLList(fieldToGraphQLOutputType(listField.getListValueField()));
    }

    public static Object dataFetchWithUnionTypes(Field field) {
        return field.getValue();
    }

    public static GraphQLFieldDefinition fieldToGraphQLFieldDefinition(Field field) {
        return newFieldDefinition().name(field.fieldName())
                .description(field.description())
                .type(fieldToGraphQLOutputType(field))
                .dataFetcher(fetcher -> dataFetchWithUnionTypes(field))
                .build();
    }

    public static class UnionTypeResolver implements TypeResolver {

        List<GraphQLObjectType> supportedTypes;

        public UnionTypeResolver(GraphQLObjectType... supportedTypes) {
            this.supportedTypes = Arrays.asList(supportedTypes);
        }

        @Override
        public GraphQLObjectType getType(Object object) {

//            if(object instanceof ListField && ((ListField)object).getListValueField() instanceof UnionValueField) {
//                List<GraphQLObjectType> objs = new ArrayList<>();
//                ((ListField)object).getFields().stream().map(obj -> getMatchingUnionType(((UnionValueField) obj))).collect(Collectors.toList());
//
//            } else
            if(object instanceof UnionValueField){
                return getMatchingUnionType((UnionValueField) object);
            } else {
                throw new RuntimeException("UNKNONW UNION TYPE: " + ((Field)object).fieldTypeName());
            }
        }

        public GraphQLObjectType getMatchingUnionType(UnionValueField field) {
            for(GraphQLObjectType type : supportedTypes) {
                String fieldTypeName = ((ObjectField)field).fieldTypeName() + "Payload";
                if(type.getName().equals(fieldTypeName)) {
                    return type;
                }
            }
            throw new RuntimeException("NO MATCHING UNION TYPE FOR: " + ((ObjectField)field).fieldTypeName() + "Payload");
        }
    }
}
