package org.codice.ddf.admin.query.ldap.actions.persist;

import java.util.List;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.commons.actions.BaseAction;
import org.codice.ddf.admin.query.commons.fields.common.ReportField;
import org.codice.ddf.admin.query.commons.fields.common.message.FailureMessageField;
import org.codice.ddf.admin.query.commons.fields.common.message.SuccessMessageField;
import org.codice.ddf.admin.query.ldap.fields.LdapConfigurationField;

import com.google.common.collect.ImmutableList;

public class SaveLdapConfiguration extends BaseAction<LdapConfigurationField> {

    public static final String NAME = "saveLdap";
    public static final String DESCRIPTION = "Saves the LDAP configuration.";
    private LdapConfigurationField config;

    public SaveLdapConfiguration() {
        super(NAME, DESCRIPTION, new LdapConfigurationField());
        config = new LdapConfigurationField();
    }

    // TODO: tbatie - 3/15/17 - Return back all the ldap configurations instead of a Report
    @Override
    public LdapConfigurationField process() {
        return config;
    }

    @Override
    public List<Field> getArguments() {
        return ImmutableList.of(config);
    }
}
