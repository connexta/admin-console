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

import static org.codice.ddf.admin.common.message.DefaultMessages.missingRequiredFieldError;

import java.util.ArrayList;
import java.util.List;

import org.codice.ddf.admin.api.action.Message;
import org.codice.ddf.admin.api.fields.Field;

import com.google.common.collect.ImmutableList;

public abstract class BaseField<T> implements Field<T> {

    private String fieldName;

    private String fieldTypeName;

    private String description;

    private FieldBaseType fieldBaseType;

    private boolean isRequired;

    private List<String> subpath;

    public BaseField(String fieldName, String fieldTypeName, String description,
            FieldBaseType fieldBaseType) {
        this.fieldName = fieldName;
        this.fieldTypeName = fieldTypeName;
        this.fieldBaseType = fieldBaseType;
        this.description = description;
        isRequired = false;
        subpath = new ArrayList<>();
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    @Override
    public void fieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String fieldTypeName() {
        return fieldTypeName;
    }

    @Override
    public FieldBaseType fieldBaseType() {
        return fieldBaseType;
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public boolean isRequired() {
        return isRequired;
    }

    @Override
    public BaseField<T> isRequired(boolean required) {
        isRequired = required;
        return this;
    }

    @Override
    public List<String> path() {
        return new ImmutableList.Builder().addAll(subpath)
                .add(fieldName)
                .build();
    }

    @Override
    public void updatePath(List<String> subPath) {
        subpath.clear();
        subpath.addAll(subPath);
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
    public Field<T> matchRequired(Field<T> fieldToMatch) {
        isRequired(fieldToMatch.isRequired());
        return this;
    }
}
