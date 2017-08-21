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

import org.codice.ddf.admin.api.fields.ScalarField;
import org.codice.ddf.admin.common.fields.base.BaseField;

public abstract class BaseScalarField<T> extends BaseField<T> implements ScalarField<T> {

    private T value;
    private ScalarType scalarType;

    public BaseScalarField(String fieldName, String fieldTypeName, String description, ScalarType scalarType) {
        super(fieldName, fieldTypeName, description);
        this.scalarType = scalarType;
    }

    public T getValue() {
        return value;
    }

    @Override
    public T getSanitizedValue() {
        return getValue();
    }

    @Override
    public void setValue(T value) {
        this.value = value;
    }

    @Override
    public ScalarType getScalarType() {
        return scalarType;
    }
}
