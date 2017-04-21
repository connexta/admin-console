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
package org.codice.ddf.admin.ldap.fields;

import java.util.List;

import org.codice.ddf.admin.api.action.Message;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;
import org.codice.ddf.admin.ldap.actions.commons.LdapMessages;
import org.forgerock.opendj.ldap.DN;

public class LdapDistinguishedName extends StringField {
    public static final String DEFAULT_FIELD_NAME = "dn";

    public static final String FIELD_TYPE_NAME = "DistinguishedName";

    // TODO: tbatie - 2/21/17 - Add examples of DN's here
    public static final String DESCRIPTION =
            "A specific position within the Directory Information Tree (DIT).";

    public LdapDistinguishedName() {
        super(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
    }

    public LdapDistinguishedName(String fieldName) {
        super(fieldName, FIELD_TYPE_NAME, DESCRIPTION);
    }

    @Override
    public List<Message> validate() {
        List<Message> validationMsgs = super.validate();
        if(!validationMsgs.isEmpty()) {
            return validationMsgs;
        }

        if(getValue() != null && !isValidDN(getValue())) {
            validationMsgs.add(LdapMessages.invalidDnFormatError(fieldName()));
        }

        return validationMsgs;
    }

    public boolean isValidDN(String dn) {
        try {
            DN.valueOf(dn);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
