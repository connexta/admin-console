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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.codice.ddf.admin.api.DataType;
import org.codice.ddf.admin.api.report.FunctionReport;
import org.codice.ddf.admin.api.report.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import graphql.ExceptionWhileDataFetching;
import graphql.ExecutionResult;
import graphql.ExecutionResultImpl;
import graphql.annotations.EnhancedExecutionStrategy;
import graphql.execution.ExecutionContext;
import graphql.language.Field;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;

public class ExecutionStrategyImpl extends EnhancedExecutionStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutionStrategyImpl.class);

    @Override
    public ExecutionResult execute(ExecutionContext executionContext, GraphQLObjectType parentType,
            Object source, Map<String, List<Field>> fields) {
        Map<String, Object> results = new LinkedHashMap<String, Object>();
        for (String fieldName : fields.keySet()) {
            List<Field> fieldList = fields.get(fieldName);
            ExecutionResult resolvedResult = resolveField(executionContext,
                    parentType,
                    source,
                    fieldList);

            results.put(fieldName, resolvedResult != null ? resolvedResult.getData() : null);
        }
        return new ExecutionResultImpl(results, executionContext.getErrors());
    }

    @Override
    protected ExecutionResult resolveField(ExecutionContext executionContext,
            GraphQLObjectType parentType, Object source, List<Field> fields) {
        GraphQLFieldDefinition fieldDef = getFieldDef(executionContext.getGraphQLSchema(),
                parentType,
                fields.get(0));

        Map<String, Object> argumentValues =
                valuesResolver.getArgumentValues(fieldDef.getArguments(),
                        fields.get(0)
                                .getArguments(),
                        executionContext.getVariables());
        DataFetchingEnvironment environment = new DataFetchingEnvironment(source,
                argumentValues,
                executionContext.getRoot(),
                fields,
                fieldDef.getType(),
                parentType,
                executionContext.getGraphQLSchema());

        Object resolvedValue = null;
        try {
            resolvedValue = fieldDef.getDataFetcher()
                    .get(environment);
            if (resolvedValue instanceof FunctionReport) {
                FunctionReport<DataType> report = ((FunctionReport) resolvedValue);
                resolvedValue = report.isResultPresent() ? report.result().getValue() : null;
                List<Message> msgs = report.messages();
                msgs.stream()
                        .map(GraphQLErrorMessageWrapper::new)
                        .forEach(executionContext::addError);
            } else if(resolvedValue instanceof DataType){
                resolvedValue = ((DataType) resolvedValue).getValue();
            }
        } catch (Exception e) {
            LOGGER.info("Exception while fetching data", e);
            executionContext.addError(new ExceptionWhileDataFetching(e));
        }

        return completeValue(executionContext, fieldDef.getType(), fields, resolvedValue);
    }
}
