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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.codice.ddf.admin.api.DataType;
import org.codice.ddf.admin.api.report.ErrorMessage;
import org.codice.ddf.admin.common.report.message.DefaultMessages;

public class BaseDataType<T> extends BaseField<T, T> implements DataType<T> {

    private String fieldTypeName;

    private boolean isRequired;

    private T value;

    public BaseDataType(String fieldName, String fieldTypeName, String description) {
        super(fieldName, description);
        this.fieldTypeName = fieldTypeName;
        isRequired = false;
    }

    @Override
    public String fieldTypeName() {
        return fieldTypeName;
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
    public List<ErrorMessage> validate() {
        List<ErrorMessage> errors = new ArrayList<>();

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
    public Set<String> getErrorCodes() {
        Set<String> errors = new HashSet<>();
        errors.add(DefaultMessages.MISSING_REQUIRED_FIELD);
        return errors;
    }
}
