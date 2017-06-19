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

import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.api.fields.ListField;
import org.codice.ddf.admin.common.fields.base.BaseObjectField;
import org.codice.ddf.admin.common.fields.base.ListFieldImpl;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;
import org.codice.ddf.admin.ldap.fields.LdapDistinguishedName;
import org.codice.ddf.admin.security.common.fields.wcpm.ClaimsMapEntry;

import com.google.common.collect.ImmutableList;

public class LdapSettingsField extends BaseObjectField {
    public static final String DEFAULT_FIELD_NAME = "settings";

    public static final String FIELD_TYPE_NAME = "LdapSettings";

    public static final String DESCRIPTION =
            "Contains information about the LDAP structure and various attributes required to setup.";

    public static final String USER_NAME_ATTRIBUTE = "userNameAttribute";

    public static final String BASE_USER_DN = "baseUserDn";

    public static final String BASE_GROUP_DN = "baseGroupDn";

    public static final String GROUP_OBJECT_CLASS = "groupObjectClass";

    public static final String GROUP_ATTRIBUTE_HOLDING_MEMBER = "groupAttributeHoldingMember";

    public static final String MEMBER_ATTRIBUTE_REFERENCED_IN_GROUP = "memberAttributeReferencedInGroup";

    public static final String ATTRIBUTE_MAPPING = "attributeMapping";

    private StringField usernameAttribute;

    private LdapDistinguishedName baseUserDn;

    private LdapDistinguishedName baseGroupDn;

    private StringField groupObjectClass;

    private StringField groupAttributeHoldingMember;

    private StringField memberAttributeReferencedInGroup;

    private ListField<ClaimsMapEntry> attributeMap;

    private LdapUseCase useCase;

    public LdapSettingsField() {
        super(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);

        this.usernameAttribute = new StringField(USER_NAME_ATTRIBUTE);
        this.baseUserDn = new LdapDistinguishedName(BASE_USER_DN);
        this.baseGroupDn = new LdapDistinguishedName(BASE_GROUP_DN);
        this.groupObjectClass = new StringField(GROUP_OBJECT_CLASS);
        this.groupAttributeHoldingMember = new StringField(GROUP_ATTRIBUTE_HOLDING_MEMBER);
        this.memberAttributeReferencedInGroup = new StringField(MEMBER_ATTRIBUTE_REFERENCED_IN_GROUP);
        this.attributeMap = new ListFieldImpl<>(ATTRIBUTE_MAPPING, ClaimsMapEntry.class);
        this.useCase = new LdapUseCase();

        updateInnerFieldPaths();
    }

    @Override
    public List<Field> getFields() {
        return ImmutableList.of(usernameAttribute,
                baseUserDn,
                baseGroupDn,
                groupObjectClass,
                groupAttributeHoldingMember,
                memberAttributeReferencedInGroup,
                attributeMap,
                useCase);
    }

    //Field getters
    public StringField usernameAttributeField() {
        return usernameAttribute;
    }

    public LdapUseCase useCaseField() {
        return useCase;
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

    public StringField groupAttributeHoldingMemberField() {
        return groupAttributeHoldingMember;
    }

    public StringField memberAttributeReferencedInGroupField() {
        return memberAttributeReferencedInGroup;
    }

    public ListField<ClaimsMapEntry> attributeMapField() {
        return attributeMap;
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

    public String usernameAttribute() {
        return usernameAttribute.getValue();
    }

    public String groupAttributeHoldingMember() {
        return groupAttributeHoldingMember.getValue();
    }

    public String memberAttributeReferencedInGroup() {
        return memberAttributeReferencedInGroup.getValue();
    }

    public LdapSettingsField useDefaultAuthentication() {
        useCase.isRequired(true);
        baseUserDn.isRequired(true);
        baseGroupDn.isRequired(true);
        usernameAttribute.isRequired(true);
        isRequired(true);
        return this;
    }

    public LdapSettingsField useDefaultAttributeStore() {
        useDefaultAuthentication();
        groupObjectClass.isRequired(true);
        groupAttributeHoldingMember.isRequired(true);
        memberAttributeReferencedInGroup.isRequired(true);
        return this;
    }

    public LdapSettingsField useDefaultUserAttributes() {
        useDefaultAttributeStore();
        attributeMap.isRequired(true);
        return this;
    }

    public Map<String, String> attributeMap() {
        Map<String, String> attributes = new HashMap<>();
        attributeMap.getList()
                .stream()
                .forEach(entry -> attributes.put(entry.key(), entry.value()));
        return attributes;
    }

    public String useCase() {
        return useCase.getValue();
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

    public LdapSettingsField usernameAttribute(String usernameAttribute) {
        this.usernameAttribute.setValue(usernameAttribute);
        return this;
    }

    public LdapSettingsField mappingEntry(String claim, String attribute) {
        attributeMap.add(new ClaimsMapEntry().key(claim)
                .value(attribute));
        return this;
    }

    public LdapSettingsField groupAttributeHoldingMember(String groupAttributeHoldingMember) {
        this.groupAttributeHoldingMember.setValue(groupAttributeHoldingMember);
        return this;
    }

    public LdapSettingsField memberAttributeReferencedInGroup(
            String memberAttributeReferencedInGroup) {
        this.memberAttributeReferencedInGroup.setValue(memberAttributeReferencedInGroup);
        return this;
    }

    public LdapSettingsField attributeMapField(Map<String, String> mapping) {
        mapping.entrySet()
                .stream()
                .forEach(entry -> attributeMap.add(new ClaimsMapEntry().key(entry.getKey())
                        .value(entry.getValue())));
        return this;
    }

    public LdapSettingsField useCase(String useCase) {
        this.useCase.setValue(useCase);
        return this;
    }
}
