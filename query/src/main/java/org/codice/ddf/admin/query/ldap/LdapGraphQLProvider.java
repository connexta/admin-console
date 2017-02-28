package org.codice.ddf.admin.query.ldap;

import static org.codice.ddf.admin.query.graphql.GraphQLCommons.fieldToGraphQLObjectType;
import static org.codice.ddf.admin.query.graphql.GraphQLCommons.fieldsToGraphQLFieldDefinition;

import java.util.Collection;
import java.util.HashMap;

import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;
import graphql.servlet.GraphQLMutationProvider;
import graphql.servlet.GraphQLQueryProvider;

public class LdapGraphQLProvider implements GraphQLQueryProvider, GraphQLMutationProvider {

    @Override
    public Collection<GraphQLFieldDefinition> getMutations() {
        return fieldsToGraphQLFieldDefinition(new LdapActionHandler().getPersistActions());
    }

    @Override
    public GraphQLObjectType getQuery() {
        return fieldToGraphQLObjectType(new LdapActionHandler());
    }

    @Override
    public Object context() {
        return new HashMap<>();
    }
}
