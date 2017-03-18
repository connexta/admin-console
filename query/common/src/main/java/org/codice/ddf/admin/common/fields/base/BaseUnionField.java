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
package org.codice.ddf.admin.common.fields.base;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codice.ddf.admin.api.fields.ObjectField;
import org.codice.ddf.admin.api.fields.UnionField;

public abstract class BaseUnionField extends BaseObjectField implements UnionField {

    private List<ObjectField> unionTypes;

    // TODO: tbatie - 3/16/17 - We could do something similar to what we do for enum types, have an internal union value field instead
    public BaseUnionField(String fieldName, String fieldTypeName, String description, List<ObjectField> unionTypes, boolean isUnionValue) {
        super(fieldName, fieldTypeName, description, isUnionValue ? FieldBaseType.OBJECT : FieldBaseType.UNION);
        this.unionTypes = unionTypes;
    }

    @Override
    public Map<String, Object> getValue() {
        Map<String, Object> value = new HashMap<>();
        getFields().forEach(field -> value.put(field.fieldName(), field.getValue()));
        value.put(FIELD_TYPE_NAME_KEY, fieldTypeName());
        return value;
    }

    @Override
    public List<ObjectField> getUnionTypes() {
        return unionTypes;
    }
}
