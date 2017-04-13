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

package org.codice.ddf.admin.security.ldap.test;

import static org.codice.ddf.admin.api.config.ldap.LdapConfiguration.ATTRIBUTE_MAPPINGS;
import static org.codice.ddf.admin.api.handler.ConfigurationMessage.INVALID_FIELD;
import static org.codice.ddf.admin.api.handler.ConfigurationMessage.buildMessage;
import static org.codice.ddf.admin.api.handler.ConfigurationMessage.createInvalidFieldMsg;
import static org.codice.ddf.admin.api.handler.commons.HandlerCommons.SUCCESSFUL_TEST;
import static org.codice.ddf.admin.api.handler.report.Report.createReport;
import static org.codice.ddf.admin.api.services.PolicyManagerServiceProperties.STS_CLAIMS_CONFIGURATION_CONFIG_ID;
import static org.codice.ddf.admin.api.services.PolicyManagerServiceProperties.STS_CLAIMS_PROPS_KEY_CLAIMS;
import static org.codice.ddf.admin.security.ldap.LdapConnectionResult.CANNOT_BIND;
import static org.codice.ddf.admin.security.ldap.LdapConnectionResult.CANNOT_CONFIGURE;
import static org.codice.ddf.admin.security.ldap.LdapConnectionResult.CANNOT_CONNECT;
import static org.codice.ddf.admin.security.ldap.LdapConnectionResult.SUCCESSFUL_BIND;
import static org.codice.ddf.admin.security.ldap.LdapConnectionResult.toDescriptionMap;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.codice.ddf.admin.api.config.ldap.LdapConfiguration;
import org.codice.ddf.admin.api.handler.method.TestMethod;
import org.codice.ddf.admin.api.handler.report.Report;
import org.codice.ddf.admin.configurator.Configurator;
import org.forgerock.opendj.ldap.Connection;
import org.forgerock.opendj.ldap.Filter;
import org.forgerock.opendj.ldap.SearchScope;
import org.forgerock.opendj.ldap.responses.SearchResultEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class AttributeMappingTestMethod extends TestMethod<LdapConfiguration> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AttributeMappingTestMethod.class);

    public static final String DESCRIPTION =
            "Verifies that sts mapping values are valid and exist.";

    public static final List<String> REQUIRED_FIELDS = ImmutableList.of(ATTRIBUTE_MAPPINGS);

    private static final String LDAP_ATTRIBUTE_MAPPING_TEST_ID = "attribute-mapping";

    private static final Map<String, String> SUCCESS_TYPES = ImmutableMap.of(SUCCESSFUL_TEST,
            "Attribute mapping was successfully validated.");

    public static final Map<String, String> FAILURE_TYPES =
            new ImmutableMap.Builder<String, String>().putAll(toDescriptionMap(Arrays.asList(
                    CANNOT_CONFIGURE,
                    CANNOT_CONNECT,
                    CANNOT_BIND)))
                    .put(INVALID_FIELD, "The given attribute mapping is invalid.")
                    .build();

    private final Configurator configurator;

    private final LdapTestingCommons ldapTestingCommons;

    public AttributeMappingTestMethod(LdapTestingCommons ldapTestingCommons,
            Configurator configurator) {
        super(LDAP_ATTRIBUTE_MAPPING_TEST_ID,
                DESCRIPTION,
                REQUIRED_FIELDS,
                null,
                SUCCESS_TYPES,
                FAILURE_TYPES,
                null);

        this.ldapTestingCommons = ldapTestingCommons;
        this.configurator = configurator;
    }

    @Override
    public Report test(LdapConfiguration configuration) {
        // First check for any unknown claims. This should never happen in a correctly configured
        // system. Bail out on first unknown claim.
        List stsClaims = Arrays.asList((String[]) configurator.getConfig(
                STS_CLAIMS_CONFIGURATION_CONFIG_ID)
                .get(STS_CLAIMS_PROPS_KEY_CLAIMS));
        Optional<String> unknownStsClaim = configuration.attributeMappings()
                .keySet()
                .stream()
                .filter(claim -> !stsClaims.contains(claim))
                .findFirst();

        if (unknownStsClaim.isPresent()) {
            return new Report(createInvalidFieldMsg(String.format(
                    "Unknown STS claim [%s], the STS properties are not set to handle this claim.",
                    unknownStsClaim.get()), ATTRIBUTE_MAPPINGS));
        }

        // If all claims were present, check the attributes provided. Search the user base for
        // user entries that have each of the mapped attributes. If at least one is found for
        // each attribute, that is a success condition; else, it is a warning that should be
        // reported to the user.
        try (LdapTestingCommons.LdapConnectionAttempt connectionAttempt = ldapTestingCommons.bindUserToLdapConnection(
                configuration)) {

            if (connectionAttempt.result() != SUCCESSFUL_BIND) {
                return createReport(SUCCESS_TYPES,
                        FAILURE_TYPES,
                        Collections.emptyMap(),
                        Collections.singletonList(connectionAttempt.result()
                                .name()));
            }

            Connection ldapConnection = connectionAttempt.connection();
            Optional<String> unknownAttribute = configuration.attributeMappings()
                    .values()
                    .stream()
                    .filter(s -> !checkClaimAttr(s, configuration, ldapConnection))
                    .findFirst();

            if (unknownAttribute.isPresent()) {
                return new Report(createInvalidFieldMsg(String.format(
                        "No user found with attribute [%s].",
                        unknownAttribute.get()), ATTRIBUTE_MAPPINGS));
            }
        } catch (IOException e) {
            LOGGER.debug("Unexpected error closing connection", e);
        }

        return new Report(buildMessage(SUCCESS_TYPES, FAILURE_TYPES, null, SUCCESSFUL_TEST));
    }

    private boolean checkClaimAttr(String attr, LdapConfiguration configuration,
            Connection ldapConnection) {
        List<SearchResultEntry> userWithAttrResults = ldapTestingCommons.getLdapQueryResults(
                ldapConnection,
                configuration.baseUserDn(),
                Filter.and(Filter.present(configuration.userNameAttribute()), Filter.present(attr))
                        .toString(),
                SearchScope.WHOLE_SUBTREE,
                1);
        return !userWithAttrResults.isEmpty();
    }
}
