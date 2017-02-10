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
 */
package org.codice.ddf.admin.query;

import static graphql.Scalars.GraphQLBoolean;
import static graphql.Scalars.GraphQLInt;
import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLEnumType.newEnum;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLInputObjectField.newInputObjectField;
import static graphql.schema.GraphQLInputObjectType.newInputObject;
import static graphql.schema.GraphQLObjectType.newObject;
import static graphql.schema.GraphQLSchema.newSchema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import graphql.GraphQL;
import graphql.annotations.GraphQLField;
import graphql.schema.GraphQLEnumType;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLInterfaceType;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import graphql.servlet.GraphQLQueryProvider;

public class Ldap implements GraphQLQueryProvider {

    @Override
    public GraphQLObjectType getQuery() {
        GraphQLEnumType EncryptionMethodType = newEnum().name("EncryptionMethodType")
                .description("The encryption method to connect to LDAP.")
                .value("none")
                .value("tls")
                .value("startTls")
                .build();

        GraphQLInputObjectType LdapConfigType = newInputObject().name("LdapConfigType")
                .field(newInputObjectField().name("hostname")
                        .type(GraphQLString))
                .field(newInputObjectField().name("port")
                        .type(GraphQLInt))
                .field(newInputObjectField().name("encryptionMethod")
                        .type(EncryptionMethodType))
                .field(newInputObjectField().name("bindUserDn")
                        .type(GraphQLString))
                .field(newInputObjectField().name("bindUserPassword")
                        .type(GraphQLString))
                .build();

        GraphQLObjectType LdapEntry = newObject().name("LdapEntry")
                .field(newFieldDefinition().name("cn")
                        .type(GraphQLString))
                .build();

        GraphQLObjectType LdapType = newObject().name("LdapType")
                .field(newFieldDefinition().type(GraphQLBoolean)
                        .name("canConnect")
                        .dataFetcher(env -> {

                            //Map<String, Object> config = (Map<String, Object>) env.getSource();
                            //System.out.println(config);
                            return false;
                        }))
                .field(newFieldDefinition().type(GraphQLBoolean)
                        .name("canBind")
                        .staticValue(false))
                .field(newFieldDefinition().type(new GraphQLList(LdapEntry))
                        .name("entries")
                        .argument(arg -> arg.type(GraphQLString)
                                .name("query"))
                        .argument(arg -> arg.type(GraphQLString)
                                .name("queryBase"))
                        .dataFetcher(env -> {
                            return new ArrayList<>();
                        }))
                .field(newFieldDefinition().type(new GraphQLList(GraphQLString))
                        .name("entryAttributes")
                        .dataFetcher(env -> {
                            return new ArrayList<>();
                        }))
                .build();

        return newObject().name("test")
                .field(newFieldDefinition().type(LdapType)
                        .name("ldap")
                        .argument(arg -> arg.type(LdapConfigType)
                                .name("config"))
                        .dataFetcher(env -> {
                            env.getArgument("config");
                            return true;
                        }))
                .build();
    }

    @Override
    public Object context() {
        return new HashMap<>();
    }
}
