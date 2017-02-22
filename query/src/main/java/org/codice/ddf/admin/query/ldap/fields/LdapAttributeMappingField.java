package org.codice.ddf.admin.query.ldap.fields;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.commons.fields.base.ListField;

public class LdapAttributeMappingField extends ListField<LdapAttributeEntryField> {

    public static final String DEFAULT_FIELD_NAME = "mapping";
    public static final String DESCRIPTION = "A map containing STS claims to user attributes. Only 1 sts claim is allowed to be mapped to a single user attribute.";

    public LdapAttributeMappingField() {
        super(DEFAULT_FIELD_NAME, DESCRIPTION);
    }

    @Override
    public Field getListValueField() {
        return new LdapAttributeEntryField();
    }

    public LdapAttributeMappingField addField(String stsClaim, String userAttribute) {
        addField(new LdapAttributeEntryField().stsClaim(stsClaim).userAttribute(userAttribute));
        return this;
    }

}
