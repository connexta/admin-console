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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.codice.ddf.admin.api.FieldProvider;
import org.codice.ddf.admin.api.report.ErrorMessage;
import org.codice.ddf.admin.graphql.servlet.request.DelegateRequest;
import org.codice.ddf.admin.graphql.servlet.request.DelegateResponse;
import org.codice.ddf.admin.graphql.transform.FunctionDataFetcherException;
import org.codice.ddf.admin.graphql.transform.GraphQLTransformCommons;
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
import graphql.servlet.OsgiGraphQLServlet;

public class ExtendedOsgiGraphQLServlet extends OsgiGraphQLServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExtendedOsgiGraphQLServlet.class);

    private GraphQLTransformCommons transformCommons;

    private ExecutionStrategyProvider execStrategy;

    private Map<String, GraphQLMutationProvider> graphQLMutationProviders;

    private Map<String, GraphQLQueryProvider> graphQLQueryProviders;

    public ExtendedOsgiGraphQLServlet() {
        super();
        transformCommons = new GraphQLTransformCommons();
        execStrategy = new ExecutionStrategyProviderImpl();
        graphQLMutationProviders = new HashMap<>();
        graphQLQueryProviders = new HashMap<>();
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

    public void bindFieldProvider(FieldProvider fieldProvider) {

        if (CollectionUtils.isNotEmpty(fieldProvider.getDiscoveryFields())) {
            LOGGER.debug("Binding queries of field provider {} to graphql servlet.",
                    fieldProvider.fieldName());
            try {
                GraphQLQueryProvider queryProvider = new GraphQLQueryProviderImpl(fieldProvider,
                        transformCommons);
                bindQueryProvider(queryProvider);
                graphQLQueryProviders.put(fieldProvider.fieldTypeName(), queryProvider);
            } catch (Exception e) {
                LOGGER.error("Unable to bind queries of field provider {} to graphql servlet.",
                        fieldProvider.fieldName(),
                        e);
            }
        }

        if (CollectionUtils.isNotEmpty(fieldProvider.getMutationFunctions())) {
            LOGGER.debug("Binding mutations of field provider {} to graphql servlet.",
                    fieldProvider.fieldName());
            try {
                GraphQLMutationProvider mutationProvider = new GraphQLMutationProviderImpl(
                        fieldProvider,
                        transformCommons);
                bindMutationProvider(mutationProvider);
                graphQLMutationProviders.put(fieldProvider.fieldTypeName(), mutationProvider);
            } catch (Exception e) {
                LOGGER.error("Unable to bind mutations of field provider {} to graphql servlet.",
                        fieldProvider.fieldName(),
                        e);
            }
        }
    }

    public void unbindFieldProvider(FieldProvider fieldProvider) {
        if (CollectionUtils.isNotEmpty(fieldProvider.getDiscoveryFields())) {
            LOGGER.debug("Unbinding queries of field provider {} to graphql servlet.",
                    fieldProvider.fieldName());
            try {
                unbindQueryProvider(graphQLQueryProviders.get(fieldProvider.fieldTypeName()));
                graphQLQueryProviders.remove(fieldProvider.fieldTypeName());
            } catch (Exception e) {
                LOGGER.error("Unable to unbind queries of field provider {} from graphql servlet.",
                        fieldProvider.fieldName(),
                        e);
            }
        }

        if (CollectionUtils.isNotEmpty(fieldProvider.getMutationFunctions())) {
            LOGGER.debug("Unbinding mutations of field provider {} to graphql servlet.",
                    fieldProvider.fieldName());
            try {
                unbindMutationProvider(graphQLMutationProviders.get(fieldProvider.fieldTypeName()));
                graphQLMutationProviders.remove(fieldProvider.fieldTypeName());
            } catch (Exception e) {
                LOGGER.error("Unable to unbind mutations of field provider {} from graphql servlet.",
                        fieldProvider.fieldName(),
                        e);
            }
        }
    }

    public class GraphQLQueryProviderImpl implements GraphQLQueryProvider {

        private List<GraphQLFieldDefinition> queries;

        public GraphQLQueryProviderImpl(FieldProvider provider,
                GraphQLTransformCommons transformCommons) {
            queries = transformCommons.fieldProviderToQueries(provider);
        }

        @Override
        public Collection<GraphQLFieldDefinition> getQueries() {
            return queries;
        }
    }

    public class GraphQLMutationProviderImpl implements GraphQLMutationProvider {

        private List<GraphQLFieldDefinition> mutations;

        public GraphQLMutationProviderImpl(FieldProvider provider,
                GraphQLTransformCommons transformCommons) {
            mutations = transformCommons.fieldProviderToMutations(provider);
        }

        @Override
        public Collection<GraphQLFieldDefinition> getMutations() {
            return mutations;
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
}