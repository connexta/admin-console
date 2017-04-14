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
package org.codice.ddf.admin.security.common.fields.wcpm;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.codice.ddf.admin.api.fields.Field;
import org.codice.ddf.admin.api.fields.ListField;
import org.codice.ddf.admin.common.fields.base.BaseObjectField;
import org.codice.ddf.admin.common.fields.base.ListFieldImpl;
import org.codice.ddf.admin.common.fields.common.ContextPath;

import com.google.common.collect.ImmutableList;

public class ContextPolicyBin extends BaseObjectField {

    public static final String DEFAULT_FIELD_NAME = "bin";

    public static final String FIELD_TYPE_NAME = "ContextPolicyBin";

    public static final String DESCRIPTION =
            "Represents a policy being applied to a set of context paths.";

    private ListField<ContextPath> contexts;

    private ListField<AuthType> authTypes;

    private Realm realm;

    private ListField<ClaimsMapEntry> claimsMapping;

    public ContextPolicyBin() {
        super(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
        contexts = new ListFieldImpl<>("paths", ContextPath.class);
        authTypes = new ListFieldImpl<>("authTypes", AuthType.class);
        realm = new Realm();
        claimsMapping = new ListFieldImpl<>("claimsMapping", ClaimsMapEntry.class);
    }

    public ContextPolicyBin realm(String realm) {
        this.realm = new Realm().getRealmFromValue(realm);
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
        claimsMapping.add(new ClaimsMapEntry().claim(claim)
                .claimValue(claimValue));
        return this;
    }

    public ContextPolicyBin addClaimsMapping(ClaimsMapEntry entry) {
        claimsMapping.add(entry);
        return this;
    }

    public ContextPolicyBin addClaimsMap(Map<String, String> claimsMap) {
        List<ClaimsMapEntry> claims = claimsMap.entrySet()
                .stream()
                .map(entry -> new ClaimsMapEntry().claim(entry.getKey())
                        .claimValue(entry.getValue()))
                .collect(Collectors.toList());
        claimsMapping.addAll(claims);
        return this;
    }

    public ContextPolicyBin addAuthType(String authType) {
        authTypes.add(new AuthType().getAuthTypeFromValue(authType));
        return this;
    }

    public ContextPolicyBin addAuthType(AuthType authType) {
        authTypes.add(authType);
        return this;
    }

    public ContextPolicyBin authTypes(Collection<String> authTypes) {
        authTypes.stream()
                .forEach(authType -> addAuthType(authType));
        return this;
    }

    public ContextPolicyBin contexts(Collection<String> contexts) {
        contexts.stream()
                .forEach(context -> addContextPath(context));
        return this;
    }

    public List<String> contexts() {
        return contexts.getValue();
    }

    public List<String> authTypes() {
        return authTypes.getValue();
    }

    public String realm() {
        return realm.getValue();
    }

    public Map<String, String> claimsMapping() {
        Map<String, String> mapping = new HashMap<>();
        claimsMapping.getList().forEach(entry -> mapping.put(entry.claim(), entry.claimValue()));
        return mapping;
    }

    @Override
    public List<Field> getFields() {
        return ImmutableList.of(contexts, authTypes, realm, claimsMapping);
    }

    @Override
    public ContextPolicyBin allFieldsRequired(boolean required) {
        super.allFieldsRequired(required);
        return this;
    }
}
