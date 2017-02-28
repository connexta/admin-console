package org.codice.ddf.admin.query.graphql;

import static org.codice.ddf.admin.query.graphql.GraphQLCommons.enumFieldToGraphQLEnumType;
import static org.codice.ddf.admin.query.graphql.GraphQLCommons.fieldsToGraphQLFieldDefinition;
import static org.codice.ddf.admin.query.graphql.GraphQLCommons.fieldToGraphQLObjectType;
import static graphql.Scalars.GraphQLInt;
import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLInterfaceType.newInterface;
import static graphql.schema.GraphQLUnionType.newUnionType;

import java.util.Arrays;
import java.util.List;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.api.fields.InterfaceField;
import org.codice.ddf.admin.query.api.fields.ObjectField;
import org.codice.ddf.admin.query.api.fields.UnionField;
import org.codice.ddf.admin.query.api.fields.UnionValueField;
import org.codice.ddf.admin.query.commons.fields.base.BaseEnumField;
import org.codice.ddf.admin.query.commons.fields.base.ListField;

import graphql.schema.GraphQLList;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLOutputType;
import graphql.schema.GraphQLScalarType;
import graphql.schema.TypeResolver;

public class GraphQLOutput {

    public static GraphQLOutputType fieldToGraphQLOutputType(Field field) {
        switch (field.fieldBaseType()) {
        case OBJECT:
            return fieldToGraphQLObjectType(field);
        case ENUM:
            return enumFieldToGraphQLEnumType((BaseEnumField) field);
        case LIST:
            return new GraphQLList(fieldToGraphQLOutputType(((ListField)field).getListValueField()));
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

    public static GraphQLOutputType interfaceToGraphQLOutputType(InterfaceField field) {
        return newInterface().name(field.fieldTypeName())
                .description(field.description())
                .typeResolver(new UnionTypeResolver())
                .fields(fieldsToGraphQLFieldDefinition(field.getFields()))
                .build();
    }

    public static GraphQLOutputType unionToGraphQLOutputType(UnionField field) {
        GraphQLObjectType[] unionValues = field.getUnionTypes()
                .stream()
                .map(GraphQLCommons::fieldToGraphQLObjectType)
                .toArray(GraphQLObjectType[]::new);

        return newUnionType().name(field.fieldTypeName())
                .description(field.description())
                .typeResolver(new UnionTypeResolver(unionValues))
                .possibleTypes(unionValues)
                .build();
    }

    public static class UnionTypeResolver implements TypeResolver {

        List<GraphQLObjectType> supportedTypes;

        public UnionTypeResolver(GraphQLObjectType... supportedTypes) {
            this.supportedTypes = Arrays.asList(supportedTypes);
        }

        @Override
        public GraphQLObjectType getType(Object object) {
            if(object instanceof UnionValueField){
                return getMatchingUnionType((UnionValueField) object);
            } else {
                throw new RuntimeException("UNKNOWN UNION TYPE: " + ((Field)object).fieldTypeName());
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