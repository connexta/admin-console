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
package org.codice.ddf.admin.sources.opensearch;

import java.util.List;

import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.common.fields.base.BaseObjectField;
import org.codice.ddf.admin.common.fields.base.scalar.BooleanField;
import org.codice.ddf.admin.sources.fields.type.OpenSearchSourceConfigurationField;

import com.google.common.collect.ImmutableList;

public class OpenSearchSourceInfoField extends BaseObjectField {

    public static final String DEFAULT_FIELD_NAME = "openSearchSourceInfo";

    public static final String FIELD_TYPE_NAME = "OpenSearchSourceInfo";

    public static final String IS_AVAILABLE_FIELD_NAME = "isAvailable";

    public static final String DESCRIPTION =
            "Contains the availability and properties of the OpenSearch source.";

    private OpenSearchSourceConfigurationField config;

    private BooleanField isAvailable;

    public OpenSearchSourceInfoField() {
        super(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
        config = new OpenSearchSourceConfigurationField();
        isAvailable = new BooleanField(IS_AVAILABLE_FIELD_NAME);
        updateInnerFieldPaths();
    }

    public OpenSearchSourceInfoField config(OpenSearchSourceConfigurationField config) {
        this.config = config;
        return this;
    }

    public OpenSearchSourceInfoField isAvailable(boolean isAvailable) {
        this.isAvailable.setValue(isAvailable);
        return this;
    }

    public OpenSearchSourceConfigurationField config() {
        return config;
    }

    public Boolean isAvailable() {
        return isAvailable.getValue();
    }

    public BooleanField isAvailableField() {
        return isAvailable;
    }

    @Override
    public List<Field> getFields() {
        return ImmutableList.of(config, isAvailable);
    }
}
