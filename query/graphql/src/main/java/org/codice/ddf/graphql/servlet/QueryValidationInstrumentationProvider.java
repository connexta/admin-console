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

import com.google.common.collect.ImmutableList;
import graphql.analysis.MaxQueryComplexityInstrumentation;
import graphql.analysis.MaxQueryDepthInstrumentation;
import graphql.execution.instrumentation.ChainedInstrumentation;
import graphql.execution.instrumentation.Instrumentation;
import graphql.servlet.InstrumentationProvider;

public class QueryValidationInstrumentationProvider implements InstrumentationProvider {

  private static final int MAX_COMPLEXITY = 1000;
  private static final int MAX_QUERY_DEPTH = 100;

  private static final MaxQueryDepthInstrumentation MAX_QUERY_DEPTH_INSTRUMENTATION =
      new MaxQueryDepthInstrumentation(MAX_QUERY_DEPTH);
  private static final MaxQueryComplexityInstrumentation MAX_QUERY_COMPLEXITY_INSTRUMENTATION =
      new MaxQueryComplexityInstrumentation(MAX_COMPLEXITY);
  private static final ChainedInstrumentation ALL_INSTRUMENTATIONS =
      new ChainedInstrumentation(
          ImmutableList.of(MAX_QUERY_DEPTH_INSTRUMENTATION, MAX_QUERY_COMPLEXITY_INSTRUMENTATION));

  @Override
  public Instrumentation getInstrumentation() {
    return ALL_INSTRUMENTATIONS;
  }
}
