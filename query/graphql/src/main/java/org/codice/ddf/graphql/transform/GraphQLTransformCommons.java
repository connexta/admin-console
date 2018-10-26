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

import graphql.schema.GraphQLEnumType;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLList;
import graphql.servlet.GraphQLProvider;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.apache.commons.lang.StringUtils;
import org.codice.ddf.admin.api.FieldProvider;
import org.codice.ddf.admin.api.fields.FunctionField;

public class GraphQLTransformCommons {

  private GraphQLTransformOutput transformOutput;

  private GraphQLTransformCommons() {
    transformOutput = new GraphQLTransformOutput();
  }

  public static GraphQLProvider createGraphQLProvider(List<FieldProvider> providers) {
    return new GraphQLTransformCommons().fieldProvidersToGraphQlProvider(providers);
  }

  private GraphQLProvider fieldProvidersToGraphQlProvider(List<FieldProvider> providers) {
    List<GraphQLFieldDefinition> queries =
        providers
            .stream()
            .map(this::fieldProviderToQueries)
            .flatMap(List::stream)
            .collect(Collectors.toList());
    GraphQLFieldDefinition errorDefinitions = getErrorCodesQueryProvider(providers);
    if (errorDefinitions != null) {
      queries.add(getErrorCodesQueryProvider(providers));
    }

    List<GraphQLFieldDefinition> mutations =
        providers
            .stream()
            .map(this::fieldProviderToMutations)
            .flatMap(List::stream)
            .collect(Collectors.toList());

    return new GraphQLProviderImpl(queries, mutations, transformOutput.getTypeProviders());
  }

  private List<GraphQLFieldDefinition> fieldProviderToMutations(FieldProvider provider) {
    return transformOutput.functionsToGraphQLFieldDefinition(provider.getMutationFunctions());
  }

  private List<GraphQLFieldDefinition> fieldProviderToQueries(FieldProvider provider) {
    return transformOutput.fieldsToGraphQLFieldDefinition(Arrays.asList(provider));
  }

  private GraphQLFieldDefinition getErrorCodesQueryProvider(List<FieldProvider> fieldProviders) {
    Set<String> errorCodes = new TreeSet<>();

    for (FieldProvider fieldProvider : fieldProviders) {
      List<FunctionField> mutations = fieldProvider.getMutationFunctions();
      List<FunctionField> queryFields = fieldProvider.getDiscoveryFunctions();

      for (FunctionField mutation : mutations) {
        errorCodes.addAll(mutation.getErrorCodes());
      }

      for (FunctionField field : queryFields) {
        errorCodes.addAll(field.getErrorCodes());
      }
    }

    if (errorCodes.isEmpty()) {
      return null;
    }

    GraphQLEnumType.Builder enumTypeBuilder =
        GraphQLEnumType.newEnum().name("ErrorCode").description("All possible error codes.");
    errorCodes.forEach(enumTypeBuilder::value);
    GraphQLEnumType errorCodeEnumType = enumTypeBuilder.build();

    return GraphQLFieldDefinition.newFieldDefinition()
        .name("errorCodes")
        .description("Returns all the possible error codes from the graphQL schema.")
        .type(GraphQLList.list(errorCodeEnumType))
        .dataFetcher(dataFetchingEnvironment -> errorCodes)
        .build();
  }

  static String capitalize(String str) {
    return StringUtils.capitalize(str);
  }
}
