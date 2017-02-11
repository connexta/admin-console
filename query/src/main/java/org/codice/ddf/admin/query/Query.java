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
 */
package org.codice.ddf.admin.query;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.codice.ddf.admin.query.api.Action;
import org.codice.ddf.admin.query.api.ActionHandler;
import org.codice.ddf.admin.query.api.Field;
import org.codice.ddf.admin.query.sample.SampleActionHandler;

import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLOutputType;
import graphql.servlet.GraphQLQueryProvider;

public class Query implements GraphQLQueryProvider {

    public static final GraphQLObjectType MESSAGE_OBJECT = newObject().name("message")
            .field(newFieldDefinition().type(GraphQLString).name("code"))
            .field(newFieldDefinition().type(GraphQLString).name("description"))
            .build();

    //https://github.com/graphql-java/graphql-java/blob/master/src/main/java/graphql/schema/PropertyDataFetcher.java
    @Override
    public GraphQLObjectType getQuery() {
        return handlerToGraphQLObject(new SampleActionHandler());
    }

    @Override
    public Object context() {
        return new HashMap<>();
    }

    public GraphQLObjectType handlerToGraphQLObject(ActionHandler handler) {
        List<Action> actions = handler.getSupportedActions();
        List<GraphQLFieldDefinition> actionObjects = new ArrayList<>();
        for (Action action : actions) {
            actionObjects.add(handlerActionToGraphQLObject(handler.getActionHandlerId(), action));
        }

        return newObject().name(handler.getActionHandlerId())
                .fields(actionObjects)
                .build();
    }

    public GraphQLFieldDefinition handlerActionToGraphQLObject(String handlerId, Action action) {
        List<Field> reqFields = action.getRequiredFields();
        List<Field> optFields = action.getOptionalFields();

        List<GraphQLArgument> requiredArgs = new ArrayList<>();
        List<GraphQLArgument> optionalArgs = new ArrayList<>();

        if (reqFields != null) {
            for (Field field : action.getRequiredFields()) {
                requiredArgs.add(fieldToGraphQLArgument(field));
            }
        }

        if (optFields != null) {
            for (Field field : action.getOptionalFields()) {
                optionalArgs.add(fieldToGraphQLArgument(field));
            }
        }

        return newFieldDefinition().name(action.getActionId())
                .type(actionToGraphQLReturnType(handlerId, action))
                .argument(requiredArgs)
                .argument(optionalArgs)
                .dataFetcher(env -> dataFetch(action, env))
                .build();
    }

    public Object dataFetch(Action action, DataFetchingEnvironment env) {
        return action.process(env.getArguments()).toMap();
    }

    public GraphQLArgument fieldToGraphQLArgument(Field field) {
        return new GraphQLArgument(field.getFieldName(),
                fieldTypeToGraphQLInputType(field.getFieldType()));
    }

    public GraphQLFieldDefinition fieldToGraphQLFieldDefinition(Field field) {
        return newFieldDefinition().name(field.getFieldName())
                .type(fieldTypeToGraphQLType(field.getFieldType()))
                .build();
    }

    public GraphQLObjectType actionToGraphQLReturnType(String actionHandlerId, Action action) {
        String reportName = new StringBuilder()
                .append(actionHandlerId)
                .append(StringUtils.capitalize(action.getActionId()))
                .append("Report")
                .toString();

        GraphQLObjectType.Builder report = getBaseActionReport(reportName);

        if(action.getReturnTypes() != null && !action.getReturnTypes().isEmpty()) {
            List<GraphQLFieldDefinition> fields = action.getReturnTypes().stream()
                    .map(field -> fieldToGraphQLFieldDefinition(field))
                    .collect(Collectors.toList());
            report.fields(fields);
        }

        return report.build();
    }

    public GraphQLOutputType fieldTypeToGraphQLType(String fieldType) {
        return GraphQLString;
    }

    public GraphQLInputType fieldTypeToGraphQLInputType(String fieldType) {
        return GraphQLString;
    }

    public GraphQLObjectType.Builder getBaseActionReport(String reportName){
        return newObject()
                .name(reportName)
                .field(newFieldDefinition().name("successes").type(new GraphQLList(MESSAGE_OBJECT)))
                .field(newFieldDefinition().name("failures").type(new GraphQLList(MESSAGE_OBJECT)))
                .field(newFieldDefinition().name("warnings").type(new GraphQLList(MESSAGE_OBJECT)));
    }
}
