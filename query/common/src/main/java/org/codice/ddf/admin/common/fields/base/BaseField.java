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

import org.codice.ddf.admin.api.fields.Field;

public abstract class BaseField<T> implements Field<T> {

    private String fieldName;

    private String fieldTypeName;

    private String description;

    private FieldBaseType fieldBaseType;

    private boolean isRequired;

    public BaseField(String fieldName, String fieldTypeName, String description,
            FieldBaseType fieldBaseType) {
        this.fieldName = fieldName;
        this.fieldTypeName = fieldTypeName;
        this.fieldBaseType = fieldBaseType;
        this.description = description;
        isRequired = false;
    }

    @Override
    public String fieldName() {
        return fieldName;
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
}
