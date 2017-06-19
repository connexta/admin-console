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
package org.codice.ddf.admin.sources.fields;

import java.util.List;

import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.common.fields.base.BaseObjectField;
import org.codice.ddf.admin.common.fields.base.scalar.BooleanField;

import com.google.common.collect.ImmutableList;

public class SourceInfoField extends BaseObjectField {

    public static final String DEFAULT_FIELD_NAME = "sourceInfo";

    public static final String IS_AVAILABLE_FIELD_NAME = "isAvailable";

    private static final String FIELD_TYPE_NAME = "SourceInfo";

    private static final String DESCRIPTION =
            "Contains various information such as if the source is reachable, and the source configuration.";

    protected BooleanField isAvailable;

    public SourceInfoField() {
        super(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
        isAvailable = new BooleanField(IS_AVAILABLE_FIELD_NAME);
        updateInnerFieldPaths();
    }

    public SourceInfoField(String fieldName, String fieldTypeName, String description) {
        super(fieldName, fieldTypeName, description);
        isAvailable = new BooleanField(IS_AVAILABLE_FIELD_NAME);
    }

    public SourceInfoField isAvaliable(boolean available) {
        isAvailable.setValue(available);
        return this;
    }

    public Boolean isAvailable() {
        return isAvailable.getValue();
    }

    @Override
    public List<Field> getFields() {
        return ImmutableList.of(isAvailable);
    }
}
