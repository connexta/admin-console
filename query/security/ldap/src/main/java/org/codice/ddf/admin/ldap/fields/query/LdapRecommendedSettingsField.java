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
package org.codice.ddf.admin.ldap.fields.query;

import com.google.common.collect.ImmutableList;
import java.util.List;
import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.api.fields.ListField;
import org.codice.ddf.admin.common.fields.base.BaseObjectField;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;
import org.codice.ddf.admin.ldap.fields.LdapDistinguishedName;

public class LdapRecommendedSettingsField extends BaseObjectField {
  public static final String DEFAULT_FIELD_NAME = "recommendedSettings";

  public static final String FIELD_TYPE_NAME = "LdapRecommendedSettings";

  public static final String DESCRIPTION =
      "An object containing potential values to be used for setting up LDAP.";

  private static final String USER_DNS = "userDns";

  private static final String GROUPS_DNS = "groupsDns";

  private static final String LOGIN_USER_ATTRIBUTES = "loginUserAttributes";

  private static final String GROUP_OBJECT_CLASSES = "groupObjectClasses";

  private static final String GROUP_ATTRIBUTES_HOLDING_MEMBER = "groupAttributesHoldingMember";

  private static final String MEMBER_ATTRIBUTES_REFERENCED_IN_GROUP =
      "memberAttributesReferencedInGroup";

  private static final String QUERY_BASES = "queryBases";

  private LdapDistinguishedName.ListImpl userDns;

  private LdapDistinguishedName.ListImpl groupDns;

  private StringField.ListImpl loginUserAttributes;

  private StringField.ListImpl groupObjectClasses;

  private StringField.ListImpl groupAttributesHoldingMember;

  private StringField.ListImpl memberAttributesReferencedInGroup;

  private LdapDistinguishedName.ListImpl queryBases;

  public LdapRecommendedSettingsField() {
    super(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
    userDns = new LdapDistinguishedName.ListImpl(USER_DNS);
    groupDns = new LdapDistinguishedName.ListImpl(GROUPS_DNS);
    loginUserAttributes = new StringField.ListImpl(LOGIN_USER_ATTRIBUTES);
    groupObjectClasses = new StringField.ListImpl(GROUP_OBJECT_CLASSES);
    groupAttributesHoldingMember = new StringField.ListImpl(GROUP_ATTRIBUTES_HOLDING_MEMBER);
    memberAttributesReferencedInGroup =
        new StringField.ListImpl(MEMBER_ATTRIBUTES_REFERENCED_IN_GROUP);
    queryBases = new LdapDistinguishedName.ListImpl(QUERY_BASES);
  }

  // Field getters
  public ListField<LdapDistinguishedName> userDnsField() {
    return userDns;
  }

  public ListField<LdapDistinguishedName> groupDnsField() {
    return groupDns;
  }

  public ListField<StringField> loginUserAttributesField() {
    return loginUserAttributes;
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

  // Value setters
  public LdapRecommendedSettingsField userDns(List<String> baseDns) {
    this.userDns.setValue(baseDns);
    return this;
  }

  public LdapRecommendedSettingsField groupDns(List<String> groupDns) {
    this.groupDns.setValue(groupDns);
    return this;
  }

  public LdapRecommendedSettingsField loginUserAttributes(List<String> loginUserAttributes) {
    this.loginUserAttributes.setValue(loginUserAttributes);
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

  @Override
  public List<Field> getFields() {
    return ImmutableList.of(
        userDns,
        groupDns,
        loginUserAttributes,
        groupObjectClasses,
        groupAttributesHoldingMember,
        memberAttributesReferencedInGroup,
        queryBases);
  }
}
