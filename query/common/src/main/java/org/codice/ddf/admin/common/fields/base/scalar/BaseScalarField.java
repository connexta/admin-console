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
package org.codice.ddf.admin.common.fields.base.scalar;

import java.util.ArrayList;
import java.util.List;

import org.codice.ddf.admin.api.action.Message;
import org.codice.ddf.admin.common.fields.base.BaseField;

public abstract class BaseScalarField<T> extends BaseField<T> {

    private T value;

    public BaseScalarField(String fieldName, String fieldTypeName, String description,
            FieldBaseType fieldBaseType) {
        super(fieldName, fieldTypeName, description, fieldBaseType);
    }

    public T getValue() {
        return value;
    }

    @Override
    public void setValue(T value) {
        this.value = value;
    }

    @Override
    public List<Message> validate() {
        // TODO: tbatie - 3/16/17 - Validate scalar fields
        return new ArrayList<>();
    }
}
