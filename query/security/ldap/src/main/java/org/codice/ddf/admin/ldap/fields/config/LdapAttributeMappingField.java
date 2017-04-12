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
import java.util.Map;

import org.codice.ddf.admin.common.fields.base.BaseListField;

public class LdapAttributeMappingField extends BaseListField<LdapAttributeEntryField> {

    public static final String DEFAULT_FIELD_NAME = "attributeMapping";

    public static final String DESCRIPTION =
            "A map containing STS claims to user attributes. Only 1 sts claim is allowed to be mapped to a single user setEnumValue.";

    public LdapAttributeMappingField() {
        super(DEFAULT_FIELD_NAME, DESCRIPTION, new LdapAttributeEntryField());
    }

    public LdapAttributeMappingField add(String stsClaim, String userAttribute) {
        add(new LdapAttributeEntryField().stsClaim(stsClaim)
                .userAttribute(userAttribute));
        return this;
    }

    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<>();
        getList().stream().forEach(entry -> map.put(entry.stsClaim(), entry.userAttribute()));
        return map;
    }
}
