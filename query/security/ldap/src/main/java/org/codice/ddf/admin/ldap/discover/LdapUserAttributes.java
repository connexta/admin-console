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
package org.codice.ddf.admin.ldap.discover;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.codice.ddf.admin.api.DataType;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.api.fields.ListField;
import org.codice.ddf.admin.common.fields.base.BaseFunctionField;
import org.codice.ddf.admin.common.fields.base.ListFieldImpl;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;
import org.codice.ddf.admin.ldap.commons.LdapConnectionAttempt;
import org.codice.ddf.admin.ldap.commons.LdapTestingUtils;
import org.codice.ddf.admin.ldap.commons.ServerGuesser;
import org.codice.ddf.admin.ldap.fields.config.LdapConfigurationField;
import org.codice.ddf.admin.ldap.fields.query.LdapTypeField;
import org.forgerock.opendj.ldap.LdapException;
import org.forgerock.opendj.ldap.SearchResultReferenceIOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

public class LdapUserAttributes extends BaseFunctionField<ListField<StringField>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LdapUserAttributes.class);

    public static final String ID = "userAttributes";

    public static final String DESCRIPTION =
            "Retrieves a subset of available user attributes based on the LDAP settings provided.";

    private LdapConfigurationField config;

    private LdapTypeField ldapType;

    private LdapTestingUtils utils;

    public LdapUserAttributes() {
        super(ID, DESCRIPTION, new ListFieldImpl<>(StringField.class));
        config = new LdapConfigurationField();
        ldapType = new LdapTypeField();
        updateArgumentPaths();

        utils = new LdapTestingUtils();
    }

    @Override
    public List<DataType> getArguments() {
        return ImmutableList.of(config, ldapType);
    }

    @Override
    public ListField<StringField> performFunction() {
        LdapConnectionAttempt ldapConnectionAttempt =
                utils.bindUserToLdapConnection(config.connectionField(),
                        config.bindUserInfoField());

        addMessages(ldapConnectionAttempt);

        if (!ldapConnectionAttempt.isResultPresent()) {
            return null;
        }

        Set<String> ldapEntryAttributes = null;
        try {
            ServerGuesser serverGuesser = ServerGuesser.buildGuesser(ldapType.getValue(),
                    ldapConnectionAttempt.result());
            ldapEntryAttributes = serverGuesser.getClaimAttributeOptions(config.settingsField()
                    .baseUserDn());

        } catch (SearchResultReferenceIOException | LdapException e) {
            // TODO: tbatie - 4/3/17 - Make a toString for LDAPConfig
            LOGGER.warn("Error retrieving attributes from LDAP server; this may indicate a "
                    + "configuration issue with config: ", config.toString());
        }

        // TODO: tbatie - 4/3/17 - Make a set field instead
        ListFieldImpl entries = new ListFieldImpl<>(StringField.class);
        entries.setValue(Arrays.asList(ldapEntryAttributes.toArray()));
        return entries;
    }

    @Override
    public FunctionField<ListField<StringField>> newInstance() {
        return new LdapUserAttributes();
    }

    public void setTestingUtils(LdapTestingUtils utils) {
        this.utils = utils;
    }
}
