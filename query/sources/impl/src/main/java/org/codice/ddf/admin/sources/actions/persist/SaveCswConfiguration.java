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
package org.codice.ddf.admin.sources.actions.persist;

import static org.codice.ddf.admin.sources.sample.SampleFields.SAMPLE_CSW_SOURCE_INFO;

import java.util.List;

import org.codice.ddf.admin.api.fields.Field;
import org.codice.ddf.admin.common.actions.BaseAction;
import org.codice.ddf.admin.common.fields.common.CredentialsField;
import org.codice.ddf.admin.common.fields.common.PidField;
import org.codice.ddf.admin.common.message.ErrorMessage;
import org.codice.ddf.admin.sources.fields.SourceInfoField;
import org.codice.ddf.admin.sources.fields.type.CswSourceConfigurationField;

import com.google.common.collect.ImmutableList;

public class SaveCswConfiguration extends BaseAction<SourceInfoField> {

    public static final String NAME = "saveCswSource";

    public static final String DESCRIPTION =
            "Saves a csw source configuration. If a pid is specified, that source configuration will be updated.";

    private CswSourceConfigurationField config;

    public SaveCswConfiguration() {
        super(NAME, DESCRIPTION, new SourceInfoField());
        config = new CswSourceConfigurationField()
                .allFieldsRequired(true)
                .innerFieldRequired(false, PidField.DEFAULT_FIELD_NAME)
                .innerFieldRequired(false, CredentialsField.DEFAULT_FIELD_NAME);
    }

    @Override
    public List<Field> getArguments() {
        return ImmutableList.of(config);
    }

    @Override
    public SourceInfoField performAction() {
        addMessage(new ErrorMessage("SAVE_CSW_CONFIG_ERROR"));
        return SAMPLE_CSW_SOURCE_INFO;
    }
}
