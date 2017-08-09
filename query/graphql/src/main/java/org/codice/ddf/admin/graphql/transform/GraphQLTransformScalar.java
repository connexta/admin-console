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
package org.codice.ddf.admin.graphql.transform;

import org.codice.ddf.admin.api.fields.ScalarField;
import org.codice.ddf.admin.graphql.GraphQLTypesProviderImpl;

import graphql.Scalars;
import graphql.schema.Coercing;
import graphql.schema.GraphQLScalarType;

public class GraphQLTransformScalar {

    GraphQLTypesProviderImpl<GraphQLScalarType> scalarTypesProvider;

    private static final String HIDDEN_FLAG = "*****";

    private static final Coercing HIDDEN_COERCING  = new Coercing() {
        @Override
        public Object serialize(Object input) {
            return HIDDEN_FLAG;
        }

        @Override
        public Object parseValue(Object input) {
            return Scalars.GraphQLString.getCoercing()
                    .parseValue(input);
        }

        @Override
        public Object parseLiteral(Object input) {
            return Scalars.GraphQLString.getCoercing()
                    .parseLiteral(input);
        }

    };

    public GraphQLTransformScalar() {
        scalarTypesProvider = new GraphQLTypesProviderImpl<>();
    }

    public GraphQLScalarType resolveScalarType(ScalarField field) {
        if(scalarTypesProvider.isTypePresent(field.fieldTypeName())) {
            return scalarTypesProvider.getType(field.fieldTypeName());
        }

        GraphQLScalarType type = null;

        switch (field.scalarType()) {
        case INTEGER:
            type = field.fieldTypeName() == null ?
                    Scalars.GraphQLInt :
                    new GraphQLScalarType(field.fieldTypeName(), field.description(), Scalars.GraphQLInt.getCoercing());
            break;

        case BOOLEAN:
            type = field.fieldTypeName() == null ?
                    Scalars.GraphQLBoolean :
                    new GraphQLScalarType(field.fieldTypeName(), field.description(), Scalars.GraphQLBoolean.getCoercing());
            break;

        case STRING:
            type = field.fieldTypeName() == null ? Scalars.GraphQLString :
                        new GraphQLScalarType(field.fieldTypeName(), field.description(), Scalars.GraphQLString.getCoercing());
            break;
        case FLOAT:
            type = field.fieldTypeName() == null ? Scalars.GraphQLFloat :
                    new GraphQLScalarType(field.fieldTypeName(), field.description(), Scalars.GraphQLFloat.getCoercing());
            break;
        case HIDDEN_STRING:
            type = field.fieldTypeName() == null ? Scalars.GraphQLString :
                    new GraphQLScalarType(field.fieldTypeName(), field.description(), HIDDEN_COERCING);
        }

        scalarTypesProvider.addType(field.fieldTypeName(), type);
        return type;
    }

    public GraphQLTypesProviderImpl getScalarTypesProvider() {
        return scalarTypesProvider;
    }
}
