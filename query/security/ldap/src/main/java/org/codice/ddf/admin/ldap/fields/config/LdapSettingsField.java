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
package org.codice.ddf.admin.ldap.fields.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codice.ddf.admin.api.fields.Field;
import org.codice.ddf.admin.api.fields.ListField;
import org.codice.ddf.admin.common.fields.base.BaseObjectField;
import org.codice.ddf.admin.common.fields.base.ListFieldImpl;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;
import org.codice.ddf.admin.ldap.fields.LdapDistinguishedName;

import com.google.common.collect.ImmutableList;

public class LdapSettingsField extends BaseObjectField {

    public static final String DEFAULT_FIELD_NAME = "settings";

    public static final String FIELD_TYPE_NAME = "LdapSettings";

    public static final String DESCRIPTION =
            "Contains information about the LDAP structure and various attributes required to setup.";

    private StringField usernameAttribute;

    private LdapDistinguishedName baseUserDn;

    private LdapDistinguishedName baseGroupDn;

    private StringField groupObjectClass;

    private StringField groupMembershipAttribute;

    private StringField groupAttributeHoldingMember;

    private StringField memberAttributeReferencedInGroup;

    private ListField<LdapAttributeEntryField> attributeMap;

    private LdapUseCase useCase;

    public LdapSettingsField() {
        super(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
    }

    @Override
    public List<Field> getFields() {
        return ImmutableList.of(usernameAttribute, baseUserDn, baseGroupDn,
                groupObjectClass,
                groupMembershipAttribute,
                groupAttributeHoldingMember,
                memberAttributeReferencedInGroup,
                attributeMap,
                useCase);
    }

    //Value getters
    public String baseUserDn() {
        return baseUserDn.getValue();
    }

    public String baseGroupDn() {
        return baseGroupDn.getValue();
    }

    public String groupObjectClass() {
        return groupObjectClass.getValue();
    }

    public String groupMembershipAttribute() {
        return groupMembershipAttribute.getValue();
    }

    public String usernameAttribute() {
        return usernameAttribute.getValue();
    }

    public String groupAttributeHoldingMember() {
        return groupAttributeHoldingMember.getValue();
    }

    public String memberAttributeReferencedInGroup() {
        return memberAttributeReferencedInGroup.getValue();
    }

    public Map<String, String> attributeMap() {
        Map<String, String> attributes = new HashMap<>();
        attributeMap.getList()
                .stream()
                .forEach(entry -> attributes.put(entry.stsClaim(), entry.userAttribute()));
        return attributes;
    }

    public String useCase() {
        return useCase.getValue();
    }

    //Field getters
    public StringField usernameAttributeField() {
        return usernameAttribute;
    }

    public LdapDistinguishedName baseUserDnField() {
        return baseUserDn;
    }

    public LdapDistinguishedName baseGroupDnField() {
        return baseGroupDn;
    }

    public StringField groupObjectClassField() {
        return groupObjectClass;
    }

    public StringField groupMembershipAttributeField() {
        return groupMembershipAttribute;
    }

    public StringField groupAttributeHoldingMemberField() {
        return groupAttributeHoldingMember;
    }

    public StringField memberAttributeReferencedInGroupField() {
        return memberAttributeReferencedInGroup;
    }

    public ListField<LdapAttributeEntryField> attributeMapField() {
        return attributeMap;
    }

    //Value setters
    public LdapSettingsField baseUserDn(String baseUserDn) {
        this.baseUserDn.setValue(baseUserDn);
        return this;
    }

    public LdapSettingsField baseGroupDn(String baseGroupDn) {
        this.baseGroupDn.setValue(baseGroupDn);
        return this;
    }

    public LdapSettingsField groupObjectClass(String groupObjectCLass) {
        this.groupObjectClass.setValue(groupObjectCLass);
        return this;
    }

    public LdapSettingsField groupMembershipAttribute(String groupMembershipAttribute) {
        this.groupMembershipAttribute.setValue(groupMembershipAttribute);
        return this;
    }

    public LdapSettingsField usernameAttribute(String usernameAttribute) {
        this.usernameAttribute.setValue(usernameAttribute);
        return this;
    }

    public LdapSettingsField mappingEntry(String claim, String attribute) {
        attributeMap.add(new LdapAttributeEntryField().stsClaim(claim)
                .userAttribute(attribute));
        return this;
    }

    public LdapSettingsField groupAttributeHoldingMember(String groupAttributeHoldingMember) {
        this.groupAttributeHoldingMember.setValue(groupAttributeHoldingMember);
        return this;
    }

    public LdapSettingsField memberAttributeReferencedInGroup(String memberAttributeReferencedInGroup) {
        this.memberAttributeReferencedInGroup.setValue(memberAttributeReferencedInGroup);
        return this;
    }

    public LdapSettingsField attributeMapField(Map<String, String> mapping) {
        mapping.entrySet()
                .stream()
                .forEach(entry -> attributeMap.add(new LdapAttributeEntryField().stsClaim(entry.getKey())
                        .userAttribute(entry.getValue())));
        return this;
    }

    public LdapSettingsField useCase(String useCase) {
        this.useCase.setValue(useCase);
        return this;
    }

    @Override
    public void initializeFields() {
        this.usernameAttribute = new StringField("userNameAttribute");
        this.baseUserDn = new LdapDistinguishedName("baseUserDn");
        this.baseGroupDn = new LdapDistinguishedName("baseGroupDn");
        this.groupObjectClass = new StringField("groupObjectClass");
        this.groupMembershipAttribute = new StringField("groupMembershipAttribute");
        this.groupAttributeHoldingMember = new StringField("groupAttributeHoldingMember");
        this.memberAttributeReferencedInGroup = new StringField("memberAttributeReferencedInGroup");
        this.attributeMap = new ListFieldImpl<>("attributeMapping", LdapAttributeEntryField.class);
        this.useCase = new LdapUseCase();
    }
}
