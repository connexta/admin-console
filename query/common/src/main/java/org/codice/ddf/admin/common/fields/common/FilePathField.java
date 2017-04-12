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
package org.codice.ddf.admin.common.fields.common;

import org.codice.ddf.admin.common.fields.base.scalar.StringField;

public class FilePathField extends StringField {
    public static final String DEFAULT_FIELD_NAME = "filePath";

    public static final String FIELD_TYPE_NAME = "FilePath";

    // TODO: tbatie - 4/3/17 - Enforce file path must be relative to ddf home?
    public static final String DESCRIPTION = "Specifies a unique location in a file system.";

    public FilePathField() {
        this(DEFAULT_FIELD_NAME);
    }

    public FilePathField(String fieldName) {
        super(fieldName, FIELD_TYPE_NAME, DESCRIPTION);
    }

    @Override
    public FilePathField isRequired(boolean required) {
        super.isRequired(required);
        return this;
    }
}
