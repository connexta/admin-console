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
package org.codice.ddf.admin.query.wcpm.actions.persist;

import static org.codice.ddf.admin.query.wcpm.sample.SampleFields.SAMPLE_CONTEXT_POLICES;

import java.util.List;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.commons.actions.BaseAction;
import org.codice.ddf.admin.query.wcpm.fields.ContextPolicies;

import com.google.common.collect.ImmutableList;

public class SaveContextPolices extends BaseAction<ContextPolicies> {

    public static final String DEFAULT_FIELD_NAME = "saveContextPolicies";
    public static final String DESCRIPTION = "Saves a list of policies to be applied to their corresponding context paths.";
    private ContextPolicies contextPolicies;

    public SaveContextPolices() {
        super(DEFAULT_FIELD_NAME, DESCRIPTION, new ContextPolicies());
        contextPolicies = new ContextPolicies();
    }

    @Override
    public ContextPolicies process() {
        return SAMPLE_CONTEXT_POLICES;
    }

    @Override
    public List<Field> getArguments() {
        return ImmutableList.of(contextPolicies);
    }
}
