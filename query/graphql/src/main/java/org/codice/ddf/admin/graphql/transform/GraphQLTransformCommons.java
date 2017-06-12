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

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.codice.ddf.admin.api.FieldProvider;

import graphql.schema.GraphQLFieldDefinition;

public class GraphQLTransformCommons {

    private GraphQLTransformOutput transformOutput;

    public GraphQLTransformCommons() {
        transformOutput = new GraphQLTransformOutput();
    }


    public List<GraphQLFieldDefinition> fieldProviderToMutations(FieldProvider provider){
        return transformOutput.fieldsToGraphQLFieldDefinition(provider.getMutationFunctions());
    }

    public List<GraphQLFieldDefinition> fieldProviderToQueries(FieldProvider provider) {
        return transformOutput.fieldsToGraphQLFieldDefinition(Arrays.asList(provider));
    }

    public static String capitalize(String str) {
        return StringUtils.capitalize(str);
    }

}