package org.codice.ddf.admin.query.ldap.actions.discover;

import static org.codice.ddf.admin.query.ldap.sample.SampleFields.SAMPLE_LDAP_CONFIGURATION;

import java.util.List;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.commons.actions.BaseAction;
import org.codice.ddf.admin.query.commons.fields.common.PidField;
import org.codice.ddf.admin.query.ldap.fields.LdapConfigurationsField;

import com.google.common.collect.ImmutableList;

public class LdapConfigurations extends BaseAction<LdapConfigurationsField> {

    public static final String NAME = "configs";
    public static final String DESCRIPTION = "Retrieves all currently configured LDAP settings.";

    private PidField pid = new PidField();
    private List<Field> arguments = ImmutableList.of(pid);

    public LdapConfigurations() {
        super(NAME, DESCRIPTION, new LdapConfigurationsField());
    }

    @Override
    public LdapConfigurationsField process() {
        return new LdapConfigurationsField()
                .add(SAMPLE_LDAP_CONFIGURATION)
                .add(SAMPLE_LDAP_CONFIGURATION);
    }

    @Override
    public List<Field> getArguments() {
        return arguments;
    }
}
