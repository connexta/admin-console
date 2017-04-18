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

import java.util.List;

import org.codice.ddf.admin.api.fields.Field;
import org.codice.ddf.admin.common.actions.BaseAction;
import org.codice.ddf.admin.ldap.actions.commons.LdapConnectionAttempt;
import org.codice.ddf.admin.ldap.actions.commons.LdapTestingUtils;
import org.codice.ddf.admin.ldap.actions.commons.ServerGuesser;
import org.codice.ddf.admin.ldap.fields.connection.LdapBindUserInfo;
import org.codice.ddf.admin.ldap.fields.connection.LdapConnectionField;
import org.codice.ddf.admin.ldap.fields.query.LdapRecommendedSettingsField;
import org.codice.ddf.admin.ldap.fields.query.LdapTypeField;

import com.google.common.collect.ImmutableList;

public class LdapRecommendedSettings extends BaseAction<LdapRecommendedSettingsField> {

    public static final String NAME = "recommendedSettings";

    public static final String DESCRIPTION =
            "Attempts to retrieve recommended settings from the LDAP connection.";

    private LdapConnectionField conn;

    private LdapBindUserInfo creds;

    private LdapTypeField ldapType;

    private LdapTestingUtils utils;

    public LdapRecommendedSettings() {
        super(NAME, DESCRIPTION, new LdapRecommendedSettingsField());
        conn = new LdapConnectionField();
        creds = new LdapBindUserInfo();
        ldapType = new LdapTypeField();
        utils = new LdapTestingUtils();
    }

    @Override
    public List<Field> getArguments() {
        return ImmutableList.of(conn, creds, ldapType);
    }

    @Override
    public LdapRecommendedSettingsField performAction() {
        LdapConnectionAttempt connectionAttempt = utils.bindUserToLdapConnection(conn, creds);
        addMessages(connectionAttempt.messages());
        if(!connectionAttempt.connection().isPresent()) {
            return null;
        }

        ServerGuesser guesser = ServerGuesser.buildGuesser(ldapType.getValue(),
                connectionAttempt.connection()
                        .get());

        return new LdapRecommendedSettingsField()
                .userDns(guesser.getUserBaseChoices())
                .groupDns(guesser.getGroupBaseChoices())
                .userNameAttributes(guesser.getUserNameAttribute())
                .groupObjectClasses(guesser.getGroupObjectClass())
                .groupAttributesHoldingMember(guesser.getGroupAttributeHoldingMember())
                .memberAttributesReferencedInGroup(guesser.getMemberAttributeReferencedInGroup())
                .queryBases(guesser.getBaseContexts());
    }
}
