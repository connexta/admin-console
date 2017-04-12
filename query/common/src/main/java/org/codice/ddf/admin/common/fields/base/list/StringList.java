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
package org.codice.ddf.admin.common.fields.base.list;

import java.util.List;

import org.codice.ddf.admin.common.fields.base.BaseListField;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;

public class StringList extends BaseListField<StringField> {
    public static final String DEFAULT_FIELD_NAME = "strings";

    public StringList() {
        super(DEFAULT_FIELD_NAME, null, new StringField(null));
    }

    public StringList(String fieldName, String description) {
        super(fieldName, description, new StringField(null));
    }

    public StringList setList(List<String> strs) {
        fields.clear();
        for(String str : strs) {
            StringField strField = new StringField(null);
            strField.setValue(str);
            fields.add(strField);
        }
        return this;
    }
}
