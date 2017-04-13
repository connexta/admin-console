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
import static org.codice.ddf.admin.api.config.ldap.LdapConfiguration.BIND_METHOD;
import static org.codice.ddf.admin.api.config.ldap.LdapConfiguration.BIND_REALM;
import static org.codice.ddf.admin.api.config.ldap.LdapConfiguration.BIND_USER;
import static org.codice.ddf.admin.api.config.ldap.LdapConfiguration.BIND_USER_PASSWORD;
import static org.codice.ddf.admin.api.config.ldap.LdapConfiguration.ENCRYPTION_METHOD;
import static org.codice.ddf.admin.api.config.ldap.LdapConfiguration.GROUP_ATTRIBUTE_HOLDING_MEMBER;
import static org.codice.ddf.admin.api.config.ldap.LdapConfiguration.HOST_NAME;
import static org.codice.ddf.admin.api.config.ldap.LdapConfiguration.LDAP_TYPE;
import static org.codice.ddf.admin.api.config.ldap.LdapConfiguration.MEMBER_ATTRIBUTE_REFERENCED_IN_GROUP;
import static org.codice.ddf.admin.api.config.ldap.LdapConfiguration.PORT;
import static org.codice.ddf.admin.api.services.PolicyManagerServiceProperties.STS_CLAIMS_CONFIGURATION_CONFIG_ID;
import static org.codice.ddf.admin.api.services.PolicyManagerServiceProperties.STS_CLAIMS_PROPS_KEY_CLAIMS;
import static org.codice.ddf.admin.api.validation.LdapValidationUtils.validateBindRealm;
import static org.codice.ddf.admin.security.ldap.LdapConnectionResult.SUCCESSFUL_BIND;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.codice.ddf.admin.api.config.ldap.LdapConfiguration;
import org.codice.ddf.admin.api.handler.ConfigurationMessage;
import org.codice.ddf.admin.api.handler.method.ProbeMethod;
import org.codice.ddf.admin.api.handler.report.ProbeReport;
import org.codice.ddf.admin.configurator.Configurator;
import org.codice.ddf.admin.security.ldap.ServerGuesser;
import org.codice.ddf.admin.security.ldap.test.LdapTestingCommons;
import org.forgerock.opendj.ldap.LdapException;
import org.forgerock.opendj.ldap.SearchResultReferenceIOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

public class SubjectAttributeProbe extends ProbeMethod<LdapConfiguration> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SubjectAttributeProbe.class);

    private static final String SUBJECT_ATTRIBUTES_PROBE_ID = "subject-attributes";

    private static final String DESCRIPTION =
            "Searches for the subject attributes for claims mapping.";

    private static final List<String> REQUIRED_FIELDS = ImmutableList.of(LDAP_TYPE,
            HOST_NAME,
            PORT,
            ENCRYPTION_METHOD,
            BIND_USER,
            BIND_USER_PASSWORD,
            BIND_METHOD,
            BASE_GROUP_DN,
            GROUP_ATTRIBUTE_HOLDING_MEMBER,
            MEMBER_ATTRIBUTE_REFERENCED_IN_GROUP);

    private static final List<String> OPTIONAL_FIELDS = ImmutableList.of(BIND_REALM);

    private static final String SUBJECT_CLAIMS = "subjectClaims";

    private static final String USER_ATTRIBUTES = "userAttributes";

    private static final List<String> RETURN_TYPES = ImmutableList.of(SUBJECT_CLAIMS,
            USER_ATTRIBUTES);

    private final LdapTestingCommons ldapTestingCommons;

    private final Configurator configurator;

    public SubjectAttributeProbe(LdapTestingCommons ldapTestingCommons, Configurator configurator) {
        super(SUBJECT_ATTRIBUTES_PROBE_ID,
                DESCRIPTION,
                REQUIRED_FIELDS,
                OPTIONAL_FIELDS,
                null,
                null,
                null,
                RETURN_TYPES);

        this.ldapTestingCommons = ldapTestingCommons;
        this.configurator = configurator;
    }

    @Override
    public ProbeReport probe(LdapConfiguration configuration) {
        ProbeReport probeReport = new ProbeReport();
        Object subjectClaims = configurator.getConfig(STS_CLAIMS_CONFIGURATION_CONFIG_ID)
                .get(STS_CLAIMS_PROPS_KEY_CLAIMS);

        Set<String> ldapEntryAttributes = null;
        try (LdapTestingCommons.LdapConnectionAttempt ldapConnectionAttempt =
                ldapTestingCommons.bindUserToLdapConnection(configuration)) {
            try {
                if (ldapConnectionAttempt.result() == SUCCESSFUL_BIND) {
                    ServerGuesser serverGuesser = ServerGuesser.buildGuesser(configuration.ldapType(),
                            ldapConnectionAttempt.connection());
                    ldapEntryAttributes =
                            serverGuesser.getClaimAttributeOptions(configuration.baseUserDn());
                } else {
                    LOGGER.warn("Error binding to LDAP server with config: {}",
                            configuration.toString());
                }
            } catch (SearchResultReferenceIOException | LdapException e) {
                LOGGER.warn("Error retrieving attributes from LDAP server; this may indicate a "
                                + "configuration issue with {}: {}, {}: {}, or {}: {}",
                        LdapConfiguration.BASE_GROUP_DN,
                        configuration.baseGroupDn(),
                        LdapConfiguration.GROUP_ATTRIBUTE_HOLDING_MEMBER,
                        configuration.groupAttributeHoldingMember(),
                        LdapConfiguration.MEMBER_ATTRIBUTE_REFERENCED_IN_GROUP,
                        configuration.memberAttributeReferencedInGroup());
            }

        } catch (IOException e) {
            LOGGER.debug("Unexpected error closing connection", e);
        }

        return probeReport.probeResult(SUBJECT_CLAIMS, subjectClaims)
                .probeResult(USER_ATTRIBUTES, ldapEntryAttributes);
    }

    @Override
    public List<ConfigurationMessage> validateOptionalFields(LdapConfiguration configuration) {
        return validateBindRealm(configuration);
    }
}
