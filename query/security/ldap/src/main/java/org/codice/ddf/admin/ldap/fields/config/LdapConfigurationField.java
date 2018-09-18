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
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.api.fields.ListField;
import org.codice.ddf.admin.common.fields.base.BaseListField;
import org.codice.ddf.admin.common.fields.base.BaseObjectField;
import org.codice.ddf.admin.common.fields.common.PidField;
import org.codice.ddf.admin.ldap.fields.connection.LdapBindUserInfo;
import org.codice.ddf.admin.ldap.fields.connection.LdapConnectionField;
import org.codice.ddf.admin.ldap.fields.connection.LdapLoadBalancingField;
import org.codice.ddf.admin.security.common.fields.wcpm.ClaimsMapEntry;

public class LdapConfigurationField extends BaseObjectField {
  public static final String DEFAULT_FIELD_NAME = "config";

  public static final String FIELD_TYPE_NAME = "LdapConfiguration";

  public static final String DESCRIPTION =
      "A configuration containing all the required fields for saving LDAP settings";

  public static final String CLAIMS_MAPPING = "claimsMapping";

  private PidField pid;

  private ListField<LdapConnectionField> connection;

  private LdapLoadBalancingField loadBalancing;

  private LdapBindUserInfo bindUserInfo;

  private LdapDirectorySettingsField settings;

  private ListField<ClaimsMapEntry> claimMappings;

  public LdapConfigurationField() {
    super(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
    pid = new PidField();
    connection = new LdapConnectionField.ListImpl();
    loadBalancing = new LdapLoadBalancingField();
    bindUserInfo = new LdapBindUserInfo();
    settings = new LdapDirectorySettingsField();
    claimMappings = new ClaimsMapEntry.ListImpl();
  }

  // Field getters
  public ListField<LdapConnectionField> connectionField() {
    return connection;
  }

  public LdapConfigurationField connectionField(ListField<LdapConnectionField> entries) {
    connection = entries;
    return this;
  }

  public LdapLoadBalancingField loadBalancingField() {
    return loadBalancing;
  }

  public LdapBindUserInfo bindUserInfoField() {
    return bindUserInfo;
  }

  public LdapDirectorySettingsField settingsField() {
    return settings;
  }

  public PidField pidField() {
    return pid;
  }

  public ListField<ClaimsMapEntry> claimMappingsField() {
    return claimMappings;
  }

  public LdapConfigurationField claimMappingsField(ListField<ClaimsMapEntry> entries) {
    claimMappings = entries;
    return this;
  }

  // Value getters
  public String pid() {
    return pid.getValue();
  }

  public Map<String, String> claimsMapping() {
    return claimMappings
        .getList()
        .stream()
        .collect(Collectors.toMap(ClaimsMapEntry::key, ClaimsMapEntry::value));
  }

  // Value setters
  public LdapConfigurationField pid(String pid) {
    this.pid.setValue(pid);
    return this;
  }

  public LdapConfigurationField connection(List<LdapConnectionField> connection) {
    this.connection.setValue(connection);
    return this;
  }

  public LdapConfigurationField connection(ListField<LdapConnectionField> connection) {
    this.connection.setValue(connection.getValue());
    return this;
  }

  public LdapConfigurationField loadBalancing(LdapLoadBalancingField loadBalancing) {
    this.loadBalancing.setValue(loadBalancing.getValue());
    return this;
  }

  public LdapConfigurationField bindUserInfo(LdapBindUserInfo bindUserInfo) {
    this.bindUserInfo.setValue(bindUserInfo.getValue());
    return this;
  }

  public LdapConfigurationField settings(LdapDirectorySettingsField settings) {
    this.settings.setValue(settings.getValue());
    return this;
  }

  public LdapConfigurationField mapClaim(String claim, String attribute) {
    claimMappings.add(new ClaimsMapEntry().key(claim).value(attribute));
    return this;
  }

  public LdapConfigurationField mapAllClaims(Map<String, String> mapping) {
    mapping
        .entrySet()
        .forEach(
            entry ->
                claimMappings.add(
                    new ClaimsMapEntry().key(entry.getKey()).value(entry.getValue())));
    return this;
  }

  @Override
  public List<Field> getFields() {
    return ImmutableList.of(pid, connection, loadBalancing, bindUserInfo, settings, claimMappings);
  }

  public LdapConfigurationField useDefaultRequired() {
    bindUserInfo.useDefaultRequired();
    settings.useDefaultRequiredForAuthentication();
    isRequired(true);
    return this;
  }

  public static class ListImpl extends BaseListField<LdapConfigurationField> {

    public static final String DEFAULT_FIELD_NAME = "configs";

    public ListImpl() {
      super(DEFAULT_FIELD_NAME);
    }

    @Override
    public Callable<LdapConfigurationField> getCreateListEntryCallable() {
      return LdapConfigurationField::new;
    }
  }
}
