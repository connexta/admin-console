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
package org.codice.ddf.admin.sources.csw;

import java.util.List;
import java.util.concurrent.Callable;

import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.common.fields.base.BaseListField;
import org.codice.ddf.admin.common.fields.base.BaseObjectField;
import org.codice.ddf.admin.common.fields.base.scalar.BooleanField;
import org.codice.ddf.admin.sources.fields.type.CswSourceConfigurationField;

import com.google.common.collect.ImmutableList;

public class CswSourceInfoField extends BaseObjectField {

    public static final String DEFAULT_FIELD_NAME = "cswSourceInfo";

    public static final String FIELD_TYPE_NAME = "CswSourceInfo";

    public static final String IS_AVAILABLE_FIELD_NAME = "isAvailable";

    public static final String DESCRIPTION =
            "Contains the availability and properties of the CSW source.";

    private CswSourceConfigurationField config;

    private BooleanField isAvailable;

    public CswSourceInfoField() {
        super(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
        config = new CswSourceConfigurationField();
        isAvailable = new BooleanField(IS_AVAILABLE_FIELD_NAME);
        updateInnerFieldPaths();
    }

    public CswSourceInfoField config(CswSourceConfigurationField config) {
        this.config = config;
        return this;
    }

    public CswSourceInfoField isAvaliable(boolean available) {
        isAvailable.setValue(available);
        return this;
    }

    public CswSourceConfigurationField config() {
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

    public static class SourceInfos extends BaseListField<SourceInfoField> {

        public static final String DEFAULT_FIELD_NAME = "sources";

        public SourceInfos() {
            super(DEFAULT_FIELD_NAME);
        }

        @Override
        public Callable<SourceInfoField> getCreateListEntryCallable() {
            return SourceInfoField::new;
        }
    }

}
