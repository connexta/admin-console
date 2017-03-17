package org.codice.ddf.admin.query.ldap.fields;

import org.codice.ddf.admin.query.commons.fields.base.BaseListField;

public class LdapAttributeMappingField extends BaseListField<LdapAttributeEntryField> {

    public static final String DEFAULT_FIELD_NAME = "mapping";
    public static final String DESCRIPTION = "A map containing STS claims to user attributes. Only 1 sts claim is allowed to be mapped to a single user setEnumValue.";

    public LdapAttributeMappingField() {
        super(DEFAULT_FIELD_NAME, DESCRIPTION, new LdapAttributeEntryField());
    }

    public LdapAttributeMappingField add(String stsClaim, String userAttribute) {
        add(new LdapAttributeEntryField().stsClaim(stsClaim).userAttribute(userAttribute));
        return this;
    }

}
