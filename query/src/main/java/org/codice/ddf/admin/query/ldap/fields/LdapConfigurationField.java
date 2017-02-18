package org.codice.ddf.admin.query.ldap.fields;

import java.util.List;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.commons.fields.base.ObjectField;
import org.codice.ddf.admin.query.commons.fields.common.PidField;

import com.google.common.collect.ImmutableList;

public class LdapConfigurationField extends ObjectField {

    public static final String FIELD_NAME = "config";
    public static final String FIELD_TYPE_NAME = "LdapConfiguration";
    public static final List<Field> FIELDS = ImmutableList.of(new PidField(),
            new LdapConnectionField(),
            new LdapCredentialsField(),
            new LdapSettingsField());

    public LdapConfigurationField() {
        super(FIELD_NAME, FIELD_TYPE_NAME);
    }

    @Override
    public String description() {
        return "A configuration containing all the required fields for saving LDAP settings";
    }

    @Override
    public List<Field> getFields() {
        return FIELDS;
    }
}
