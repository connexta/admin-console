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
package org.codice.ddf.admin.ldap.actions.discover;

import java.util.List;

import org.codice.ddf.admin.api.fields.Field;
import org.codice.ddf.admin.common.actions.BaseAction;
import org.codice.ddf.admin.security.common.fields.ldap.LdapDistinguishedName;
import org.codice.ddf.admin.security.common.fields.ldap.query.LdapAttributeField;
import org.codice.ddf.admin.security.common.fields.ldap.query.LdapEntriesListField;
import org.codice.ddf.admin.security.common.fields.ldap.query.LdapEntryField;
import org.codice.ddf.admin.security.common.fields.ldap.query.LdapQueryField;

import com.google.common.collect.ImmutableList;

public class LdapQuery extends BaseAction<LdapEntriesListField> {

    public static final String NAME = "query";
    public static final String DESCRIPTION = "Executes a query against LDAP.";

    private LdapDistinguishedName dn =  new LdapDistinguishedName();
    private LdapQueryField query = new LdapQueryField();
    private List<Field> arguments = ImmutableList.of(dn, query);

    public LdapQuery() {
        super(NAME, DESCRIPTION, new LdapEntriesListField());
    }

    @Override
    public LdapEntriesListField process() {
        LdapAttributeField attri = new LdapAttributeField();
        attri.setValue("exampleAttri");
        LdapEntryField entry = new LdapEntryField().addAttribute(attri);
        LdapEntryField outterEntry = new LdapEntryField().addAttribute(attri)
                .addEntry(entry);
        return new LdapEntriesListField().add(outterEntry);
    }

    @Override
    public List<Field> getArguments() {
        return arguments;
    }
}
