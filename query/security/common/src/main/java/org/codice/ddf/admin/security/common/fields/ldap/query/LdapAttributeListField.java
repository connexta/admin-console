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
package org.codice.ddf.admin.security.common.fields.ldap.query;

import org.codice.ddf.admin.common.fields.base.BaseListField;

public class LdapAttributeListField extends BaseListField<LdapAttributeField> {

    public static final String DEFAULT_FIELD_NAME = "attributes";

    public static final String DESCRIPTION = "A list of attributes an LDAP entry contains.";

    public LdapAttributeListField() {
        super(DEFAULT_FIELD_NAME, DESCRIPTION, new LdapAttributeField());
    }

    @Override
    public LdapAttributeListField add(LdapAttributeField value) {
        super.add(value);
        return this;
    }
}
