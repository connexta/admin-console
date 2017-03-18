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
package org.codice.ddf.admin.sources.fields;

import org.codice.ddf.admin.common.fields.base.BaseListField;

public class SourceInfoListField extends BaseListField<SourceInfoField> {

    public static final String DEFAULT_FIELD_NAME = "sourceConfigs";
    public static final String DESCRIPTION = "A list of source configurations";

    public SourceInfoListField() {
        super(DEFAULT_FIELD_NAME, DESCRIPTION, new SourceInfoField());
    }

    @Override
    public SourceInfoListField add(SourceInfoField value) {
        super.add(value);
        return this;
    }
}
