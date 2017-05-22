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

import static org.codice.ddf.admin.common.report.message.DefaultMessages.missingRequiredFieldError;

import java.util.ArrayList;
import java.util.List;

import org.codice.ddf.admin.api.DataType;
import org.codice.ddf.admin.api.report.Message;

public class BaseDataType<T> extends BaseField<T, T> implements DataType<T> {

    private String fieldTypeName;

    private FieldBaseType fieldBaseType;

    private boolean isRequired;

    private T value;

    public BaseDataType(String fieldName, String fieldTypeName, String description,
            FieldBaseType fieldBaseType) {
        super(fieldName, description);
        this.fieldTypeName = fieldTypeName;
        this.fieldBaseType = fieldBaseType;
        isRequired = false;
    }

    @Override
    public String fieldTypeName() {
        return fieldTypeName;
    }

    @Override
    public FieldBaseType baseDataType() {
        return fieldBaseType;
    }

    @Override
    public boolean isRequired() {
        return isRequired;
    }

    @Override
    public BaseDataType<T> isRequired(boolean required) {
        isRequired = required;
        return this;
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public void setValue(T value) {
        this.value = value;
    }

    @Override
    public List<Message> validate() {
        List<Message> errors = new ArrayList<>();

        if (isRequired()) {
            if (getValue() == null) {
                errors.add(missingRequiredFieldError(path()));
            } else if (getValue() instanceof List && ((List) getValue()).isEmpty()) {
                errors.add(missingRequiredFieldError(path()));
            }
        }

        return errors;
    }

    @Override
    public DataType<T> matchRequired(DataType<T> fieldToMatch) {
        isRequired(fieldToMatch.isRequired());
        return this;
    }
}
