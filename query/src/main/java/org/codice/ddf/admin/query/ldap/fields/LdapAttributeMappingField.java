package org.codice.ddf.admin.query.ldap.fields;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.commons.fields.base.ListField;

public class LdapAttributeMappingField extends ListField {

    public static final String FIELD_NAME = "mapping";
    public static final String DESCRIPTION = "A map containing STS claims to user attributes. Only 1 sts claim is allowed to be mapped to a single user attribute.";
    public LdapAttributeMappingField() {
        super(FIELD_NAME);
    }

    @Override
    public String description() {
        return DESCRIPTION;
    }

    @Override
    public Field getListValueField() {
        return new LdapAttributeEntryField();
    }
}
