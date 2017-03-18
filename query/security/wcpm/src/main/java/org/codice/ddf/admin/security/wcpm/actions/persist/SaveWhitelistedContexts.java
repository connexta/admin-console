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
package org.codice.ddf.admin.security.wcpm.actions.persist;

import static org.codice.ddf.admin.security.wcpm.sample.SampleFields.SAMPLE_CONTEXT_PATHS;

import java.util.List;

import org.codice.ddf.admin.api.fields.Field;
import org.codice.ddf.admin.common.actions.BaseAction;
import org.codice.ddf.admin.common.fields.common.ContextPaths;

import com.google.common.collect.ImmutableList;

public class SaveWhitelistedContexts extends BaseAction<ContextPaths> {

    public static final String DEFAULT_FIELD_NAME = "saveWhitelistContexts";
    public static final String DESCRIPTION = "Persists the given contexts paths as white listed contexts. White listing a context path will result in no security being applied to the given paths.";
    private ContextPaths contexts;

    public SaveWhitelistedContexts() {
        super(DEFAULT_FIELD_NAME, DESCRIPTION, new ContextPaths());
        contexts = new ContextPaths();
    }

    @Override
    public ContextPaths process() {
        return SAMPLE_CONTEXT_PATHS;
    }

    @Override
    public List<Field> getArguments() {
        return ImmutableList.of(contexts);
    }
}
