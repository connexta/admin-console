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

import graphql.Scalars;
import graphql.schema.GraphQLScalarType;
import org.codice.ddf.admin.api.fields.ScalarField;
import org.codice.ddf.admin.graphql.GraphQLTypesProviderImpl;

public class GraphQLTransformScalar {

  GraphQLTypesProviderImpl<GraphQLScalarType> scalarTypesProvider;

  public GraphQLTransformScalar() {
    scalarTypesProvider = new GraphQLTypesProviderImpl<>();
  }

  public GraphQLScalarType resolveScalarType(ScalarField field) {
    if (scalarTypesProvider.isTypePresent(field.getFieldTypeName())) {
      return scalarTypesProvider.getType(field.getFieldTypeName());
    }

    GraphQLScalarType type = null;

    switch (field.getScalarType()) {
      case INTEGER:
        type =
            field.getFieldTypeName() == null
                ? Scalars.GraphQLInt
                : new GraphQLScalarType(
                    field.getFieldTypeName(),
                    field.getDescription(),
                    Scalars.GraphQLInt.getCoercing());
        break;

      case BOOLEAN:
        type =
            field.getFieldTypeName() == null
                ? Scalars.GraphQLBoolean
                : new GraphQLScalarType(
                    field.getFieldTypeName(),
                    field.getDescription(),
                    Scalars.GraphQLBoolean.getCoercing());
        break;

      case STRING:
        type =
            field.getFieldTypeName() == null
                ? Scalars.GraphQLString
                : new GraphQLScalarType(
                    field.getFieldTypeName(),
                    field.getDescription(),
                    Scalars.GraphQLString.getCoercing());
        break;
      case FLOAT:
        type =
            field.getFieldTypeName() == null
                ? Scalars.GraphQLFloat
                : new GraphQLScalarType(
                    field.getFieldTypeName(),
                    field.getDescription(),
                    Scalars.GraphQLFloat.getCoercing());
    }

    scalarTypesProvider.addType(field.getFieldTypeName(), type);
    return type;
  }

  public GraphQLTypesProviderImpl getScalarTypesProvider() {
    return scalarTypesProvider;
  }
}
