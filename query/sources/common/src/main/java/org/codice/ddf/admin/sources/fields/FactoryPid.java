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

import org.codice.ddf.admin.common.fields.base.scalar.StringField;

public class FactoryPid extends StringField {
    public static final String DEFAULT_FIELD_NAME = "factoryPid";

    public static final String FIELD_TYPE_NAME = "FactoryPid";

    public static final String DESCRIPTION =
            "A unique ID used for persisting a configuration with a factory.";

    public FactoryPid() {
        this(DEFAULT_FIELD_NAME);
    }

    public FactoryPid(String fieldName) {
        super(fieldName, FIELD_TYPE_NAME, DESCRIPTION);
    }
}
