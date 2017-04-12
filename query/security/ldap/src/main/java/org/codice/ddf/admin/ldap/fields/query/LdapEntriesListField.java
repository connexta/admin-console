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
package org.codice.ddf.admin.ldap.fields.query;

import java.util.List;

import org.codice.ddf.admin.common.fields.base.BaseListField;
import org.codice.ddf.admin.common.fields.common.MapField;

public class LdapEntriesListField extends BaseListField<MapField> {

    public static final String DEFAULT_FIELD_NAME = "entries";

    public static final String DESCRIPTION = "A list of LDAP entries containing attributes.";

    public LdapEntriesListField() {
        super(DEFAULT_FIELD_NAME, DESCRIPTION, new MapField());
    }

    public LdapEntriesListField addAll(List<MapField> entries) {
        entries.stream().forEach(entry -> add(entry));
        return this;
    }
}
