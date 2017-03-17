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
package org.codice.ddf.admin.query.ldap.fields.query;

import java.util.List;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.commons.fields.base.BaseObjectField;

import com.google.common.collect.ImmutableList;

public class LdapEntryField extends BaseObjectField {

    public static final String DEFAULT_FIELD_NAME = "entry";
    public static final String FIELD_TYPE_NAME = "LdapEntry";
    public static final String DESCRIPTION = "An entry within an LDAP server.";

    // TODO: tbatie - 2/22/17 - Can't handle recursion
//    private LdapEntriesListField entries;
    private LdapAttributeListField attributes;

    public LdapEntryField() {
        super(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
//        entries = new LdapEntriesListField();
        attributes = new LdapAttributeListField();
    }

    @Override
    public List<Field> getFields() {
        return ImmutableList.of(
//                entries,
                attributes);
    }

    public LdapEntryField addEntry(LdapEntryField entry){
//        entries.add(entry);
        return this;
    }

    public LdapEntryField addAttribute(LdapAttributeField attribute) {
        attributes.add(attribute);
        return this;
    }
}
