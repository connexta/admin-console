package org.codice.ddf.admin.query.ldap.fields;

import org.codice.ddf.admin.query.commons.fields.base.BaseListField;

public class LdapConfigurationsField extends BaseListField<LdapConfigurationField> {

    public static final String DEFAULT_FIELD_NAME = "configs";
    public static final String DESCRIPTION = "A list of LDAP configurations.";

    public LdapConfigurationsField() {
        super(DEFAULT_FIELD_NAME, DESCRIPTION, new LdapConfigurationField());
    }

    @Override
    public LdapConfigurationsField add(LdapConfigurationField value) {
        super.add(value);
        return this;
    }
}
