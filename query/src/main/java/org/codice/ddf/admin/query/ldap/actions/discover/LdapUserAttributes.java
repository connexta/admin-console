package org.codice.ddf.admin.query.ldap.actions.discover;

import java.util.List;
import java.util.Map;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.commons.actions.BaseActionField;
import org.codice.ddf.admin.query.ldap.fields.LdapConfigurationField;
import org.codice.ddf.admin.query.ldap.fields.query.LdapAttributeField;
import org.codice.ddf.admin.query.ldap.fields.query.LdapAttributeListField;

import com.google.common.collect.ImmutableList;

public class LdapUserAttributes extends BaseActionField<LdapAttributeListField> {

    public static final String FIELD_NAME = "userAttributes";
    public static final String DESCRIPTION = "Retrieves a subset of available user attributes based on the LDAP settings provided.";

    private LdapConfigurationField config = new LdapConfigurationField();
    private List<Field> arguments = ImmutableList.of(config);

    public LdapUserAttributes() {
        super(FIELD_NAME, DESCRIPTION, new LdapAttributeListField());
    }

    @Override
    public LdapAttributeListField process(Map<String, Object> args) {
        return new LdapAttributeListField()
                .addField(new LdapAttributeField().setValue("exampleAttri"))
                .addField(new LdapAttributeField().setValue("exampleAttri2"));
    }

    @Override
    public List<Field> getArguments() {
        return arguments;
    }
}
