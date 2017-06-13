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

import static org.codice.ddf.admin.api.fields.ScalarField.ScalarType.INTEGER;

public class IntegerField extends BaseScalarField<Integer> {

    public static final String DEFAULT_FIELD_NAME  = "integer";

    public IntegerField() {
        this(DEFAULT_FIELD_NAME);
    }

    public IntegerField(String fieldName) {
        super(fieldName, null, null, INTEGER);
    }

    protected IntegerField(String fieldName, String fieldTypeName, String description) {
        super(fieldName, fieldTypeName, description, INTEGER);
    }
}
