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

import java.util.List;

import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.api.fields.ListField;
import org.codice.ddf.admin.common.fields.base.BaseObjectField;
import org.codice.ddf.admin.common.fields.base.ListFieldImpl;

import com.google.common.collect.ImmutableList;

public class MapField extends BaseObjectField {

    public static final String DEFAULT_FIELD_NAME = "map";

    public static final String FIELD_TYPE_NAME = "Map";

    public static final String DESCRIPTION = "A map containing a list of key value pairs.";

    public static final String ENTRIES_FIELD_NAME = "entries";

    private ListField<PairField> entries;

    public MapField() {
        super(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
        entries = new ListFieldImpl<>(ENTRIES_FIELD_NAME, PairField.class);
        updateInnerFieldPaths();
    }

    @Override
    public List<Field> getFields() {
        return ImmutableList.of(entries);
    }

    public MapField put(String key, String value) {
        entries.add(new PairField().key(key).value(value));
        return this;
    }
}
