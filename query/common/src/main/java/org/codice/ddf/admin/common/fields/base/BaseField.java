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
import java.util.Set;

import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.api.report.ErrorMessage;
import org.codice.ddf.admin.common.report.message.DefaultMessages;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class BaseField<T> implements Field<T> {

    private String name;

    private String typeName;

    private String description;

    private List<String> subpath;

    private String pathName;

    private boolean isRequired;

    private T value;

    public BaseField(String name, String typeName, String description) {
        this.name = name;
        this.typeName = typeName;
        this.description = description;
        pathName = name;
        subpath = new ArrayList<>();
        isRequired = false;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getTypeName() {
        return typeName;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Set<String> getErrorCodes() {
        return ImmutableSet.of(DefaultMessages.MISSING_REQUIRED_FIELD);
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
    public boolean isRequired() {
        return isRequired;
    }

    @Override
    public BaseField<T> isRequired(boolean required) {
        isRequired = required;
        return this;
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
    public List<String> path() {
        return new ImmutableList.Builder().addAll(subpath)
                .add(pathName)
                .build();
    }

    @Override
    public void pathName(String pathName) {
        this.pathName = pathName;
    }

    @Override
    public void updatePath(List<String> subPath) {
        subpath.clear();
        subpath.addAll(subPath);
    }
}
