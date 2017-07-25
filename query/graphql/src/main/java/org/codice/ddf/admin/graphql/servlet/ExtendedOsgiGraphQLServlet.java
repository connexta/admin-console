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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalCause;
import com.google.common.cache.RemovalNotification;

import graphql.GraphQLError;
import graphql.annotations.EnhancedExecutionStrategy;
import graphql.execution.ExecutionContext;
import graphql.execution.ExecutionStrategy;
import graphql.schema.GraphQLFieldDefinition;
import graphql.servlet.DefaultGraphQLErrorHandler;
import graphql.servlet.ExecutionStrategyProvider;
import graphql.servlet.GraphQLErrorHandler;
import graphql.servlet.GraphQLMutationProvider;
import graphql.servlet.GraphQLProvider;
import graphql.servlet.GraphQLQueryProvider;
import graphql.servlet.OsgiGraphQLServlet;

public class ExtendedOsgiGraphQLServlet extends OsgiGraphQLServlet implements EventHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExtendedOsgiGraphQLServlet.class);
    private static final long CACHE_EXPIRATION_IN_SECONDS = 1;
    private static final long CACHE_CLEANUP_INVOCATION_IN_SECONDS = 1;

    private static final String BINDING_FIELD_PROVIDER = "GraphQL servlet binding field provider %s";
    private static final String UNBINDING_FIELD_PROVIDER = "GraphQL servlet unbinding field provider %s";

    private Cache<String, Object> cache;
    private ScheduledExecutorService scheduler;
    private List<FieldProvider> fieldProviders;
    private List<GraphQLProviderImpl> transformedProviders;
    private ExecutionStrategyProvider execStrategy;
    private GraphQLErrorHandler errorHandler;
    private GraphQLQueryProvider errorCodeProvider;

    public ExtendedOsgiGraphQLServlet() {
        super();
        cache = CacheBuilder.newBuilder()
                .expireAfterWrite(CACHE_EXPIRATION_IN_SECONDS, TimeUnit.SECONDS)
                .removalListener(this::refreshSchemaOnExpire)
                .build();

        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> cache.cleanUp(),
                CACHE_CLEANUP_INVOCATION_IN_SECONDS,
                CACHE_CLEANUP_INVOCATION_IN_SECONDS,
                TimeUnit.SECONDS);

        fieldProviders = new ArrayList<>();
        transformedProviders = new ArrayList<>();
        execStrategy = new ExecutionStrategyProviderImpl();
        errorHandler = new GraphQLErrorHandlerImpl();
    }

    @Override
    public void destroy() {
        scheduler.shutdownNow();
    }

    @Override
    public void handleEvent(Event event) {
        if(Events.REFRESH_SCHEMA.equals(event.getTopic())) {
            triggerSchemaRefresh((String) event.getProperty(Events.EVENT_REASON));
        }
    }

    @Override
    protected ExecutionStrategyProvider getExecutionStrategyProvider() {
        return execStrategy;
    }

    @Override
    protected GraphQLErrorHandler getGraphQLErrorHandler() {
        return errorHandler;
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

    private List<String> splitQueries(String requestContent) throws Exception {
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

    private boolean isBatchRequest(String requestContent) throws IOException {
        return new ObjectMapper().readTree(requestContent)
                .isArray();
    }

    private void triggerSchemaRefresh(String refreshReason) {
        LOGGER.debug("GraphQL schema refresh requested. Cause: {}", refreshReason);
        cache.put(Events.REFRESH_SCHEMA, true);
    }

    /**
     * Refreshes the schema periodically once the cache invalidates if a REFRESH_SCHEMA event was added to the cache.
     * This allows multiple threads to ask for a schema refresh while only refreshing the schema once.
     * @param notification
     */
    private void refreshSchemaOnExpire(RemovalNotification notification) {
        if (notification.getCause() == RemovalCause.EXPIRED) {
            refreshSchema();
        }
    }

    //Synchronized just in case the schema is still updating when another refresh is called
    //The performance decrease by the `synchronized` is negligible because of the periodic cache invalidation implementation
    private synchronized void refreshSchema() {
        LOGGER.debug("Refreshing GraphQL schema.");

        transformedProviders.forEach(this::unbindProvider);

        if(errorCodeProvider != null){
            unbindProvider(errorCodeProvider);
        }

        GraphQLTransformCommons transformer = new GraphQLTransformCommons();

        transformedProviders = fieldProviders.stream()
                .map(fieldProvider -> new GraphQLProviderImpl(fieldProvider, transformer))
                .collect(Collectors.toList());

        errorCodeProvider = transformer.getErrorCodesQueryProvider(fieldProviders);

        transformedProviders.forEach(this::bindProvider);

        bindProvider(errorCodeProvider);

        LOGGER.debug("Finished refreshing GraphQL schema.");
    }

    public void bindFieldProvider(FieldProvider fieldProvider) {
        if (fieldProvider == null) {
            return;
        }

        triggerSchemaRefresh(String.format(BINDING_FIELD_PROVIDER, fieldProvider.fieldTypeName()));
    }

    public void unbindFieldProvider(FieldProvider fieldProvider) {
        if (fieldProvider == null) {
            return;
        }

        triggerSchemaRefresh(String.format(UNBINDING_FIELD_PROVIDER, fieldProvider == null ? "" : fieldProvider.fieldTypeName()));
    }

    public void setFieldProviders(List<FieldProvider> fieldProviders) {
        this.fieldProviders = fieldProviders;
    }

    private static class GraphQLProviderImpl
            implements GraphQLProvider, GraphQLQueryProvider, GraphQLMutationProvider {

        private List<GraphQLFieldDefinition> queries;

        private List<GraphQLFieldDefinition> mutations;

        public GraphQLProviderImpl(FieldProvider provider,
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

    private static class ExtendedEnhancedExecutionStrategy extends EnhancedExecutionStrategy {

        @Override
        protected void handleDataFetchingException(ExecutionContext executionContext,
                GraphQLFieldDefinition fieldDef, Map<String, Object> argumentValues, Exception e) {
            if (e instanceof FunctionDataFetcherException) {
                for (ErrorMessage msg : ((FunctionDataFetcherException) e).getCustomMessages()) {
                    LOGGER.debug("Unsuccessful GraphQL request:\n", e.toString());
                    executionContext.addError(new DataFetchingGraphQLError(msg));
                }
            } else {
                super.handleDataFetchingException(executionContext, fieldDef, argumentValues, e);
            }
        }
    }

    private static class ExecutionStrategyProviderImpl implements ExecutionStrategyProvider {

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

    public static class GraphQLErrorHandlerImpl extends DefaultGraphQLErrorHandler {

        @Override
        protected boolean isClientError(GraphQLError error) {
            return error instanceof DataFetchingGraphQLError || super.isClientError(error);
        }
    }
}