package org.codice.ddf.admin.query.ldap.fields.query;

import org.codice.ddf.admin.query.commons.fields.base.StringField;

public class LdapQueryField extends StringField {
    public static final String DEFAULT_FIELD_NAME = "query";
    public static final String FIELD_TYPE_NAME = "LdapQuery";
    public static final String DESCRIPTION = "A Search filters that enables you to define search criteria. Ex: (objectClass=*). LDAP query syntax can be found at: https://msdn.microsoft.com/en-us/library/aa746475(v=vs.85).aspx";

    public LdapQueryField() {
        super(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
    }
}
