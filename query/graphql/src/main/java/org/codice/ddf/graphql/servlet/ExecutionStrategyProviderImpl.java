/**
 * Copyright (c) Codice Foundation
 *
 * <p>This is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version.
 *
 * <p>This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details. A copy of the GNU Lesser General Public
 * License is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 */
package org.codice.ddf.graphql.servlet;

import graphql.execution.AsyncExecutionStrategy;
import graphql.execution.DataFetcherExceptionHandlerParameters;
import graphql.execution.ExecutionPath;
import graphql.execution.ExecutionStrategy;
import graphql.execution.SimpleDataFetcherExceptionHandler;
import graphql.servlet.ExecutionStrategyProvider;
import java.util.List;
import org.codice.ddf.admin.api.report.ErrorMessage;
import org.codice.ddf.graphql.FunctionDataFetcherException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecutionStrategyProviderImpl implements ExecutionStrategyProvider {
  private static final Logger LOGGER = LoggerFactory.getLogger(ExecutionStrategyProviderImpl.class);

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

  public static class ExtendedEnhancedExecutionStrategy extends AsyncExecutionStrategy {

    ExtendedEnhancedExecutionStrategy() {
      super(new DataFetcherExceptionHandlerImpl());
    }
  }

  private static class DataFetcherExceptionHandlerImpl extends SimpleDataFetcherExceptionHandler {

    @Override
    public void accept(
        DataFetcherExceptionHandlerParameters dataFetcherExceptionHandlerParameters) {
      Throwable e = dataFetcherExceptionHandlerParameters.getException();

      if (e instanceof FunctionDataFetcherException) {
        for (ErrorMessage msg : ((FunctionDataFetcherException) e).getCustomMessages()) {
          LOGGER.trace("Unsuccessful GraphQL request:\n", e);
          ExecutionPath executionPath = listToExecutionPath(msg.getPath());
          dataFetcherExceptionHandlerParameters
              .getExecutionContext()
              .addError(new DataFetchingGraphQLError(msg, executionPath), executionPath);
        }
      } else {
        LOGGER.debug("Internal error.", e);
        super.accept(dataFetcherExceptionHandlerParameters);
      }
    }

    private static ExecutionPath listToExecutionPath(List<Object> path) {
      ExecutionPath transformedPath = ExecutionPath.rootPath();
      for (Object seg : path) {
        if (seg instanceof String) {
          transformedPath = transformedPath.segment((String) seg);
        } else {
          transformedPath = transformedPath.segment((Integer) seg);
        }
      }
      return transformedPath;
    }
  }
}
