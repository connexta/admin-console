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
package org.codice.ddf.admin.graphql;

import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;
import static graphql.schema.GraphQLSchema.newSchema;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codice.ddf.admin.api.action.ActionCreator;
import org.codice.ddf.admin.api.action.Message;

import graphql.GraphQLError;
import graphql.execution.ExecutionStrategy;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import graphql.servlet.DefaultGraphQLContextBuilder;
import graphql.servlet.ExecutionStrategyProvider;
import graphql.servlet.GraphQLContext;
import graphql.servlet.GraphQLContextBuilder;
import graphql.servlet.GraphQLMutationProvider;
import graphql.servlet.GraphQLQueryProvider;
import graphql.servlet.GraphQLServlet;
import graphql.servlet.GraphQLVariables;

public class GraphQLServletImpl extends GraphQLServlet {

    private List<ActionCreator> actionCreators = new ArrayList<>();

    private GraphQLContextBuilder contextBuilder = new DefaultGraphQLContextBuilder();

    private ExecutionStrategyProvider executionStrategyProvider =
            new ExecutionStrategyProviderImpl();

    private GraphQLSchema schema;

    public GraphQLServletImpl() {
        updateSchema();
    }

    private void updateSchema() {
        GraphQLObjectType.Builder object = newObject().name("Query");

        List<GraphQLProviderImpl> providers = actionCreators.stream()
                .map(GraphQLProviderImpl::new)
                .collect(Collectors.toList());

        for (GraphQLQueryProvider provider : providers) {
            GraphQLObjectType query = provider.getQuery();
            object.field(newFieldDefinition().
                    type(query)
                    .
                            staticValue(provider.context())
                    .
                            name(provider.getName())
                    .
                            description(query.getDescription())
                    .
                            build());
        }

        boolean noMutations = providers.stream()
                .map(GraphQLProviderImpl::getMutations)
                .flatMap(Collection::stream)
                .collect(Collectors.toList())
                .isEmpty();

        if (noMutations) {
            schema = newSchema().query(object.build())
                    .build();
        } else {
            GraphQLObjectType.Builder mutationObject = newObject().name("Mutation");

            for (GraphQLMutationProvider provider : providers) {
                provider.getMutations()
                        .forEach(mutationObject::field);
            }

            schema = newSchema().query(object.build())
                    .mutation(mutationObject.build())
                    .build();
        }
    }

    @Override
    protected Map<String, Object> createResultFromDataAndErrors(Object data,
            List<GraphQLError> msgs) {
        Map<String, Object> result = new HashMap<>();
        result.put("data", data);

        if (msgs != null && !msgs.isEmpty()) {
            List<Message> actionMsgs = msgs.stream()
                    .filter(ActionGraphQLError.class::isInstance)
                    .map(ActionGraphQLError.class::cast)
                    .map(ActionGraphQLError::getActionMessage)
                    .collect(Collectors.toList());

            List<Message> warnings = actionMsgs.stream()
                    .filter(error -> error.getType() == Message.MessageType.WARNING)
                    .collect(Collectors.toList());

            List<Message> errors = actionMsgs.stream()
                    .filter(error -> error.getType() == Message.MessageType.ERROR)
                    .collect(Collectors.toList());

            result.put("errors", errors);
            result.put("warnings", warnings);
        }

        return result;
    }

    @Override
    protected GraphQLContext createContext(Optional<HttpServletRequest> request,
            Optional<HttpServletResponse> response) {
        return contextBuilder.build(request, response);
    }

    @Override
    protected ExecutionStrategy getQueryExecutionStrategy() {
        return executionStrategyProvider.getExecutionStrategy();
    }

    @Override
    protected ExecutionStrategy getMutationExecutionStrategy() {
        return executionStrategyProvider.getExecutionStrategy();
    }

    @Override
    protected Map<String, Object> transformVariables(GraphQLSchema schema, String query,
            Map<String, Object> variables) {
        return new GraphQLVariables(schema, query, variables);
    }

    @Override
    public GraphQLSchema getSchema() {
        return schema;
    }

    @Override
    public GraphQLSchema getReadOnlySchema() {
        return schema;
    }

    public void bindCreator(ActionCreator creator) {
        updateSchema();
    }

    public void unbindCreator(ActionCreator creator) {
        updateSchema();
    }

    public void setActionCreators(List<ActionCreator> actionCreators) {
        this.actionCreators = actionCreators;
        updateSchema();
    }

    public class ExecutionStrategyProviderImpl implements ExecutionStrategyProvider {
        @Override
        public ExecutionStrategyImpl getExecutionStrategy() {
            return new ExecutionStrategyImpl();
        }
    }
}
