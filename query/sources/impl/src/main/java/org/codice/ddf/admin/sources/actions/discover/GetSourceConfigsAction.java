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
package org.codice.ddf.admin.sources.actions.discover;

import static org.codice.ddf.admin.sources.sample.SampleFields.SAMPLE_SOURCES_INFO_LIST;

import java.util.List;

import org.codice.ddf.admin.api.fields.Field;
import org.codice.ddf.admin.common.actions.BaseAction;
import org.codice.ddf.admin.common.fields.common.PidField;
import org.codice.ddf.admin.sources.fields.SourceInfoListField;

import com.google.common.collect.ImmutableList;

public class GetSourceConfigsAction extends BaseAction<SourceInfoListField> {

    public static final String NAME = "configs";
    public static final String DESCRIPTION = "Retrieves all currently configured sources. If a source pid is specified, only that source configuration will be returned.";
    public PidField pid;

    public GetSourceConfigsAction() {
        super(NAME, DESCRIPTION, new SourceInfoListField());
        pid = new PidField();
    }

    @Override
    public SourceInfoListField process() {
        return SAMPLE_SOURCES_INFO_LIST;
    }

    @Override
    public List<Field> getArguments() {
        return ImmutableList.of(pid);
    }
}
