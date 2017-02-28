package org.codice.ddf.admin.query.ldap.actions.discover;

import static org.codice.ddf.admin.query.ldap.sample.SampleFields.SAMPLE_LDAP_CONFIGURATION;

import java.util.List;
import java.util.Map;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.commons.actions.BaseActionField;
import org.codice.ddf.admin.query.commons.fields.common.PidField;
import org.codice.ddf.admin.query.ldap.fields.LdapConfigurationsField;

import com.google.common.collect.ImmutableList;

public class LdapConfigurations extends BaseActionField<LdapConfigurationsField> {

    public static final String ACTION_ID = "configs";
    public static final String DESCRIPTION = "Retrieves all currently configured LDAP settings.";
    private PidField pid = new PidField();
    private List<Field> arguements = ImmutableList.of(pid);

    public LdapConfigurations() {
        super(ACTION_ID, DESCRIPTION, new LdapConfigurationsField());
    }

    @Override
    public LdapConfigurationsField process(Map<String, Object> args) {
        return new LdapConfigurationsField()
                .addField(SAMPLE_LDAP_CONFIGURATION)
                .addField(SAMPLE_LDAP_CONFIGURATION);
    }

    @Override
    public List<Field> getArguments() {
        return arguements;
    }
}
