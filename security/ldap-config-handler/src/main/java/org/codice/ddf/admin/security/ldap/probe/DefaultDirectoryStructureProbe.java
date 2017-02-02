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

import static org.codice.ddf.admin.api.config.ldap.LdapConfiguration.BASE_GROUP_DN;
import static org.codice.ddf.admin.api.config.ldap.LdapConfiguration.BASE_USER_DN;
import static org.codice.ddf.admin.api.config.ldap.LdapConfiguration.BIND_METHOD;
import static org.codice.ddf.admin.api.config.ldap.LdapConfiguration.BIND_REALM;
import static org.codice.ddf.admin.api.config.ldap.LdapConfiguration.BIND_USER;
import static org.codice.ddf.admin.api.config.ldap.LdapConfiguration.BIND_USER_PASSWORD;
import static org.codice.ddf.admin.api.config.ldap.LdapConfiguration.ENCRYPTION_METHOD;
import static org.codice.ddf.admin.api.config.ldap.LdapConfiguration.GROUP_ATTRIBUTE_HOLDING_MEMBER;
import static org.codice.ddf.admin.api.config.ldap.LdapConfiguration.GROUP_OBJECT_CLASS;
import static org.codice.ddf.admin.api.config.ldap.LdapConfiguration.HOST_NAME;
import static org.codice.ddf.admin.api.config.ldap.LdapConfiguration.LDAP_TYPE;
import static org.codice.ddf.admin.api.config.ldap.LdapConfiguration.MEMBER_ATTRIBUTE_REFERENCED_IN_GROUP;
import static org.codice.ddf.admin.api.config.ldap.LdapConfiguration.PORT;
import static org.codice.ddf.admin.api.config.ldap.LdapConfiguration.QUERY;
import static org.codice.ddf.admin.api.config.ldap.LdapConfiguration.QUERY_BASE;
import static org.codice.ddf.admin.api.config.ldap.LdapConfiguration.USER_NAME_ATTRIBUTE;
import static org.codice.ddf.admin.api.handler.commons.HandlerCommons.SUCCESSFUL_PROBE;
import static org.codice.ddf.admin.api.handler.report.ProbeReport.createProbeReport;
import static org.codice.ddf.admin.api.validation.LdapValidationUtils.validateBindRealm;
import static org.codice.ddf.admin.security.ldap.LdapConnectionResult.CANNOT_BIND;
import static org.codice.ddf.admin.security.ldap.LdapConnectionResult.CANNOT_CONFIGURE;
import static org.codice.ddf.admin.security.ldap.LdapConnectionResult.CANNOT_CONNECT;
import static org.codice.ddf.admin.security.ldap.LdapConnectionResult.SUCCESSFUL_BIND;
import static org.codice.ddf.admin.security.ldap.LdapConnectionResult.toDescriptionMap;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codice.ddf.admin.api.config.ldap.LdapConfiguration;
import org.codice.ddf.admin.api.handler.ConfigurationMessage;
import org.codice.ddf.admin.api.handler.method.ProbeMethod;
import org.codice.ddf.admin.api.handler.report.ProbeReport;
import org.codice.ddf.admin.security.ldap.ServerGuesser;
import org.codice.ddf.admin.security.ldap.test.LdapTestingCommons;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class DefaultDirectoryStructureProbe extends ProbeMethod<LdapConfiguration> {

    private static final String ID = "dir-struct";

    private static final String DESCRIPTION =
            "Queries the bound LDAP server, attempting to find the user and group base DNs, the "
                    + "username attribute, the group membership attribute, and the objectClass "
                    + "representing groups.";

    private static final List<String> REQUIRED_FIELDS = ImmutableList.of(LDAP_TYPE,
            HOST_NAME,
            PORT,
            ENCRYPTION_METHOD,
            BIND_USER,
            BIND_USER_PASSWORD,
            BIND_METHOD);

    private static final Map<String, String> SUCCESS_TYPES = ImmutableMap.of(SUCCESSFUL_PROBE,
            "Successfully discovered recommended values");

    private static final List<String> OPTIONAL_FIELDS = ImmutableList.of(BIND_REALM);

    private static final Map<String, String> FAILURE_TYPES = toDescriptionMap(Arrays.asList(
            CANNOT_CONFIGURE,
            CANNOT_CONNECT,
            CANNOT_BIND));

    private static final List<String> RETURN_TYPES = ImmutableList.of(BASE_USER_DN,
            BASE_GROUP_DN,
            USER_NAME_ATTRIBUTE,
            GROUP_OBJECT_CLASS,
            GROUP_ATTRIBUTE_HOLDING_MEMBER,
            MEMBER_ATTRIBUTE_REFERENCED_IN_GROUP,
            QUERY,
            QUERY_BASE);

    private final LdapTestingCommons ldapTestingCommons;

    public DefaultDirectoryStructureProbe(LdapTestingCommons ldapTestingCommons) {
        super(ID,
                DESCRIPTION,
                REQUIRED_FIELDS,
                OPTIONAL_FIELDS,
                SUCCESS_TYPES,
                FAILURE_TYPES,
                null,
                RETURN_TYPES);
        this.ldapTestingCommons = ldapTestingCommons;
    }

    @Override
    public ProbeReport probe(LdapConfiguration configuration) {
        LdapTestingCommons.LdapConnectionAttempt connectionAttempt =
                ldapTestingCommons.bindUserToLdapConnection(configuration);
        if (connectionAttempt.result() != SUCCESSFUL_BIND) {
            return createProbeReport(SUCCESS_TYPES,
                    FAILURE_TYPES,
                    null,
                    connectionAttempt.result()
                            .name());
        }

        Map<String, Object> probeResult = new HashMap<>();
        String ldapType = configuration.ldapType();
        ServerGuesser guesser = ServerGuesser.buildGuesser(ldapType,
                connectionAttempt.connection());

        if (guesser != null) {
            probeResult.put(BASE_USER_DN, guesser.getUserBaseChoices());
            probeResult.put(BASE_GROUP_DN, guesser.getGroupBaseChoices());
            probeResult.put(USER_NAME_ATTRIBUTE, guesser.getUserNameAttribute());
            probeResult.put(GROUP_OBJECT_CLASS, guesser.getGroupObjectClass());
            probeResult.put(GROUP_ATTRIBUTE_HOLDING_MEMBER,
                    guesser.getGroupAttributeHoldingMember());
            probeResult.put(MEMBER_ATTRIBUTE_REFERENCED_IN_GROUP,
                    guesser.getMemberAttributeReferencedInGroup());
            // TODO RAP 13 Dec 16: Better query, perhaps driven by guessers?
            probeResult.put(QUERY, Collections.singletonList("objectClass=*"));
            probeResult.put(QUERY_BASE, guesser.getBaseContexts());
        }

        return createProbeReport(SUCCESS_TYPES, FAILURE_TYPES, null, SUCCESSFUL_PROBE).probeResults(
                probeResult);
    }

    @Override
    public List<ConfigurationMessage> validateOptionalFields(LdapConfiguration configuration) {
        return validateBindRealm(configuration);
    }
}
