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
package org.codice.ddf.admin.query.ldap;

import static org.codice.ddf.admin.query.graphql.GraphQLCommons.actionCreatorToGraphQLObjectType;
import static org.codice.ddf.admin.query.graphql.GraphQLCommons.actionsToGraphQLFieldDef;

import java.util.Collection;
import java.util.HashMap;

import org.codice.ddf.admin.query.api.action.ActionCreator;
import org.codice.ddf.admin.query.ldap.actions.LdapActionCreator;

import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;
import graphql.servlet.GraphQLMutationProvider;
import graphql.servlet.GraphQLQueryProvider;

public class LdapGraphQLProvider implements GraphQLQueryProvider, GraphQLMutationProvider {

    public static final ActionCreator LDAP_ACTION_CREATOR = new LdapActionCreator();

    @Override
    public Collection<GraphQLFieldDefinition> getMutations() {
        return actionsToGraphQLFieldDef(LDAP_ACTION_CREATOR, LDAP_ACTION_CREATOR.getPersistActions());
    }

    @Override
    public GraphQLObjectType getQuery() {
        return actionCreatorToGraphQLObjectType(LDAP_ACTION_CREATOR, LDAP_ACTION_CREATOR.getDiscoveryActions());
    }

    @Override
    public String getName() {
        return LDAP_ACTION_CREATOR.name();
    }

    @Override
    public Object context() {
        return new HashMap<>();
    }
}
