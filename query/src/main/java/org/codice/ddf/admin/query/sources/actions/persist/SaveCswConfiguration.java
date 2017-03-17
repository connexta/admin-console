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
package org.codice.ddf.admin.query.sources.actions.persist;

import static org.codice.ddf.admin.query.sources.sample.SampleFields.SAMPLE_CSW_SOURCE_INFO;

import java.util.List;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.commons.actions.BaseAction;
import org.codice.ddf.admin.query.sources.common.SourceInfoField;
import org.codice.ddf.admin.query.sources.common.fields.CswSourceConfigurationField;

import com.google.common.collect.ImmutableList;

public class SaveCswConfiguration extends BaseAction<SourceInfoField> {

    public static final String NAME = "saveCswSource";
    public static final String DESCRIPTION = "Saves a csw source configuration. If a pid is specified, that source configuration will be updated.";
    private CswSourceConfigurationField config;

    public SaveCswConfiguration() {
        super(NAME, DESCRIPTION, new SourceInfoField());
        config = new CswSourceConfigurationField();
    }

    @Override
    public SourceInfoField process() {
        return SAMPLE_CSW_SOURCE_INFO;
    }

    @Override
    public List<Field> getArguments() {
        return ImmutableList.of(config);
    }
}
