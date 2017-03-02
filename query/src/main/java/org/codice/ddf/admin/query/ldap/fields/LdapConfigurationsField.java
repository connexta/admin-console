package org.codice.ddf.admin.query.ldap.fields;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.commons.fields.base.BaseListField;

public class LdapConfigurationsField extends BaseListField<LdapConfigurationField> {

    public static final String DEFAULT_FIELD_NAME = "configs";
    public static final String DESCRIPTION = "A list of LDAP configurations.";

    public LdapConfigurationsField() {
        super(DEFAULT_FIELD_NAME, DESCRIPTION);
    }

    @Override
    public LdapConfigurationsField addField(LdapConfigurationField value) {
        super.addField(value);
        return this;
    }

    @Override
    public Field getListValueField() {
        return new LdapConfigurationField();
    }
}
