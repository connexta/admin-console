package org.codice.ddf.admin.query.graphql;

import static org.codice.ddf.admin.query.graphql.GraphQLInput.fieldTypeToGraphQLInputType;
import static org.codice.ddf.admin.query.graphql.GraphQLOutput.fieldToGraphQLOutputType;
import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLEnumType.newEnum;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.codice.ddf.admin.query.api.action.Action;
import org.codice.ddf.admin.query.api.action.ActionCreator;
import org.codice.ddf.admin.query.api.action.Message;
import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.api.fields.ListField;
import org.codice.ddf.admin.query.api.fields.ObjectField;
import org.codice.ddf.admin.query.commons.fields.base.BaseEnumField;

import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLEnumType;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;

public class GraphQLCommons {

    public static GraphQLObjectType actionCreatorToGraphQLObjectType(ActionCreator creator, List<Action> actions) {
        return newObject().name(creator.typeName())
                .description(creator.description())
                .fields(actionsToGraphQLFieldDef(creator, actions))
                .build();
    }

    public static List<GraphQLFieldDefinition> actionsToGraphQLFieldDef(ActionCreator creator, List<Action> actions) {
        List<GraphQLFieldDefinition> fields = new ArrayList<>();

        for(Action actionField : actions) {
            List<GraphQLArgument> graphQLArgs = new ArrayList<>();

            if (actionField.getArguments() != null) {
                actionField.getArguments().forEach(f -> graphQLArgs.add(fieldToGraphQLArgument((Field)f)));
            }

            fields.add(newFieldDefinition().name(actionField.name())
                    .type(fieldToGraphQLOutputType(actionField.returnType()))
                    .description(actionField.description())
                    .argument(graphQLArgs)
                    .dataFetcher(env -> actionFieldDataFetch(env, actionField.name(), creator))
                    .build());
        }

        return fields;
    }

    public static GraphQLObjectType fieldToGraphQLObjectType(Field field) {
        switch (field.fieldBaseType()) {
        case OBJECT:
            //Add on Payload to avoid collision between an input and output field type name;
            return newObject().name(capitalize(field.fieldTypeName()) + "Payload")
                    .description(field.description())
                    .fields(fieldsToGraphQLFieldDefinition(((ObjectField)field).getFields()))
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

    public static Object actionFieldDataFetch(DataFetchingEnvironment env, String actionId, ActionCreator actionCreator) {
//        if(true) {
//            throw new RuntimeException("This is a test. Gimmie da money");
//        }
        Action action = actionCreator.createAction(actionId);
        action.setArguments(env.getArguments());
        List<Message> validationMessage = action.validate();
        return action.process().getValue();
    }

    public static String capitalize(String str){
        return StringUtils.capitalize(str);
    }
}
