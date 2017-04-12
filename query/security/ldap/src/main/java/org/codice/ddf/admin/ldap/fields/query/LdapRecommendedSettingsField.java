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
package org.codice.ddf.admin.ldap.fields.query;

import java.util.List;

import org.codice.ddf.admin.api.fields.Field;
import org.codice.ddf.admin.common.fields.base.BaseObjectField;
import org.codice.ddf.admin.common.fields.base.list.StringList;

import com.google.common.collect.ImmutableList;

public class LdapRecommendedSettingsField extends BaseObjectField {

    public static final String DEFAULT_FIELD_NAME = "recommendedSettings";

    public static final String FIELD_TYPE_NAME = "LdapRecommendedSettings";

    public static final String DESCRIPTION = "An object containing potential values to be used for setting up LDAP.";

    private StringList userDns;
    private StringList groupDns;
    private StringList userNameAttributes;
    private StringList groupObjectClasses;
    private StringList groupAttributesHoldingMember;
    private StringList memberAttributesReferencedInGroup;
    private StringList queryBases;

    public LdapRecommendedSettingsField() {
        super(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
        userDns = new StringList("userDns", "List of possible dn's that contains the users for LDAP.");
        groupDns = new StringList("groupsDns", "List of possible dn's that contains the groups for LDAP.");
        userNameAttributes = new StringList("userNameAttributes", "List of possible dn's that contains the users for LDAP.");
        groupObjectClasses = new StringList("groupObjectClasses", "List of group object classes that could be used as a setting for LDAP.");
        // TODO: tbatie - 4/3/17 - Add description to the next 2 feilds
        groupAttributesHoldingMember = new StringList("groupAttributesHoldingMember", "TODO: Add a description here");
        memberAttributesReferencedInGroup =  new StringList("memberAttributesReferencedInGroup", "TODO: Add a description here");
        queryBases = new StringList("queryBases", "Dn's containing useful information for discovery correct settings.");
    }

    public LdapRecommendedSettingsField userDns(List<String> baseDns) {
        this.userDns.setList(baseDns);
        return this;
    }

    public LdapRecommendedSettingsField groupDns(List<String> groupDns) {
        this.groupDns.setList(groupDns);
        return this;
    }

    public LdapRecommendedSettingsField userNameAttributes(List<String> userNameAttributes) {
        this.userNameAttributes.setList(userNameAttributes);
        return this;

    }

    public LdapRecommendedSettingsField groupObjectClasses(List<String> groupObjectClasses) {
        this.groupObjectClasses.setList(groupObjectClasses);
        return this;

    }

    public LdapRecommendedSettingsField groupAttributesHoldingMember(List<String> groupAttributesHoldingMember) {
        this.groupAttributesHoldingMember.setList(groupAttributesHoldingMember);
        return this;

    }

    public LdapRecommendedSettingsField memberAttributesReferencedInGroup(List<String> memberAttributesReferencedInGroup) {
        this.memberAttributesReferencedInGroup.setList(memberAttributesReferencedInGroup);
        return this;

    }

    public LdapRecommendedSettingsField queryBases(List<String> queryBases) {
        this.queryBases.setList(queryBases);
        return this;

    }

    @Override
    public List<Field> getFields() {
        return ImmutableList.of(userDns, groupDns, userNameAttributes, groupObjectClasses, groupAttributesHoldingMember, memberAttributesReferencedInGroup,
                queryBases);
    }
}
