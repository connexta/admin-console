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
package org.codice.ddf.admin.graphql.transform;

import graphql.schema.GraphQLEnumType;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLList;
import graphql.servlet.GraphQLQueryProvider;
import graphql.servlet.GraphQLTypesProvider;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.apache.commons.lang.StringUtils;
import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.api.FieldProvider;
import org.codice.ddf.admin.api.fields.FunctionField;

public class GraphQLTransformCommons {

  private GraphQLTransformOutput transformOutput;

  public GraphQLTransformCommons() {
    transformOutput = new GraphQLTransformOutput();
  }

  public List<GraphQLFieldDefinition> fieldProviderToMutations(FieldProvider provider) {
    return transformOutput.fieldsToGraphQLFieldDefinition(provider.getMutationFunctions());
  }

  public List<GraphQLFieldDefinition> fieldProviderToQueries(FieldProvider provider) {
    return transformOutput.fieldsToGraphQLFieldDefinition(Arrays.asList(provider));
  }

  public GraphQLQueryProvider getErrorCodesQueryProvider(List<FieldProvider> fieldProviders) {
    Set<String> errorCodes = new TreeSet<>();

    for (FieldProvider fieldProvider : fieldProviders) {
      List<FunctionField> mutations = fieldProvider.getMutationFunctions();
      List<Field> queryFields = fieldProvider.getDiscoveryFields();

      for (FunctionField mutation : mutations) {
        errorCodes.addAll(mutation.getErrorCodes());
      }

      for (Field field : queryFields) {
        errorCodes.addAll(field.getErrorCodes());
      }
    }

    GraphQLEnumType.Builder enumTypeBuilder =
        GraphQLEnumType.newEnum().name("ErrorCode").description("All possible error codes.");
    errorCodes.forEach(enumTypeBuilder::value);
    GraphQLEnumType errorCodeEnumType = enumTypeBuilder.build();

    return () ->
        Collections.singletonList(
            GraphQLFieldDefinition.newFieldDefinition()
                .name("errorCodes")
                .description("Returns all the possible error codes from the graphQL schema.")
                .type(GraphQLList.list(errorCodeEnumType))
                .dataFetcher((dataFetchingEnvironment) -> errorCodes)
                .build());
  }

  public static String capitalize(String str) {
    return StringUtils.capitalize(str);
  }

  public List<GraphQLTypesProvider> getGraphQlTypeProviders() {
    return transformOutput.getTypeProviders();
  }
}
