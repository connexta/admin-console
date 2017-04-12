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
package org.codice.ddf.admin.ldap.actions.discover;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.codice.ddf.admin.api.fields.Field;
import org.codice.ddf.admin.common.actions.BaseAction;
import org.codice.ddf.admin.common.fields.base.list.StringList;
import org.codice.ddf.admin.ldap.actions.commons.LdapConnectionAttempt;
import org.codice.ddf.admin.ldap.actions.commons.LdapTestingUtils;
import org.codice.ddf.admin.ldap.actions.commons.ServerGuesser;
import org.codice.ddf.admin.ldap.fields.config.LdapConfigurationField;
import org.codice.ddf.admin.ldap.fields.query.LdapTypeField;
import org.forgerock.opendj.ldap.LdapException;
import org.forgerock.opendj.ldap.SearchResultReferenceIOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

public class LdapUserAttributes extends BaseAction<StringList> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LdapUserAttributes.class);

    public static final String NAME = "userAttributes";

    public static final String DESCRIPTION =
            "Retrieves a subset of available user attributes based on the LDAP settings provided.";

    private LdapConfigurationField config;

    private LdapTypeField ldapType;

    private LdapTestingUtils utils;

    public LdapUserAttributes() {
        super(NAME,
                DESCRIPTION,
                new StringList("attributes", "A list of attributes found on LDAP entries."));
        config = new LdapConfigurationField();
        ldapType = new LdapTypeField();
        utils = new LdapTestingUtils();
    }

    @Override
    public List<Field> getArguments() {
        return ImmutableList.of(config, ldapType);
    }

    @Override
    public StringList performAction() {

        LdapConnectionAttempt ldapConnectionAttempt =
                utils.bindUserToLdapConnection(config.connection(), config.bindUserInfo());
        addArgumentMessages(ldapConnectionAttempt.messages());

        if (!ldapConnectionAttempt.connection()
                .isPresent()) {
            // TODO: tbatie - 4/3/17 - Make a toString for LDAPConfig
            LOGGER.warn("Error binding to LDAP server with config: {}", config.toString());
            return null;
        }

        Set<String> ldapEntryAttributes = null;
        try {
            ServerGuesser serverGuesser = ServerGuesser.buildGuesser(ldapType.getValue(),
                    ldapConnectionAttempt.connection()
                            .get());
            ldapEntryAttributes = serverGuesser.getClaimAttributeOptions(config.settings()
                    .baseUserDn());

        } catch (SearchResultReferenceIOException | LdapException e) {
            // TODO: tbatie - 4/3/17 - Make a toString for LDAPConfig
            LOGGER.warn("Error retrieving attributes from LDAP server; this may indicate a "
                    + "configuration issue with config: ", config.toString());
        }

        // TODO: tbatie - 4/3/17 - Make a set field instead
        return new StringList().setList(new ArrayList<>(ldapEntryAttributes));
    }
}
