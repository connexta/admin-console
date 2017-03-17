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
package org.codice.ddf.admin.query.wcpm.fields;

import java.util.List;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.commons.fields.base.BaseObjectField;
import org.codice.ddf.admin.query.commons.fields.common.ContextPath;
import org.codice.ddf.admin.query.commons.fields.common.ContextPaths;

import com.google.common.collect.ImmutableList;

public class ContextPolicyBin extends BaseObjectField {

    public static final String DEFAULT_FIELD_NAME = "bin";
    public static final String FIELD_TYPE_NAME  ="ContextPolicyBin";
    public static final String DESCRIPTION = "Represents a policy being applied to a set of context paths.";

    private ContextPaths contexts;
    private AuthTypeList authTypes;
    private Realm realm;
    private ClaimsMapping claimsMapping;

    public ContextPolicyBin() {
        super(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
        contexts = new ContextPaths();
        authTypes = new AuthTypeList();
        realm = new Realm();
        claimsMapping = new ClaimsMapping();
    }

    public ContextPolicyBin realm(Realm realm) {
        this.realm = realm;
        return this;
    }

    public ContextPolicyBin addContextPath(ContextPath contextPath) {
        contexts.add(contextPath);
        return this;
    }

    public ContextPolicyBin addClaimsMapping(ClaimsMapEntry entry) {
        claimsMapping.add(entry);
        return this;
    }

    public ContextPolicyBin addAuthType(AuthType authType) {
        authTypes.add(authType);
        return this;
    }

    @Override
    public List<Field> getFields() {
        return ImmutableList.of(contexts, authTypes, realm, claimsMapping);
    }
}
