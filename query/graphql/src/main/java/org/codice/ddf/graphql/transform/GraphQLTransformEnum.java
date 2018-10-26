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
import graphql.servlet.GraphQLTypesProvider;
import org.codice.ddf.admin.api.fields.EnumField;
import org.codice.ddf.admin.api.fields.EnumValue;

public class GraphQLTransformEnum {

  private GraphQLTypesProviderImpl<GraphQLEnumType> enumTypeProvider;

  public GraphQLTransformEnum() {
    this.enumTypeProvider = new GraphQLTypesProviderImpl<>();
  }

  public GraphQLEnumType enumFieldToGraphQLEnumType(EnumField<Object, EnumValue<Object>> field) {

    if (enumTypeProvider.isTypePresent(field.getFieldType())) {
      return enumTypeProvider.getType(field.getFieldType());
    }

    GraphQLEnumType.Builder builder =
        GraphQLEnumType.newEnum()
            .name(GraphQLTransformCommons.capitalize(field.getFieldType()))
            .description(field.getDescription());

    field
        .getEnumValues()
        .forEach(val -> builder.value(val.getEnumTitle(), val.getValue(), val.getDescription()));

    GraphQLEnumType newEnum = builder.build();
    enumTypeProvider.addType(field.getFieldType(), newEnum);
    return newEnum;
  }

  public GraphQLTypesProvider getEnumTypeProvider() {
    return enumTypeProvider;
  }
}
