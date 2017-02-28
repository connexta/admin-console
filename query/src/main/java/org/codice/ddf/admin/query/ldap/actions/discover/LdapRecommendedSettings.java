package org.codice.ddf.admin.query.ldap.actions.discover;

import static org.codice.ddf.admin.query.ldap.sample.SampleFields.SAMPLE_LDAP_SETTINGS;

import java.util.List;
import java.util.Map;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.commons.actions.BaseActionField;
import org.codice.ddf.admin.query.ldap.fields.LdapConnectionField;
import org.codice.ddf.admin.query.ldap.fields.LdapCredentialsField;
import org.codice.ddf.admin.query.ldap.fields.LdapSettingsField;

import com.google.common.collect.ImmutableList;

public class LdapRecommendedSettings extends BaseActionField<LdapSettingsField> {

    public static final String FIELD_NAME = "recommendedSettings";
    public static final String DESCRIPTION = "Attempts to retrieve recommended settings from the LDAP connection.";

    private LdapConnectionField connection = new LdapConnectionField();
    private LdapCredentialsField credentials = new LdapCredentialsField();
    private List<Field> arguments = ImmutableList.of(connection, credentials);

    public LdapRecommendedSettings() {
        super(FIELD_NAME, DESCRIPTION, new LdapSettingsField());
    }

    @Override
    public LdapSettingsField process(Map<String, Object> args) {
        return SAMPLE_LDAP_SETTINGS;
    }

    @Override
    public List<Field> getArguments() {
        return arguments;
    }
}
