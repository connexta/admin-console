package org.codice.ddf.admin.query.ldap.fields;

import java.util.List;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.commons.fields.base.ObjectField;
import org.codice.ddf.admin.query.commons.fields.common.PidField;

import com.google.common.collect.ImmutableList;

public class LdapConfigurationField extends ObjectField {

    public static final String FIELD_NAME = "config";
    public static final String FIELD_TYPE_NAME = "LdapConfiguration";
    public static final String DESCRIPTION = "A configuration containing all the required fields for saving LDAP settings";
    private PidField pid;
    private LdapConnectionField connection;
    private LdapCredentialsField credentials;
    private LdapSettingsField settings;

    public LdapConfigurationField() {
        super(FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
        this.pid = new PidField();
        this.connection = new LdapConnectionField();
        this.credentials = new LdapCredentialsField();
        this.settings = new LdapSettingsField();
    }


    public LdapConfigurationField connection(LdapConnectionField connection) {
        this. connection = connection;
        return this;
    }

    public LdapConfigurationField credentials(LdapCredentialsField credentials) {
        this.credentials = credentials;
        return this;
    }

    public LdapConfigurationField settings(LdapSettingsField settings) {
        this.settings = settings;
        return this;
    }

    @Override
    public List<Field> getFields() {
        return ImmutableList.of(pid, connection, credentials, settings);
    }
}
