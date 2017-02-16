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
import org.codice.ddf.admin.query.api.field.Field;
import org.codice.ddf.admin.query.commons.field.BaseFields;

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
            reqFields.stream()
                    .forEach(f -> requiredArgs.add(fieldToGraphQLArgument(f)));
        }

        if (optFields != null) {
            optFields.stream()
                    .forEach(f -> optionalArgs.add(fieldToGraphQLArgument(f)));
        }

        return newFieldDefinition().name(action.getActionName())
                .type(fieldToGraphQLOutputType(action.getReturnType()))
                .description(action.description())
                .argument(requiredArgs)
                .argument(optionalArgs)
                .dataFetcher(env -> action.process(env.getArguments()))
                .build();
    }

    public static GraphQLEnumType enumFieldToGraphQLEnumType(BaseFields.EnumField field) {
        GraphQLEnumType.Builder builder = newEnum()
                .name(capitalize(field.fieldName()))
                .description(field.description());

        field.getEnumValues()
                .stream()
                .forEach(val -> builder.value(((BaseFields.EnumValue) val).getName(),
                        ((BaseFields.EnumValue) val).getValue(),
                        ((BaseFields.EnumValue) val).getDescription()));
        return builder.build();
    }

    public static String capitalize(String str){
        return StringUtils.capitalize(str);
    }
}
