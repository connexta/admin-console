package org.codice.ddf.admin.query.ldap.fields;

import org.codice.ddf.admin.query.commons.fields.base.scalar.StringField;

public class LdapDistinguishedName extends StringField{
    public static final String DEFAULT_FIELD_NAME = "dn";
    public static final String FIELD_TYPE_NAME  = "DistinguishedName";

    // TODO: tbatie - 2/21/17 - Add examples of DN's here
    public static final String DESCRIPTION = "A specific position within the Directory Information Tree (DIT).";

    public LdapDistinguishedName() {
        super(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
    }

    public LdapDistinguishedName(String fieldName) {
        super(fieldName, FIELD_TYPE_NAME, DESCRIPTION);
    }
}
