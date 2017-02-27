package org.codice.ddf.admin.query.ldap.fields.query;

import org.codice.ddf.admin.query.commons.fields.base.scalar.StringField;

public class LdapAttributeField extends StringField {

    public static final String DEFAULT_FIELD_NAME = "setValue";
    public static final String FIELD_TYPE_NAME = "LdapEntryAttribute";
    public static final String DESCRIPTION = "A particular setValue an LDAP entry contains.";

    public LdapAttributeField() {
        super(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
    }

    @Override
    public LdapAttributeField setValue(String attribute) {
        super.setValue(attribute);
        return this;
    }
}
