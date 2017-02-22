package org.codice.ddf.admin.query.graphql;

import static org.codice.ddf.admin.query.graphql.GraphQLInput.fieldToGraphQLArgument;
import static org.codice.ddf.admin.query.graphql.GraphQLOutput.fieldToGraphQLOutputType;
import static graphql.schema.GraphQLEnumType.newEnum;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.codice.ddf.admin.query.api.Action;
import org.codice.ddf.admin.query.api.ActionHandler;
import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.commons.fields.base.EnumField;
import org.codice.ddf.admin.query.commons.fields.base.EnumFieldValue;

import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLEnumType;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;

public class GraphQLCommons {

    public static GraphQLObjectType handlerToGraphQLObject(ActionHandler handler) {
        List<GraphQLFieldDefinition> actionObjects = handler.getSupportedActions()
                .stream()
                .map(a -> handlerActionToGraphQLObject(a))
                .collect(Collectors.toList());

        return newObject().name(handler.getActionHandlerId())
                .description(handler.description())
                .fields(actionObjects)
                .build();
    }

    public static GraphQLFieldDefinition handlerActionToGraphQLObject(Action action) {
        List<Field> reqFields = action.getRequiredFields();
        List<Field> optFields = action.getOptionalFields();
        List<GraphQLArgument> requiredArgs = new ArrayList<>();
        List<GraphQLArgument> optionalArgs = new ArrayList<>();

        // TODO: tbatie - 2/15/17 - Need to figure out how to make the required args required
        if (reqFields != null) {
            reqFields.forEach(f -> requiredArgs.add(fieldToGraphQLArgument(f)));
        }

        if (optFields != null) {
            optFields
                    .forEach(f -> optionalArgs.add(fieldToGraphQLArgument(f)));
        }

        return newFieldDefinition().name(action.getActionName())
                .type(fieldToGraphQLOutputType(action.getReturnType()))
                .description(action.description())
                .argument(requiredArgs)
                .argument(optionalArgs)
                .dataFetcher(env -> dataFetch(env, action))
                .build();
    }

    public static GraphQLEnumType enumFieldToGraphQLEnumType(EnumField field) {
        GraphQLEnumType.Builder builder = newEnum()
                .name(capitalize(field.fieldName()))
                .description(field.description());

        field.getEnumValues()
                .forEach(val -> builder.value(((EnumFieldValue) val).getName(),
                        ((EnumFieldValue) val).getValue(),
                        ((EnumFieldValue) val).getDescription()));
        return builder.build();
    }

    public static Object dataFetch(DataFetchingEnvironment env, Action action) {
        return action.process(env.getArguments()).getValue();
    }
    public static String capitalize(String str){
        return StringUtils.capitalize(str);
    }
}
