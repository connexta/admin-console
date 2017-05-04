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

import org.codice.ddf.admin.api.fields.Field;
import org.codice.ddf.admin.common.fields.base.BaseObjectField;
import org.codice.ddf.admin.common.fields.base.scalar.BooleanField;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;
import org.codice.ddf.admin.sources.fields.type.SourceConfigUnionField;

import com.google.common.collect.ImmutableList;

public class SourceInfoField extends BaseObjectField {

    public static final String DEFAULT_FIELD_NAME = "sourceInfo";

    public static final String SOURCE_HANDLER_FIELD_NAME = "sourceHandlerName";

    public static final String IS_AVAILABLE_FIELD_NAME = "isAvailable";

    private static final String FIELD_TYPE_NAME = "SourceInfo";

    private static final String DESCRIPTION =
            "Contains various information such as if the source is reachable, and the source configuration";

    private BooleanField isAvailable;

    private StringField sourceHandlerName;

    private SourceConfigUnionField config;

    public SourceInfoField() {
        super(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
    }

    public SourceInfoField isAvaliable(boolean avaliable) {
        isAvailable.setValue(avaliable);
        return this;
    }

    public SourceInfoField sourceHandlerName(String name) {
        sourceHandlerName.setValue(name);
        return this;
    }

    public SourceInfoField configuration(SourceConfigUnionField config) {
        this.config = config;
        return this;
    }

    public String sourceHandlerName() {
        return sourceHandlerName.getValue();
    }

    public Boolean isAvailable() {
        return isAvailable.getValue();
    }

    public SourceConfigUnionField config() {
        return config;
    }

    @Override
    public List<Field> getFields() {
        return ImmutableList.of(isAvailable, sourceHandlerName, config);
    }

    @Override
    public void initializeFields() {
        config = new SourceConfigUnionField();
        sourceHandlerName = new StringField(SOURCE_HANDLER_FIELD_NAME);
        isAvailable = new BooleanField(IS_AVAILABLE_FIELD_NAME);
    }
}
