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
package org.codice.ddf.admin.security.common.fields.wcpm;

import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.api.fields.ListField;
import org.codice.ddf.admin.common.fields.base.BaseListField;
import org.codice.ddf.admin.common.fields.base.BaseObjectField;
import org.codice.ddf.admin.common.fields.common.ContextPath;
import org.codice.ddf.internal.admin.configurator.actions.ServiceReader;

public class ContextPolicyBin extends BaseObjectField {

  public static final String DEFAULT_FIELD_NAME = "bin";

  public static final String FIELD_TYPE_NAME = "ContextPolicyBin";

  public static final String DESCRIPTION =
      "Represents a policy being applied to a set of context paths.";

  private ContextPath.ListImpl contexts;

  private AuthType.ListImpl authTypes;

  private Realm realm;

  private ClaimsMapEntry.ListImpl claimsMapping;

  private ServiceReader serviceReader;

  public ContextPolicyBin(ServiceReader serviceReader) {
    super(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
    this.serviceReader = serviceReader;

    contexts = new ContextPath.ListImpl();
    authTypes = new AuthType.ListImpl(serviceReader);
    realm = new Realm(serviceReader);
    claimsMapping = new ClaimsMapEntry.ListImpl();
    updateInnerFieldPaths();
  }

  public ContextPolicyBin realm(String realm) {
    this.realm.setValue(realm);
    return this;
  }

  public ContextPolicyBin realm(Realm realm) {
    this.realm = realm;
    return this;
  }

  public ContextPolicyBin addContextPath(ContextPath contextPath) {
    contexts.add(contextPath);
    return this;
  }

  public ContextPolicyBin addContextPath(String contextPath) {
    contexts.add(new ContextPath(contextPath));
    return this;
  }

  public ContextPolicyBin addClaimsMapping(String claim, String claimValue) {
    claimsMapping.add(new ClaimsMapEntry().key(claim).value(claimValue));
    return this;
  }

  public ContextPolicyBin addClaimsMapping(ClaimsMapEntry entry) {
    claimsMapping.add(entry);
    return this;
  }

  public ContextPolicyBin addClaimsMap(Map<String, String> claimsMap) {
    List<ClaimsMapEntry> claims =
        claimsMap
            .entrySet()
            .stream()
            .map(entry -> new ClaimsMapEntry().key(entry.getKey()).value(entry.getValue()))
            .collect(Collectors.toList());
    claimsMapping.addAll(claims);
    return this;
  }

  public ContextPolicyBin addAuthType(String authType) {
    AuthType newAuthType = new AuthType(serviceReader);
    newAuthType.setValue(authType);
    authTypes.add(newAuthType);
    return this;
  }

  public ContextPolicyBin authTypes(Collection<String> authTypes) {
    authTypes.forEach(authType -> addAuthType(authType));
    return this;
  }

  public List<String> contexts() {
    return contexts.getValue();
  }

  public ContextPath.ListImpl contextFields() {
    return contexts;
  }

  public List<String> authTypes() {
    return authTypes.getValue();
  }

  public String realm() {
    return realm.getValue();
  }

  public Map<String, String> claimsMapping() {
    Map<String, String> mapping = new HashMap<>();
    claimsMapping.getList().forEach(entry -> mapping.put(entry.key(), entry.value()));
    return mapping;
  }

  public ListField<ClaimsMapEntry> claimsMappingField() {
    return claimsMapping;
  }

  @Override
  public List<Field> getFields() {
    return ImmutableList.of(contexts, authTypes, realm, claimsMapping);
  }

  public ContextPolicyBin useDefaultRequiredFields() {
    isRequired(true);
    contexts.isRequired(true);
    authTypes.useDefaultRequired();
    realm.isRequired(true);
    claimsMapping.useDefaultRequired();
    return this;
  }

  public static class ListImpl extends BaseListField<ContextPolicyBin> {

    public static final String DEFAULT_FIELD_NAME = "policies";

    private Callable<ContextPolicyBin> newPolicy;

    private final ServiceReader serviceReader;

    public ListImpl(ServiceReader serviceReader) {
      super(DEFAULT_FIELD_NAME);
      this.serviceReader = serviceReader;
      newPolicy = () -> new ContextPolicyBin(serviceReader);
    }

    @Override
    public Callable<ContextPolicyBin> getCreateListEntryCallable() {
      return newPolicy;
    }

    @Override
    public ListImpl addAll(Collection<ContextPolicyBin> values) {
      super.addAll(values);
      return this;
    }

    @Override
    public ListImpl useDefaultRequired() {
      newPolicy =
          () -> {
            ContextPolicyBin bin = new ContextPolicyBin(serviceReader);
            bin.useDefaultRequiredFields();
            return bin;
          };

      isRequired(true);
      return this;
    }
  }
}
