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
package org.codice.ddf.graphql.transform;

import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLType;
import graphql.servlet.GraphQLMutationProvider;
import graphql.servlet.GraphQLProvider;
import graphql.servlet.GraphQLQueryProvider;
import graphql.servlet.GraphQLTypesProvider;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class GraphQLProviderImpl
    implements GraphQLProvider,
        GraphQLQueryProvider,
        GraphQLTypesProvider,
        GraphQLMutationProvider {

  private final List<GraphQLFieldDefinition> queries;
  private final List<GraphQLFieldDefinition> mutations;
  private final List<GraphQLTypesProvider> typesProviders;

  public GraphQLProviderImpl(
      List<GraphQLFieldDefinition> queries,
      List<GraphQLFieldDefinition> mutations,
      List<GraphQLTypesProvider> typesProviders) {
    this.queries = queries;
    this.mutations = mutations;
    this.typesProviders = typesProviders;
  }

  @Override
  public Collection<GraphQLFieldDefinition> getQueries() {
    return queries;
  }

  @Override
  public Collection<GraphQLFieldDefinition> getMutations() {
    return mutations;
  }

  @Override
  public Collection<GraphQLType> getTypes() {
    return typesProviders
        .stream()
        .map(GraphQLTypesProvider::getTypes)
        .flatMap(Collection::stream)
        .collect(Collectors.toList());
  }
}
