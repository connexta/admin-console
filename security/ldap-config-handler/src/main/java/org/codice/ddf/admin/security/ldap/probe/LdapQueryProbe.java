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
package org.codice.ddf.admin.security.ldap.probe;

import static org.codice.ddf.admin.api.config.ldap.LdapConfiguration.BIND_METHOD;
import static org.codice.ddf.admin.api.config.ldap.LdapConfiguration.BIND_REALM;
import static org.codice.ddf.admin.api.config.ldap.LdapConfiguration.BIND_USER;
import static org.codice.ddf.admin.api.config.ldap.LdapConfiguration.BIND_USER_PASSWORD;
import static org.codice.ddf.admin.api.config.ldap.LdapConfiguration.ENCRYPTION_METHOD;
import static org.codice.ddf.admin.api.config.ldap.LdapConfiguration.HOST_NAME;
import static org.codice.ddf.admin.api.config.ldap.LdapConfiguration.LDAP_TYPE;
import static org.codice.ddf.admin.api.config.ldap.LdapConfiguration.PORT;
import static org.codice.ddf.admin.api.config.ldap.LdapConfiguration.QUERY;
import static org.codice.ddf.admin.api.config.ldap.LdapConfiguration.QUERY_BASE;
import static org.codice.ddf.admin.api.handler.commons.HandlerCommons.SUCCESSFUL_PROBE;
import static org.codice.ddf.admin.api.handler.report.ProbeReport.createProbeReport;
import static org.codice.ddf.admin.api.validation.LdapValidationUtils.validateBindRealm;
import static org.codice.ddf.admin.security.ldap.LdapConnectionResult.CANNOT_BIND;
import static org.codice.ddf.admin.security.ldap.LdapConnectionResult.CANNOT_CONFIGURE;
import static org.codice.ddf.admin.security.ldap.LdapConnectionResult.CANNOT_CONNECT;
import static org.codice.ddf.admin.security.ldap.LdapConnectionResult.SUCCESSFUL_BIND;
import static org.codice.ddf.admin.security.ldap.LdapConnectionResult.toDescriptionMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.codice.ddf.admin.api.config.ldap.LdapConfiguration;
import org.codice.ddf.admin.api.handler.ConfigurationMessage;
import org.codice.ddf.admin.api.handler.method.ProbeMethod;
import org.codice.ddf.admin.api.handler.report.ProbeReport;
import org.codice.ddf.admin.security.ldap.test.LdapTestingCommons;
import org.forgerock.opendj.ldap.Attribute;
import org.forgerock.opendj.ldap.ByteString;
import org.forgerock.opendj.ldap.SearchScope;
import org.forgerock.opendj.ldap.responses.SearchResultEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class LdapQueryProbe extends ProbeMethod<LdapConfiguration> {
    private static final Logger LOGGER = LoggerFactory.getLogger(LdapQueryProbe.class);

    private static final String LDAP_QUERY_ID = "query";

    private static final String DESCRIPTION =
            "Probe to execute arbitrary query against an LDAP server and return the results.";

    private static final int MAX_QUERY_RESULTS = 50;

    private static final List<String> REQUIRED_FIELDS = ImmutableList.of(LDAP_TYPE,
            HOST_NAME,
            PORT,
            ENCRYPTION_METHOD,
            BIND_USER,
            BIND_USER_PASSWORD,
            BIND_METHOD,
            QUERY,
            QUERY_BASE);

    private static final List<String> OPTIONAL_FIELDS = ImmutableList.of(BIND_REALM);

    private static final String LDAP_QUERY_RESULTS = "ldapQueryResults";

    private static final List<String> RETURN_TYPES = ImmutableList.of(LDAP_QUERY_RESULTS);

    private static final Map<String, String> SUCCESS_TYPES = ImmutableMap.of(SUCCESSFUL_PROBE,
            "Successfully executed LDAP query.");

    private static final Map<String, String> FAILURE_TYPES = toDescriptionMap(Arrays.asList(
            CANNOT_CONFIGURE,
            CANNOT_CONNECT,
            CANNOT_BIND));

    private final LdapTestingCommons ldapTestingCommons;

    public LdapQueryProbe(LdapTestingCommons ldapTestingCommons) {
        super(LDAP_QUERY_ID,
                DESCRIPTION,
                REQUIRED_FIELDS,
                OPTIONAL_FIELDS,
                SUCCESS_TYPES,
                null,
                null,
                RETURN_TYPES);
        this.ldapTestingCommons = ldapTestingCommons;
    }

    @Override
    public ProbeReport probe(LdapConfiguration configuration) {
        ProbeReport probeReport = new ProbeReport();
        List<Map<String, String>> convertedSearchResults = new ArrayList<>();
        try (LdapTestingCommons.LdapConnectionAttempt connectionAttempt =
                ldapTestingCommons.bindUserToLdapConnection(configuration)) {

            if (connectionAttempt.result() != SUCCESSFUL_BIND) {
                return createProbeReport(SUCCESS_TYPES,
                        FAILURE_TYPES,
                        null,
                        Collections.singletonList(connectionAttempt.result()
                                .name()));
            }

            List<SearchResultEntry> searchResults = ldapTestingCommons.getLdapQueryResults(
                    connectionAttempt.connection(),
                    configuration.queryBase(),
                    configuration.query(),
                    SearchScope.WHOLE_SUBTREE,
                    MAX_QUERY_RESULTS);

            for (SearchResultEntry entry : searchResults) {
                Map<String, String> entryMap = new HashMap<>();
                for (Attribute attri : entry.getAllAttributes()) {
                    entryMap.put("name",
                            entry.getName()
                                    .toString());
                    if (!attri.getAttributeDescriptionAsString()
                            .toLowerCase()
                            .contains("password")) {
                        List<String> attributeValueList = attri.parallelStream()
                                .map(ByteString::toString)
                                .collect(Collectors.toList());
                        String attributeValue = attributeValueList.size() == 1 ? attributeValueList.get(
                                0) : attributeValueList.toString();
                        entryMap.put(attri.getAttributeDescriptionAsString(), attributeValue);
                    }
                }
                convertedSearchResults.add(entryMap);
            }
        } catch (IOException e) {
            LOGGER.debug("Unexpected error closing connection", e);
        }


        return probeReport.probeResult(LDAP_QUERY_RESULTS, convertedSearchResults);
    }

    @Override
    public List<ConfigurationMessage> validateOptionalFields(LdapConfiguration configuration) {
        return validateBindRealm(configuration);
    }
}
