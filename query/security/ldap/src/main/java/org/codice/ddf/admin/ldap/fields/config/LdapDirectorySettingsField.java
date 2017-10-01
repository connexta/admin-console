/**
 * Copyright (c) Codice Foundation
 *
 * <p>This is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version.
 *
 * <p>This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details. A copy of the GNU Lesser General Public
 * License is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 */
package org.codice.ddf.admin.ldap.fields.config;

import com.google.common.collect.ImmutableList;
import java.util.List;
import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.common.fields.base.BaseObjectField;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;
import org.codice.ddf.admin.ldap.fields.LdapAttributeName;
import org.codice.ddf.admin.ldap.fields.LdapDistinguishedName;
import org.codice.ddf.admin.security.common.fields.ldap.LdapUseCase;

public class LdapDirectorySettingsField extends BaseObjectField {
  public static final String DEFAULT_FIELD_NAME = "directorySettings";

  public static final String FIELD_TYPE_NAME = "LdapDirectorySettings";

  public static final String DESCRIPTION =
      "Contains information about the LDAP structure and various attributes required to setup.";

  public static final String USER_NAME_ATTRIBUTE = "userNameAttribute";

  public static final String BASE_USER_DN = "baseUserDn";

  public static final String BASE_GROUP_DN = "baseGroupDn";

  public static final String GROUP_OBJECT_CLASS = "groupObjectClass";

  public static final String GROUP_ATTRIBUTE_HOLDING_MEMBER = "groupAttributeHoldingMember";

  public static final String MEMBER_ATTRIBUTE_REFERENCED_IN_GROUP =
      "memberAttributeReferencedInGroup";

  private LdapAttributeName usernameAttribute;

  private LdapDistinguishedName baseUserDn;

  private LdapDistinguishedName baseGroupDn;

  private StringField groupObjectClass;

  private LdapAttributeName groupAttributeHoldingMember;

  private LdapAttributeName memberAttributeReferencedInGroup;

  private LdapUseCase useCase;

  public LdapDirectorySettingsField() {
    super(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);

    this.usernameAttribute = new LdapAttributeName(USER_NAME_ATTRIBUTE);
    this.baseUserDn = new LdapDistinguishedName(BASE_USER_DN);
    this.baseGroupDn = new LdapDistinguishedName(BASE_GROUP_DN);
    this.groupObjectClass = new StringField(GROUP_OBJECT_CLASS);
    this.groupAttributeHoldingMember = new LdapAttributeName(GROUP_ATTRIBUTE_HOLDING_MEMBER);
    this.memberAttributeReferencedInGroup =
        new LdapAttributeName(MEMBER_ATTRIBUTE_REFERENCED_IN_GROUP);
    this.useCase = new LdapUseCase();
  }

  @Override
  public List<Field> getFields() {
    return ImmutableList.of(
        usernameAttribute,
        baseUserDn,
        baseGroupDn,
        groupObjectClass,
        groupAttributeHoldingMember,
        memberAttributeReferencedInGroup,
        useCase);
  }

  // Field getters
  public LdapAttributeName usernameAttributeField() {
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

  public LdapAttributeName groupAttributeHoldingMemberField() {
    return groupAttributeHoldingMember;
  }

  public LdapAttributeName memberAttributeReferencedInGroupField() {
    return memberAttributeReferencedInGroup;
  }

  // Value getters
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

  public LdapDirectorySettingsField useDefaultRequiredForAuthentication() {
    baseUserDn.isRequired(true);
    usernameAttribute.isRequired(true);
    useCase.isRequired(true);
    baseGroupDn.isRequired(true);
    isRequired(true);
    return this;
  }

  public LdapDirectorySettingsField useDefaultRequiredForAttributeStore() {
    useDefaultRequiredForAuthentication();
    groupObjectClass.isRequired(true);
    groupAttributeHoldingMember.isRequired(true);
    memberAttributeReferencedInGroup.isRequired(true);
    return this;
  }

  public String useCase() {
    return useCase.getValue();
  }

  // Value setters
  public LdapDirectorySettingsField baseUserDn(String baseUserDn) {
    this.baseUserDn.setValue(baseUserDn);
    return this;
  }

  public LdapDirectorySettingsField baseGroupDn(String baseGroupDn) {
    this.baseGroupDn.setValue(baseGroupDn);
    return this;
  }

  public LdapDirectorySettingsField groupObjectClass(String groupObjectCLass) {
    this.groupObjectClass.setValue(groupObjectCLass);
    return this;
  }

  public LdapDirectorySettingsField usernameAttribute(String usernameAttribute) {
    this.usernameAttribute.setValue(usernameAttribute);
    return this;
  }

  public LdapDirectorySettingsField groupAttributeHoldingMember(
      String groupAttributeHoldingMember) {
    this.groupAttributeHoldingMember.setValue(groupAttributeHoldingMember);
    return this;
  }

  public LdapDirectorySettingsField memberAttributeReferencedInGroup(
      String memberAttributeReferencedInGroup) {
    this.memberAttributeReferencedInGroup.setValue(memberAttributeReferencedInGroup);
    return this;
  }

  public LdapDirectorySettingsField useCase(String useCase) {
    this.useCase.setValue(useCase);
    return this;
  }
}
