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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import graphql.schema.GraphQLType;
import graphql.servlet.GraphQLTypesProvider;

public class GraphQLTypesProviderImpl implements GraphQLTypesProvider {

    private Map<String, GraphQLType> types;

    public GraphQLTypesProviderImpl() {
        types = new HashMap<>();
    }

    @Override
    public Collection<GraphQLType> getTypes() {
        return types.values();
    }

    public void addType(String typeName, GraphQLType type) {
        types.put(typeName, type);
    }

    public boolean isTypePresent(String typeName) {
        return typeName != null && types.containsKey(typeName);
    }

    public <T extends GraphQLType> T getType(String typeName) {
        return (T) types.get(typeName);
    }
}
