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
import org.codice.ddf.admin.api.fields.ListField;
import org.codice.ddf.admin.common.fields.base.BaseObjectField;
import org.codice.ddf.admin.common.fields.base.ListFieldImpl;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;
import org.codice.ddf.admin.ldap.fields.LdapDistinguishedName;

import com.google.common.collect.ImmutableList;

public class LdapRecommendedSettingsField extends BaseObjectField {

    public static final String DEFAULT_FIELD_NAME = "recommendedSettings";

    public static final String FIELD_TYPE_NAME = "LdapRecommendedSettings";

    public static final String DESCRIPTION = "An object containing potential values to be used for setting up LDAP.";

    private ListField<LdapDistinguishedName> userDns;
    private ListField<LdapDistinguishedName> groupDns;
    private ListField<StringField> userNameAttributes;
    private ListField<StringField> groupObjectClasses;
    private ListField<StringField> groupAttributesHoldingMember;
    private ListField<StringField> memberAttributesReferencedInGroup;
    private ListField<LdapDistinguishedName> queryBases;

    public LdapRecommendedSettingsField() {
        super(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
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

    public LdapRecommendedSettingsField groupAttributesHoldingMember(List<String> groupAttributesHoldingMember) {
        this.groupAttributesHoldingMember.setValue(groupAttributesHoldingMember);
        return this;

    }

    public LdapRecommendedSettingsField memberAttributesReferencedInGroup(List<String> memberAttributesReferencedInGroup) {
        this.memberAttributesReferencedInGroup.setValue(memberAttributesReferencedInGroup);
        return this;

    }

    public LdapRecommendedSettingsField queryBases(List<String> queryBases) {
        this.queryBases.setValue(queryBases);
        return this;

    }

    @Override
    public List<Field> getFields() {
        return ImmutableList.of(userDns, groupDns, userNameAttributes, groupObjectClasses, groupAttributesHoldingMember, memberAttributesReferencedInGroup,
                queryBases);
    }

    @Override
    public void initializeFields() {
        userDns = new ListFieldImpl<>("userDns", LdapDistinguishedName.class);
        groupDns = new ListFieldImpl<>("groupsDns", LdapDistinguishedName.class);
        userNameAttributes = new ListFieldImpl<>("userNameAttributes", StringField.class);
        groupObjectClasses = new ListFieldImpl<>("groupObjectClasses", StringField.class);
        groupAttributesHoldingMember = new ListFieldImpl<>("groupAttributesHoldingMember", StringField.class);
        memberAttributesReferencedInGroup =  new ListFieldImpl<>("memberAttributesReferencedInGroup", StringField.class);
        queryBases = new ListFieldImpl<>("queryBases", LdapDistinguishedName.class);
    }
}
