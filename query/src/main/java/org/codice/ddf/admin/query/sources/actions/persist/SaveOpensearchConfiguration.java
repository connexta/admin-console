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

import static org.codice.ddf.admin.query.sources.sample.SampleFields.SAMPLE_OPENSEARCH_SOURCE_INFO;

import java.util.List;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.commons.actions.BaseAction;
import org.codice.ddf.admin.query.sources.common.SourceInfoField;
import org.codice.ddf.admin.query.sources.common.fields.OpensearchSourceConfigurationField;

import com.google.common.collect.ImmutableList;

public class SaveOpensearchConfiguration extends BaseAction<SourceInfoField> {

    public static final String NAME = "saveOpeansearchSource";
    public static final String DESCRIPTION = "Saves a wfs source configuration. If a pid is specified, that source configuration will be updated.";
    private OpensearchSourceConfigurationField config;

    public SaveOpensearchConfiguration() {
        super(NAME, DESCRIPTION, new SourceInfoField());
        config = new OpensearchSourceConfigurationField();
    }

    @Override
    public SourceInfoField process() {
        return SAMPLE_OPENSEARCH_SOURCE_INFO;
    }

    @Override
    public List<Field> getArguments() {
        return ImmutableList.of(config);
    }
}
