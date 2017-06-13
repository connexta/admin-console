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

import java.util.List;

import org.codice.ddf.admin.api.DataType;
import org.codice.ddf.admin.api.fields.FunctionField;
import org.codice.ddf.admin.common.fields.base.BaseFunctionField;
import org.codice.ddf.admin.ldap.commons.LdapConnectionAttempt;
import org.codice.ddf.admin.ldap.commons.LdapTestingUtils;
import org.codice.ddf.admin.ldap.commons.ServerGuesser;
import org.codice.ddf.admin.ldap.fields.connection.LdapBindUserInfo;
import org.codice.ddf.admin.ldap.fields.connection.LdapConnectionField;
import org.codice.ddf.admin.ldap.fields.query.LdapRecommendedSettingsField;
import org.codice.ddf.admin.ldap.fields.query.LdapTypeField;

import com.google.common.collect.ImmutableList;

public class LdapRecommendedSettings extends BaseFunctionField<LdapRecommendedSettingsField> {
    public static final String ID = "recommendedSettings";

    public static final String DESCRIPTION =
            "Attempts to retrieve recommended settings from the LDAP connection.";

    private LdapConnectionField conn;

    private LdapBindUserInfo creds;

    private LdapTypeField ldapType;

    private LdapTestingUtils utils;

    public LdapRecommendedSettings() {
        super(ID, DESCRIPTION, new LdapRecommendedSettingsField());
        conn = new LdapConnectionField().useDefaultRequired();
        creds = new LdapBindUserInfo().useDefaultRequired();
        ldapType = new LdapTypeField();
        updateArgumentPaths();

        utils = new LdapTestingUtils();
    }

    @Override
    public List<DataType> getArguments() {
        return ImmutableList.of(conn, creds, ldapType);
    }

    @Override
    public LdapRecommendedSettingsField performFunction() {
        LdapConnectionAttempt connectionAttempt = utils.bindUserToLdapConnection(conn, creds);
        addResultMessages(connectionAttempt.messages());
        addArgumentMessages(connectionAttempt.argumentMessages());
        if (!connectionAttempt.connection()
                .isPresent()) {
            return null;
        }

        ServerGuesser guesser = ServerGuesser.buildGuesser(ldapType.getValue(),
                connectionAttempt.connection()
                        .get());

        return new LdapRecommendedSettingsField().userDns(guesser.getUserBaseChoices())
                .groupDns(guesser.getGroupBaseChoices())
                .userNameAttributes(guesser.getUserNameAttribute())
                .groupObjectClasses(guesser.getGroupObjectClass())
                .groupAttributesHoldingMember(guesser.getGroupAttributeHoldingMember())
                .memberAttributesReferencedInGroup(guesser.getMemberAttributeReferencedInGroup())
                .queryBases(guesser.getBaseContexts());
    }

    @Override
    public FunctionField<LdapRecommendedSettingsField> newInstance() {
        return new LdapRecommendedSettings();
    }

    public void setTestingUtils(LdapTestingUtils utils) {
        this.utils = utils;
    }
}
