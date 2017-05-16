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

import static org.codice.ddf.admin.api.fields.Field.FieldBaseType.ENUM;
import static org.codice.ddf.admin.common.message.DefaultMessages.unsupportedEnum;

import java.util.List;

import org.codice.ddf.admin.api.action.Message;
import org.codice.ddf.admin.api.fields.EnumField;
import org.codice.ddf.admin.api.fields.Field;

public abstract class BaseEnumField<S> extends BaseField<S> implements EnumField<S, Field<S>> {

    private S enumValue;

    private List<Field<S>> enumValues;

    public BaseEnumField(String fieldName, String fieldTypeName, String description,
            List<Field<S>> enumValues) {
        super(fieldName, fieldTypeName, description, ENUM);
        this.enumValues = enumValues;
    }

    public BaseEnumField(String fieldName, String fieldTypeName, String description,
            List<Field<S>> enumValues, Field<S> enumValue) {
        this(fieldName, fieldTypeName, description, enumValues);
        this.enumValue = enumValue == null ? null : enumValue.getValue();
    }

    @Override
    public List<Field<S>> getEnumValues() {
        return enumValues;
    }

    @Override
    public S getValue() {
        return enumValue;
    }

    @Override
    public void setValue(S value) {
        if(value != null) {
            enumValue = getEnumValues().stream()
                    .map(Field::getValue)
                    .filter(o -> o.equals(value) || (o instanceof String && o.toString()
                            .equalsIgnoreCase(value.toString())))
                    .findFirst()
                    .orElse(value);
        }
    }

    @Override
    public List<Message> validate() {
        List<Message> validationMsgs = super.validate();

        if(validationMsgs.isEmpty() && getValue() != null) {
            if (getEnumValues().stream()
                    .map(Field::getValue)
                    .noneMatch(v -> v.equals(getValue()))) {
                validationMsgs.add(unsupportedEnum(path()));
            }
        }
        return validationMsgs;
    }
}
