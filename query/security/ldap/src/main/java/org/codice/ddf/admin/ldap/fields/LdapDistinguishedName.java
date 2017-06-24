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

import static org.codice.ddf.admin.ldap.commons.LdapMessages.invalidDnFormatError;

import java.util.List;
import java.util.concurrent.Callable;

import org.codice.ddf.admin.api.report.ErrorMessage;
import org.codice.ddf.admin.common.fields.base.BaseListField;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;
import org.forgerock.opendj.ldap.DN;

public class LdapDistinguishedName extends StringField {
    public static final String DEFAULT_FIELD_NAME = "dn";

    public static final String FIELD_TYPE_NAME = "DistinguishedName";

    public static final String DESCRIPTION =
            "A specific position within the Directory Information Tree (DIT). For more information visit https://www.ldap.com/ldap-dns-and-rdns.";

    public LdapDistinguishedName() {
        super(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
    }

    public LdapDistinguishedName(String fieldName) {
        super(fieldName, FIELD_TYPE_NAME, DESCRIPTION);
    }

    public LdapDistinguishedName dn(String dn) {
        setValue(dn);
        return this;
    }

    @Override
    public List<ErrorMessage> validate() {
        List<ErrorMessage> validationMsgs = super.validate();
        if (!validationMsgs.isEmpty()) {
            return validationMsgs;
        }

        if (getValue() != null && !isValidDN(getValue())) {
            validationMsgs.add(invalidDnFormatError(path()));
        }

        return validationMsgs;
    }

    private boolean isValidDN(String dn) {
        try {
            DN.valueOf(dn);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static class ListImpl extends BaseListField<LdapDistinguishedName> {

        public static final String DEFAULT_FIELD_NAME = "dns";

        public ListImpl() {
            super(DEFAULT_FIELD_NAME);
        }

        public ListImpl(String fieldName) {
            super(fieldName);
        }

        @Override
        public Callable<LdapDistinguishedName> getCreateListEntryCallable() {
            return LdapDistinguishedName::new;
        }
    }
}
