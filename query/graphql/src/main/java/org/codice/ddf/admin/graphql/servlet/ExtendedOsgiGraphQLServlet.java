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
package org.codice.ddf.admin.graphql.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.codice.ddf.admin.api.Events;
import org.codice.ddf.admin.api.FieldProvider;
import org.codice.ddf.admin.api.report.ErrorMessage;
import org.codice.ddf.admin.graphql.servlet.request.DelegateRequest;
import org.codice.ddf.admin.graphql.servlet.request.DelegateResponse;
import org.codice.ddf.admin.graphql.transform.FunctionDataFetcherException;
import org.codice.ddf.admin.graphql.transform.GraphQLTransformCommons;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import graphql.GraphQLError;
import graphql.annotations.EnhancedExecutionStrategy;
import graphql.execution.ExecutionContext;
import graphql.execution.ExecutionStrategy;
import graphql.schema.GraphQLFieldDefinition;
import graphql.servlet.ExecutionStrategyProvider;
import graphql.servlet.GraphQLMutationProvider;
import graphql.servlet.GraphQLQueryProvider;
import graphql.servlet.GraphQLTypesProvider;
import graphql.servlet.OsgiGraphQLServlet;

public class ExtendedOsgiGraphQLServlet extends OsgiGraphQLServlet implements EventHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExtendedOsgiGraphQLServlet.class);

    private List<FieldProvider> fieldProviders;
    private List<GraphQlProvider> transformedProviders;
    private List<GraphQLTypesProvider> typesProviders;
    private ExecutionStrategyProvider execStrategy;

    public ExtendedOsgiGraphQLServlet() {
        super();
        fieldProviders = new ArrayList<>();
        transformedProviders = new ArrayList<>();
        typesProviders = new ArrayList<>();
        execStrategy = new ExecutionStrategyProviderImpl();

    }

    @Override
    public void handleEvent(Event event) {
        if(Events.REFRESH_SCHEMA.equals(event.getTopic())) {
            refreshSchema();
        }
    }

    @Override
    protected ExecutionStrategyProvider getExecutionStrategyProvider() {
        return execStrategy;
    }

    @Override
    protected boolean isClientError(GraphQLError error) {
        return error instanceof DataFetchingGraphQLError || super.isClientError(error);
    }

    @Override
    protected void doPost(HttpServletRequest originalRequest, HttpServletResponse originalResponse)
            throws ServletException, IOException {
        // TODO: tbatie - 6/9/17 - GraphQLServlet does not support batched requests even though a BatchedExecutionStrategy exists. This should be fixed in the GraphQLServlet and contributed back to graphql-java-servlet
        List<String> responses = new ArrayList<>();
        String originalReqContent = IOUtils.toString(originalRequest.getInputStream());
        boolean isBatchRequest = isBatchRequest(originalReqContent);

        try {
            List<String> splitReqs = splitQueries(originalReqContent);
            for (String reqStr : splitReqs) {
                DelegateResponse response = new DelegateResponse(originalResponse);
                super.doPost(new DelegateRequest(originalRequest, reqStr), response);
                responses.add(response.getDelegatedResponse());
            }

            originalResponse.setContentType(APPLICATION_JSON_UTF8);
            originalResponse.setStatus(STATUS_OK);
            originalResponse.getWriter()
                    .write(isBatchRequest ?
                            "[" + String.join(",", responses) + "]" :
                            responses.get(0));
        } catch (Throwable t) {
            originalResponse.setStatus(500);
            log.error("Error executing GraphQL request!", t);
        }
    }

    public List<String> splitQueries(String requestContent) throws Exception {
        List<String> splitElements = new ArrayList<>();
        JsonNode jsonNode = new ObjectMapper().readTree(requestContent);
        if (jsonNode.isArray()) {
            jsonNode.elements()
                    .forEachRemaining(node -> splitElements.add(node.toString()));
        } else {
            splitElements.add(jsonNode.toString());
        }
        return splitElements;
    }

    public boolean isBatchRequest(String requestContent) throws IOException {
        return new ObjectMapper().readTree(requestContent)
                .isArray();
    }

    public void refreshSchema() {
        LOGGER.debug("Refreshing GraphQL schema.");

        unbindProviders();

        GraphQLTransformCommons transformer = new GraphQLTransformCommons();
        transformedProviders = fieldProviders.stream()
                .map(fieldProvider -> new GraphQlProvider(fieldProvider, transformer))
                .collect(Collectors.toList());

        typesProviders = transformer.getGraphQlTypeProviders();

        bindProviders();

        LOGGER.debug("Finished refreshing GraphQL schema.");
    }

    public void unbindProviders() {
        LOGGER.debug("Unbinding GraphQL providers.");

        transformedProviders.stream()
                .filter(provider -> CollectionUtils.isNotEmpty(provider.getMutations()))
                .forEach(this::unbindMutationProvider);

        transformedProviders.stream()
                .filter(provider -> CollectionUtils.isNotEmpty(provider.getQueries()))
                .forEach(this::unbindQueryProvider);

        typesProviders.stream()
                .filter(provider -> CollectionUtils.isNotEmpty(provider.getTypes()))
                .forEach(this::unbindTypesProvider);
    }

    public void bindProviders() {
        LOGGER.debug("Binding GraphQL providers.");

        typesProviders.stream()
                .filter(provider -> CollectionUtils.isNotEmpty(provider.getTypes()))
                .forEach(this::typesProviders);

        transformedProviders.stream()
                .filter(provider -> CollectionUtils.isNotEmpty(provider.getMutations()))
                .forEach(this::bindMutationProvider);

        transformedProviders.stream()
                .filter(provider -> CollectionUtils.isNotEmpty(provider.getQueries()))
                .forEach(this::bindQueryProvider);
    }

    public class GraphQlProvider implements GraphQLQueryProvider, GraphQLMutationProvider {

        private List<GraphQLFieldDefinition> queries;
        private List<GraphQLFieldDefinition> mutations;

        public GraphQlProvider(FieldProvider provider,
                GraphQLTransformCommons transformCommons) {
            queries = transformCommons.fieldProviderToQueries(provider);
            mutations = transformCommons.fieldProviderToMutations(provider);
        }

        @Override
        public Collection<GraphQLFieldDefinition> getMutations() {
            return mutations;
        }

        @Override
        public Collection<GraphQLFieldDefinition> getQueries() {
            return queries;
        }
    }

    public class ExtendedEnhancedExecutionStrategy extends EnhancedExecutionStrategy {

        @Override
        protected void handleDataFetchingException(ExecutionContext executionContext,
                GraphQLFieldDefinition fieldDef, Map<String, Object> argumentValues, Exception e) {
            if (e instanceof FunctionDataFetcherException) {
                for (ErrorMessage msg : ((FunctionDataFetcherException) e).getCustomMessages()) {
                    executionContext.addError(new DataFetchingGraphQLError(msg));
                }
            } else {
                super.handleDataFetchingException(executionContext, fieldDef, argumentValues, e);
            }
        }
    }

    public class ExecutionStrategyProviderImpl implements ExecutionStrategyProvider {

        private ExtendedEnhancedExecutionStrategy strategy;

        public ExecutionStrategyProviderImpl() {
            strategy = new ExtendedEnhancedExecutionStrategy();
        }

        @Override
        public ExecutionStrategy getQueryExecutionStrategy() {
            return strategy;
        }

        @Override
        public ExecutionStrategy getMutationExecutionStrategy() {
            return strategy;
        }

        @Override
        public ExecutionStrategy getSubscriptionExecutionStrategy() {
            return strategy;
        }
    }

    public void bindFieldProvider(FieldProvider fieldProvider) {
        refreshSchema();
    }

    public void unbindFieldProvider(FieldProvider fieldProvider) {
        refreshSchema();
    }

    public void setFieldProviders(List<FieldProvider> fieldProviders) {
        this.fieldProviders = fieldProviders;
    }
}