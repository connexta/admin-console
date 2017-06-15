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

import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.api.fields.ListField;
import org.codice.ddf.admin.common.fields.base.BaseObjectField;
import org.codice.ddf.admin.common.fields.base.ListFieldImpl;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;
import org.codice.ddf.admin.ldap.fields.LdapDistinguishedName;

import com.google.common.collect.ImmutableList;

public class LdapRecommendedSettingsField extends BaseObjectField {

    public static final String DEFAULT_FIELD_NAME = "recommendedSettings";

    public static final String FIELD_TYPE_NAME = "LdapRecommendedSettings";

    public static final String DESCRIPTION =
            "An object containing potential values to be used for setting up LDAP.";

    public static final String USER_DNS = "userDns";

    public static final String GROUPS_DNS = "groupsDns";

    public static final String USER_NAME_ATTRIBUTES = "userNameAttributes";

    public static final String GROUP_OBJECT_CLASSES = "groupObjectClasses";

    public static final String GROUP_ATTRIBUTES_HOLDING_MEMBER =
            "groupAttributesHoldingMember";

    public static final String MEMBER_ATTRIBUTES_REFERENCED_IN_GROUP =
            "memberAttributesReferencedInGroup";

    public static final String QUERY_BASES = "queryBases";

    private ListField<LdapDistinguishedName> userDns;

    private ListField<LdapDistinguishedName> groupDns;

    private ListField<StringField> userNameAttributes;

    private ListField<StringField> groupObjectClasses;

    private ListField<StringField> groupAttributesHoldingMember;

    private ListField<StringField> memberAttributesReferencedInGroup;

    private ListField<LdapDistinguishedName> queryBases;

    public LdapRecommendedSettingsField() {
        super(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
        userDns = new ListFieldImpl<>(USER_DNS, LdapDistinguishedName.class);
        groupDns = new ListFieldImpl<>(GROUPS_DNS, LdapDistinguishedName.class);
        userNameAttributes = new ListFieldImpl<>(USER_NAME_ATTRIBUTES, StringField.class);
        groupObjectClasses = new ListFieldImpl<>(GROUP_OBJECT_CLASSES, StringField.class);
        groupAttributesHoldingMember = new ListFieldImpl<>(GROUP_ATTRIBUTES_HOLDING_MEMBER,
                StringField.class);
        memberAttributesReferencedInGroup = new ListFieldImpl<>(
                MEMBER_ATTRIBUTES_REFERENCED_IN_GROUP,
                StringField.class);
        queryBases = new ListFieldImpl<>(QUERY_BASES, LdapDistinguishedName.class);
        updateInnerFieldPaths();
    }

    public LdapRecommendedSettingsField userDns(List<String> baseDns) {
        this.userDns.setValue(baseDns);
        return this;
    }

    public LdapRecommendedSettingsField groupDns(List<String> groupDns) {
        this.groupDns.setValue(groupDns);
        return this;
    }

    public LdapRecommendedSettingsField userNameAttributes(List<String> userNameAttributes) {
        this.userNameAttributes.setValue(userNameAttributes);
        return this;
    }

    public LdapRecommendedSettingsField groupObjectClasses(List<String> groupObjectClasses) {
        this.groupObjectClasses.setValue(groupObjectClasses);
        return this;
    }

    public LdapRecommendedSettingsField groupAttributesHoldingMember(
            List<String> groupAttributesHoldingMember) {
        this.groupAttributesHoldingMember.setValue(groupAttributesHoldingMember);
        return this;
    }

    public LdapRecommendedSettingsField memberAttributesReferencedInGroup(
            List<String> memberAttributesReferencedInGroup) {
        this.memberAttributesReferencedInGroup.setValue(memberAttributesReferencedInGroup);
        return this;
    }

    public LdapRecommendedSettingsField queryBases(List<String> queryBases) {
        this.queryBases.setValue(queryBases);
        return this;
    }

    public ListField<LdapDistinguishedName> userDnsField() {
        return userDns;
    }

    public ListField<LdapDistinguishedName> groupDnsField() {
        return groupDns;
    }

    public ListField<StringField> userNameAttributesField() {
        return userNameAttributes;
    }

    public ListField<StringField> groupObjectClassesField() {
        return groupObjectClasses;
    }

    public ListField<StringField> groupAttributesHoldingMemberField() {
        return groupAttributesHoldingMember;
    }

    public ListField<StringField> memberAttributesReferencedInGroupField() {
        return memberAttributesReferencedInGroup;
    }

    public ListField<LdapDistinguishedName> queryBasesField() {
        return queryBases;
    }

    @Override
    public List<Field> getFields() {
        return ImmutableList.of(userDns,
                groupDns,
                userNameAttributes,
                groupObjectClasses,
                groupAttributesHoldingMember,
                memberAttributesReferencedInGroup,
                queryBases);
    }
}
