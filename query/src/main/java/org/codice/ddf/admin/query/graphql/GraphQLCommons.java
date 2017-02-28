package org.codice.ddf.admin.query.graphql;

import static org.codice.ddf.admin.query.graphql.GraphQLInput.fieldTypeToGraphQLInputType;
import static org.codice.ddf.admin.query.graphql.GraphQLOutput.fieldToGraphQLOutputType;
import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLEnumType.newEnum;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.codice.ddf.admin.query.api.ActionHandler;
import org.codice.ddf.admin.query.api.fields.ActionField;
import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.api.fields.ObjectField;
import org.codice.ddf.admin.query.api.fields.UnionField;
import org.codice.ddf.admin.query.api.fields.UnionValueField;
import org.codice.ddf.admin.query.commons.fields.base.BaseEnumField;
import org.codice.ddf.admin.query.commons.fields.base.EnumFieldValue;
import org.codice.ddf.admin.query.commons.fields.base.ListField;

import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLEnumType;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;
import graphql.schema.TypeResolver;

public class GraphQLCommons {

    public static GraphQLObjectType handlerToGraphQLObject(ActionHandler handler) {
        return newObject().name(handler.getActionHandlerId())
                .description(handler.description())
                .fields(fieldsToGraphQLFieldDefinition(handler.getDiscoveryActions()))
                .build();
    }

    public static List<GraphQLFieldDefinition> fieldsToGraphQLFieldDefinition(List<? extends Field> fields) {
        if(fields == null) {
            return new ArrayList<>();
        }
        return fields.stream()
                .map(field -> fieldToGraphQLFieldDefinition(field))
                .collect(Collectors.toList());
    }

    public static GraphQLFieldDefinition fieldToGraphQLFieldDefinition(Field field) {
        switch (field.fieldBaseType()) {
        case ACTION:
            return handlerActionToGraphQLFieldDefinition((ActionField) field);
        case UNION:
            return newFieldDefinition().name(field.fieldName())
                    .description(field.description())
                    .type(fieldToGraphQLOutputType(field))
                    .dataFetcher(fetcher -> dataFetchWithUnionTypes(field))
                    .build();
        default:
            return newFieldDefinition().name(field.fieldName())
                    .description(field.description())
                    .type(fieldToGraphQLOutputType(field))
                    .build();
        }
    }

    public static GraphQLFieldDefinition handlerActionToGraphQLFieldDefinition(ActionField action) {
        List<GraphQLArgument> graphQLArgs = new ArrayList<>();

        if (action.getArguments() != null) {
            action.getArguments().forEach(f -> graphQLArgs.add(fieldToGraphQLArgument((Field)f)));
        }

        return newFieldDefinition().name(action.fieldName())
                .type(fieldToGraphQLOutputType(action.getReturnField()))
                .description(action.description())
                .argument(graphQLArgs)
                .dataFetcher(env -> actionFieldDataFetch(env, action))
                .build();
    }

    public static GraphQLArgument fieldToGraphQLArgument(Field field) {
        return newArgument().name(field.fieldName())
                .description(field.description())
                .type(fieldTypeToGraphQLInputType(field))
                .build();
    }

    public static GraphQLEnumType enumFieldToGraphQLEnumType(BaseEnumField field) {
        GraphQLEnumType.Builder builder = newEnum()
                .name(capitalize(field.fieldName()))
                .description(field.description());

        field.getEnumValues()
                .forEach(val -> builder.value(((EnumFieldValue) val).getName(),
                        ((EnumFieldValue) val).getValue(),
                        ((EnumFieldValue) val).getDescription()));
        return builder.build();
    }

    public static Object actionFieldDataFetch(DataFetchingEnvironment env, ActionField action) {
        Field fieldResult = action.process(env.getArguments());

        //Union values are handled by the union data fetcher
        if(fieldResult instanceof UnionValueField) {
            return fieldResult;
        } else if(fieldResult instanceof ListField && ((ListField) fieldResult).getListValueField() instanceof UnionField) {
            return ((ListField) fieldResult).getFields();
        }

        return fieldResult.getValue();
    }

    //This method is used to break the recursion cycle of "actionFieldDataFetch"
    public static Object dataFetchWithUnionTypes(Field field) {
        return field.getValue();
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

    public static String capitalize(String str){
        return StringUtils.capitalize(str);
    }
}
