/**
 * Copyright (c) Codice Foundation
 * <p>
 * This is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details. A copy of the GNU Lesser General Public License
 * is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 **/
package org.codice.ddf.admin.graphql.common;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.codice.ddf.admin.api.action.Action;
import org.codice.ddf.admin.api.action.ActionCreator;
import org.codice.ddf.admin.api.action.Message;
import org.codice.ddf.admin.api.fields.EnumField;
import org.codice.ddf.admin.api.fields.Field;
import org.codice.ddf.admin.api.fields.ObjectField;

import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLEnumType;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;

public class GraphQLCommons {

    public static GraphQLObjectType actionCreatorToGraphQLObjectType(ActionCreator creator, List<Action> actions) {
        return GraphQLObjectType.newObject().name(creator.typeName())
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

            fields.add(GraphQLFieldDefinition.newFieldDefinition().name(actionField.name())
                    .type(GraphQLOutput.fieldToGraphQLOutputType(actionField.returnType()))
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
            return GraphQLObjectType.newObject().name(capitalize(field.fieldTypeName()) + "Payload")
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
            return GraphQLFieldDefinition.newFieldDefinition().name(field.fieldName())
                    .description(field.description())
                    .type(GraphQLOutput.fieldToGraphQLOutputType(field))
//                    .dataFetcher(fetcher -> unionTypeDataFetch(field))
                    .build();
        default:
            return GraphQLFieldDefinition.newFieldDefinition().name(field.fieldName())
                    .description(field.description())
                    .type(GraphQLOutput.fieldToGraphQLOutputType(field))
                    .build();
        }
    }

    public static GraphQLArgument fieldToGraphQLArgument(Field field) {
        return GraphQLArgument.newArgument().name(field.fieldName())
                .description(field.description())
                .type(GraphQLInput.fieldTypeToGraphQLInputType(field))
                .build();
    }

    public static GraphQLEnumType enumFieldToGraphQLEnumType(EnumField field) {
        GraphQLEnumType.Builder builder = GraphQLEnumType.newEnum()
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
