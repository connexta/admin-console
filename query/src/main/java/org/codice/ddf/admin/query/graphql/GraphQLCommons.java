package org.codice.ddf.admin.query.graphql;

import static org.codice.ddf.admin.query.graphql.GraphQLInput.fieldTypeToGraphQLInputType;
import static org.codice.ddf.admin.query.graphql.GraphQLOutput.fieldToGraphQLOutputType;
import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLEnumType.newEnum;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.codice.ddf.admin.query.api.fields.ActionField;
import org.codice.ddf.admin.query.api.fields.ActionHandlerField;
import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.api.fields.ObjectField;
import org.codice.ddf.admin.query.commons.fields.base.BaseEnumField;
import org.codice.ddf.admin.query.commons.fields.base.ListField;

import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLEnumType;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;

public class GraphQLCommons {

    public static GraphQLObjectType actionHandlerToGraphQLObjectType(ActionHandlerField actionHandler) {
        return newObject().name(actionHandler.fieldName())
                .description(actionHandler.description())
                .fields(fieldsToGraphQLFieldDefinition(((ActionHandlerField)actionHandler).getDiscoveryActions()))
                .build();
    }

    public static GraphQLObjectType fieldToGraphQLObjectType(Field field) {
        switch (field.fieldBaseType()) {
        case OBJECT:
            //Add on Payload to avoid collision between an input and output field type name;
            return newObject().name(capitalize(field.fieldTypeName()) + "Payload")
                    .description(field.description())
                    .fields(fieldsToGraphQLFieldDefinition(((ObjectField)field).getFields()))
                    .build();

        case ACTION_HANDLER:
            //Because this only be transformed into a top level sub query, there is no need to add Payload
            return newObject().name(field.fieldTypeName())
                    .description(field.description())
                    .fields(fieldsToGraphQLFieldDefinition(((ActionHandlerField)field).getDiscoveryActions()))
                    .build();
        default:
            throw new RuntimeException("Unknown ObjectType: " + field.fieldBaseType());
        }
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
            ActionField actionField = (ActionField) field;
            List<GraphQLArgument> graphQLArgs = new ArrayList<>();

            if (actionField.getArguments() != null) {
                actionField.getArguments().forEach(f -> graphQLArgs.add(fieldToGraphQLArgument((Field)f)));
            }

            return newFieldDefinition().name(actionField.fieldName())
                    .type(fieldToGraphQLOutputType(actionField.getReturnField()))
                    .description(actionField.description())
                    .argument(graphQLArgs)
                    .dataFetcher(env -> actionFieldDataFetch(env, actionField))
                    .build();

        case UNION:
            return newFieldDefinition().name(field.fieldName())
                    .description(field.description())
                    .type(fieldToGraphQLOutputType(field))
//                    .dataFetcher(fetcher -> unionTypeDataFetch(field))
                    .build();
        default:
            return newFieldDefinition().name(field.fieldName())
                    .description(field.description())
                    .type(fieldToGraphQLOutputType(field))
                    .build();
        }
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
                .forEach(val -> builder.value(((Field) val).fieldName(),
                        ((Field) val).getValue(),
                        ((Field) val).description()));
        return builder.build();
    }

    public static Object actionFieldDataFetch(DataFetchingEnvironment env, ActionField action) {
        return action.process(env.getArguments()).getValue();
    }

    public static Object transformFieldToValue(Field field){
        switch(field.fieldBaseType()) {
        case ENUM:
        case STRING:
        case INTEGER:
        case FLOAT:
            return field.getValue();
        case LIST:
            return ((ListField) field).getFields()
                    .stream()
                    .map(f -> transformFieldToValue((Field) f))
                    .collect(Collectors.toList());
        case OBJECT:

        default:
            throw new RuntimeException("Unhandled base transform type [" + field.fieldBaseType() + "] with field name of [" + field.fieldName() + "] ");
        }
    }

    public static String capitalize(String str){
        return StringUtils.capitalize(str);
    }
}
