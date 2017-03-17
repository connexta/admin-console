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
package org.codice.ddf.admin.query.commons.fields.base;

import static org.codice.ddf.admin.query.api.fields.Field.FieldBaseType.OBJECT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codice.ddf.admin.query.api.action.Message;
import org.codice.ddf.admin.query.api.fields.ObjectField;

public abstract class BaseObjectField extends BaseField<Map<String, Object>> implements ObjectField {

    public BaseObjectField(String fieldName, String fieldTypeName, String description) {
        super(fieldName, fieldTypeName, description, OBJECT);
    }

    protected BaseObjectField(String fieldName, String fieldTypeName, String description, FieldBaseType baseType) {
        super(fieldName, fieldTypeName, description, baseType);
    }

    @Override
    public Map<String, Object> getValue() {
        Map<String, Object> value = new HashMap<>();
        getFields().forEach(field -> value.put(field.fieldName(), field.getValue()));
        return value;
    }

    @Override
    public void setValue(Map<String, Object> values) {
        getFields().stream()
                .filter(field -> values.containsKey(field.fieldName()))
                .forEach(field -> field.setValue(values.get(field.fieldName())));
    }

    @Override
    public List<Message> validate() {
        // TODO: tbatie - 3/16/17 - Validate object fields
        return new ArrayList<>();
    }
}
