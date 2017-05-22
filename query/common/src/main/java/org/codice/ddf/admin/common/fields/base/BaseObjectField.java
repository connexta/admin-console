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

import static org.codice.ddf.admin.api.DataType.FieldBaseType.OBJECT;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.codice.ddf.admin.api.DataType;
import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.api.fields.ObjectField;
import org.codice.ddf.admin.api.report.Message;

public abstract class BaseObjectField extends BaseDataType<Map<String, Object>>
        implements ObjectField {

    // TODO: tbatie - 5/21/17 - Remove the baseType field once unions are moved to interfaces
    protected BaseObjectField(String fieldName, String fieldTypeName, String description,
            FieldBaseType baseType) {
        super(fieldName, fieldTypeName, description, baseType);
    }

    public BaseObjectField(String fieldName, String fieldTypeName, String description) {
        this(fieldName, fieldTypeName, description, OBJECT);
    }

    @Override
    public Map<String, Object> getValue() {
        Map<String, Object> value = new HashMap<>();
        getFields().forEach(field -> value.put(field.fieldName(), field.getValue()));
        return value;
    }

    @Override
    public void setValue(Map<String, Object> values) {
        if(values == null || values.isEmpty()) {
            return;
        }

        getFields().stream()
                .filter(field -> values.containsKey(field.fieldName()))
                .forEach(field -> field.setValue(values.get(field.fieldName())));
    }

    @Override
    public List<Message> validate() {
        List<Message> validationErrors = super.validate();

        if (!validationErrors.isEmpty()) {
            return validationErrors;
        }

        validationErrors.addAll(getFields().stream()
                .filter(field -> field instanceof DataType)
                .map(field -> (List<Message>) ((DataType)field).validate())
                .flatMap(Collection::stream)
                .collect(Collectors.toList()));
        return validationErrors;
    }

    @Override
    public BaseObjectField allFieldsRequired(boolean required) {
        if (required) {
            isRequired(required);
        }

        getFields().stream()
                .filter(field -> field instanceof DataType)
                .map(field -> ((DataType) field).isRequired(required))
                .filter(field -> field instanceof ObjectField)
                .map(ObjectField.class::cast)
                .forEach(objField -> objField.allFieldsRequired(required));
        return this;
    }

    @Override
    public void updatePath(List<String> subPath) {
        super.updatePath(subPath);
        updateInnerFieldPaths();
    }

    @Override
    public void fieldName(String fieldName) {
        super.fieldName(fieldName);
        updateInnerFieldPaths();
    }

    @Override
    public DataType matchRequired(DataType field) {
        super.matchRequired(field);

        if(!(field instanceof ObjectField)) {
            throw new RuntimeException("Cannot match required field that is not of type ObjectField.");
        }

        for(Field subField : ((ObjectField)field).getFields()) {
            for(Field toSetSubField : getFields()) {
                if(toSetSubField instanceof DataType && toSetSubField.fieldName().equals(subField.fieldName())) {
                    ((DataType)toSetSubField).matchRequired((DataType) subField);
                    break;
                }
            }
        }

        return this;
    }

    public void updateInnerFieldPaths() {
        getFields().stream()
                .filter(Objects::nonNull)
                .forEach(child -> child.updatePath(path()));
    }
}
