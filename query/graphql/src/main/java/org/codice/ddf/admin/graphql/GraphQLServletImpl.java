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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codice.ddf.admin.api.FieldProvider;
import org.codice.ddf.admin.api.report.Message;
import org.codice.ddf.admin.graphql.common.GraphQLTransformCommons;
import org.codice.ddf.admin.graphql.test.TestFieldProvider;

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

    private List<FieldProvider> fieldProviders = new ArrayList<>();

    private GraphQLContextBuilder contextBuilder = new DefaultGraphQLContextBuilder();

    private ExecutionStrategyProvider executionStrategyProvider =
            new ExecutionStrategyProviderImpl();

    private GraphQLSchema schema;

    public GraphQLServletImpl() {
        updateSchema();
    }

    private void updateSchema() {
        GraphQLObjectType.Builder object = newObject().name("Query");
        GraphQLTransformCommons transformCommons = new GraphQLTransformCommons();
//        List<GraphQLProviderImpl> graphqlProviders = transformCommons.fieldProviderToGraphQLProvider(fieldProviders);
        List<GraphQLProviderImpl> graphqlProviders = transformCommons.fieldProviderToGraphQLProvider(Arrays.asList(new TestFieldProvider()));

        for (GraphQLQueryProvider provider : graphqlProviders) {
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

        boolean noMutations = graphqlProviders.stream()
                .map(GraphQLProviderImpl::getMutations)
                .flatMap(Collection::stream)
                .collect(Collectors.toList())
                .isEmpty();

        if (noMutations) {
            schema = newSchema().query(object.build())
                    .build();
        } else {
            GraphQLObjectType.Builder mutationObject = newObject().name("Mutation");

            for (GraphQLMutationProvider provider : graphqlProviders) {
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
            List<GraphQLError> graphQLJavaErrors = msgs.stream().filter(msg -> !GraphQLErrorMessageWrapper.class.isInstance(msg)).collect(
                    Collectors.toList());

            List<Message> internalMsgs = msgs.stream()
                    .filter(GraphQLErrorMessageWrapper.class::isInstance)
                    .map(GraphQLErrorMessageWrapper.class::cast)
                    .map(GraphQLErrorMessageWrapper::getQueryProviderError)
                    .collect(Collectors.toList());

            List<Message> internalWarningMsgs = internalMsgs.stream()
                    .filter(error -> error.getType() == Message.MessageType.WARNING)
                    .collect(Collectors.toList());

            List<Message> internalErrorMsgs = internalMsgs.stream()
                    .filter(error -> error.getType() == Message.MessageType.ERROR)
                    .collect(Collectors.toList());

            result.put("errors", Stream.concat(graphQLJavaErrors.stream(), internalErrorMsgs.stream()).collect(Collectors.toList()));
            result.put("warnings", internalWarningMsgs);
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

    public void bindFieldProvider(FieldProvider creator) {
        updateSchema();
    }

    public void unbindFieldProvider(FieldProvider creator) {
        updateSchema();
    }

    public void setFieldProviders(List<FieldProvider> fieldProviders) {
        this.fieldProviders = fieldProviders;
        updateSchema();
    }

    public class ExecutionStrategyProviderImpl implements ExecutionStrategyProvider {
        @Override
        public ExecutionStrategyImpl getExecutionStrategy() {
            return new ExecutionStrategyImpl();
        }
    }
}
