package org.codice.ddf.admin.query.ldap.actions;

import static org.codice.ddf.admin.query.ldap.sample.SampleFields.SAMPLE_LDAP_CONFIGURATION;

import java.util.List;
import java.util.Map;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.commons.actions.DefaultAction;
import org.codice.ddf.admin.query.commons.fields.common.PidField;
import org.codice.ddf.admin.query.ldap.fields.LdapConfigurationsField;

import com.google.common.collect.ImmutableList;

public class LdapConfigurations extends DefaultAction<LdapConfigurationsField> {

    public static final String ACTION_ID = "configs";
    public static final String DESCRIPTION = "Retrieves all currently configured LDAP settings.";
    public static final List<Field> OPTIONAL_FIELDS = ImmutableList.of(new PidField());

    public LdapConfigurations() {
        super(ACTION_ID, DESCRIPTION, null, OPTIONAL_FIELDS, new LdapConfigurationsField());
    }

    @Override
    public LdapConfigurationsField process(Map<String, Object> args) {
        return new LdapConfigurationsField()
                .addField(SAMPLE_LDAP_CONFIGURATION)
                .addField(SAMPLE_LDAP_CONFIGURATION);
    }
}
