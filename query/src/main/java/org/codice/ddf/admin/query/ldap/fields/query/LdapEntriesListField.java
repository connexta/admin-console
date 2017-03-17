package org.codice.ddf.admin.query.ldap.fields.query;

import org.codice.ddf.admin.query.commons.fields.base.BaseListField;

public class LdapEntriesListField extends BaseListField<LdapEntryField> {

    public static final String DEFAULT_FIELD_NAME = "entries";
    public static final String DESCRIPTION = "A list of LDAP entries.";

    public LdapEntriesListField() {
        super(DEFAULT_FIELD_NAME, DESCRIPTION, new LdapEntryField());
    }

    @Override
    public LdapEntriesListField add(LdapEntryField field) {
        super.add(field);
        return this;
    }
}
