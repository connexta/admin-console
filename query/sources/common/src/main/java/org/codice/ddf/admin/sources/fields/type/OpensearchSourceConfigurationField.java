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
package org.codice.ddf.admin.sources.fields.type;

import java.util.List;

import org.codice.ddf.admin.api.fields.Field;

public class OpensearchSourceConfigurationField extends SourceConfigUnionField {

    private static final String FIELD_TYPE_NAME = "OpenSearchConfiguration";
    private static final String DESCRIPTION = "Example opensearch config description";

    public OpensearchSourceConfigurationField() {
        super(FIELD_TYPE_NAME, DESCRIPTION);
    }
    @Override
    public List<Field> getFields() {
        // TODO: tbatie - 2/28/17 - Add additional fields once implemented
        return super.getFields();
    }
}
