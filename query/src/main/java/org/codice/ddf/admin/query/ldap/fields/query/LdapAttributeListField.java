package org.codice.ddf.admin.query.ldap.fields.query;

import org.codice.ddf.admin.query.commons.fields.base.BaseListField;

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
