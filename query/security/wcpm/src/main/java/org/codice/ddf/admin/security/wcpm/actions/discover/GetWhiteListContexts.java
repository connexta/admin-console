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
package org.codice.ddf.admin.security.wcpm.actions.discover;

import static org.codice.ddf.admin.security.wcpm.sample.SampleFields.SAMPLE_CONTEXT_PATHS;

import org.codice.ddf.admin.common.actions.GetAction;
import org.codice.ddf.admin.common.fields.common.ContextPaths;

public class GetWhiteListContexts extends GetAction<ContextPaths> {

    public static final String DEFAULT_FIELD_NAME = "whitelisted";

    public static final String DESCRIPTION =
            "Returns all white listed contexts. Any contexts that are white listed have no security policy applied to them.";

    public GetWhiteListContexts() {
        super(DEFAULT_FIELD_NAME, DESCRIPTION, new ContextPaths());
    }

    @Override
    public ContextPaths performAction() {
        return SAMPLE_CONTEXT_PATHS;
    }
}
