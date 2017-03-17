package org.codice.ddf.admin.query.ldap.actions.discover;

import static org.codice.ddf.admin.query.ldap.sample.SampleFields.SAMPLE_LDAP_ATTRIBUTE;

import java.util.List;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.commons.actions.BaseAction;
import org.codice.ddf.admin.query.ldap.fields.LdapConfigurationField;
import org.codice.ddf.admin.query.ldap.fields.query.LdapAttributeField;
import org.codice.ddf.admin.query.ldap.fields.query.LdapAttributeListField;

import com.google.common.collect.ImmutableList;

public class LdapUserAttributes extends BaseAction<LdapAttributeListField> {

    public static final String NAME = "userAttributes";
    public static final String DESCRIPTION = "Retrieves a subset of available user attributes based on the LDAP settings provided.";

    private LdapConfigurationField config = new LdapConfigurationField();

    public LdapUserAttributes() {
        super(NAME, DESCRIPTION, new LdapAttributeListField());
    }

    @Override
    public LdapAttributeListField process() {
        return new LdapAttributeListField()
                .add(SAMPLE_LDAP_ATTRIBUTE)
                .add(SAMPLE_LDAP_ATTRIBUTE);
    }

    @Override
    public List<Field> getArguments() {
        return ImmutableList.of(config);
    }
}
