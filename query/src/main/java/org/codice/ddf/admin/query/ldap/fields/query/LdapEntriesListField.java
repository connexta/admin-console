package org.codice.ddf.admin.query.ldap.fields.query;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.commons.fields.base.ListField;

public class LdapEntriesListField extends ListField<LdapEntryField> {

    public static final String DEFAULT_FIELD_NAME = "entries";
    public static final String DESCRIPTION = "A list of LDAP entries.";

    public LdapEntriesListField() {
        super(DEFAULT_FIELD_NAME, DESCRIPTION);
    }

    @Override
    public Field getListValueField() {
        return new LdapEntryField();
    }

    @Override
    public LdapEntriesListField addField(LdapEntryField field) {
        super.addField(field);
        return this;
    }
}
