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
package org.codice.ddf.admin.query.request;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.fail;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.boon.Boon;
import org.codice.ddf.admin.ldap.fields.config.LdapConfigurationField;
import org.codice.ddf.itests.common.WaitCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LdapRequestHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(LdapRequestHelper.class);

    public static final String LDAP_QUERY_RESOURCE_PATH = "/query/ldap/query/";

    public static final String LDAP_MUTATION_RESOURCE_PATH = "/query/ldap/mutation/";

    private GraphQlHelper requestFactory;

    public LdapRequestHelper(String graphQlEndpoint) {
        requestFactory = new GraphQlHelper(LdapRequestHelper.class,
                LDAP_QUERY_RESOURCE_PATH,
                LDAP_MUTATION_RESOURCE_PATH,
                graphQlEndpoint);
    }

    public void waitForLdapInSchema() {
        WaitCondition.expect("Ldap appears in schema.")
                .within(30L, TimeUnit.SECONDS)
                .until(() -> {
                    LOGGER.info("Waiting for ldap to appear in graphql schema.");
                    return getLdapConfigs() != null;
                });
    }

    public List<Map<String, Object>> getLdapConfigs() {
        return requestFactory.createRequest()
                .usingQuery("GetLdapConfigs.graphql")
                .send()
                .getResponse()
                .jsonPath()
                .get("data.ldap.configs");
    }

    public void waitForConfigs(List<Map<String, Object>> expectedConfigs, boolean ignorePid) {
        WaitCondition.expect("Successfully retrieved Ldap configuration.")
                .within(30L, TimeUnit.SECONDS)
                .until(() -> {
                    List<Map<String, Object>> retrievedConfigs = getLdapConfigs();
                    if (retrievedConfigs == null) {
                        return false;
                    }

                    if (ignorePid) {
                        expectedConfigs.forEach(map -> map.entrySet()
                                .removeIf(entry -> entry.getKey()
                                        .equals("pid")));

                        retrievedConfigs.forEach(map -> map.entrySet()
                                .removeIf(entry -> entry.getKey()
                                        .equals("pid")));
                    }

                    boolean conditionMet = expectedConfigs.containsAll(retrievedConfigs)
                            && retrievedConfigs.containsAll(expectedConfigs);

                    if (!conditionMet) {
                        LOGGER.info("Expecting configs:\n{}", Boon.toJson(expectedConfigs));
                        LOGGER.info("Received:\n{}", Boon.toJson(retrievedConfigs));
                    }

                    return conditionMet;
                });
    }

    public void createLdapConfig(LdapConfigurationField config) {
        try {
            List errors = requestFactory.createRequest()
                    .usingMutation("CreateLdapConfig.graphql")
                    .addArgument("connection",
                            config.connectionField()
                                    .getValue())
                    .addArgument("bindInfo",
                            config.bindUserInfoField()
                                    .getValue())
                    .addArgument("settings",
                            config.settingsField()
                                    .getValue())
                    .addArgument("claimsMapping",
                            config.claimMappingsField()
                                    .getValue())
                    .send()
                    .getResponse()
                    .jsonPath()
                    .get("errors");

            assertThat(errors, is(nullValue()));
        } catch (Exception e) {
            fail("Something went wrong saving white list contexts query.\n" + e);
            resetLdapConfigs();
        }
    }

    public void deleteLdapConfig(String pid) {
        try {
            List errors = requestFactory.createRequest()
                    .usingMutation("DeleteLdapConfig.graphql")
                    .addArgument("pid", pid)
                    .send()
                    .getResponse()
                    .jsonPath()
                    .get("errors");

            assertThat(errors, is(nullValue()));
        } catch (Exception e) {
            fail("Something went wrong deleting ldap configuration.\n" + e);
        }
    }

    public void resetLdapConfigs() {
        getLdapConfigs().stream()
                .map(config -> (String) config.get("pid"))
                .forEach(this::deleteLdapConfig);
        waitForConfigs(Collections.emptyList(), true);
    }

}
