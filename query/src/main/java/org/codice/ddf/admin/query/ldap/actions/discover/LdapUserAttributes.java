package org.codice.ddf.admin.query.ldap.actions.discover;

import java.util.List;
import java.util.Map;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.commons.actions.DefaultAction;
import org.codice.ddf.admin.query.ldap.fields.LdapConfigurationField;
import org.codice.ddf.admin.query.ldap.fields.query.LdapAttributeField;
import org.codice.ddf.admin.query.ldap.fields.query.LdapAttributeListField;

import com.google.common.collect.ImmutableList;

public class LdapUserAttributes extends DefaultAction<LdapAttributeListField> {

    public static final String ACTION_ID = "userAttributes";
    public static final String DESCRIPTION = "Retrieves a subset of available user attributes based on the LDAP settings provided.";
    public static final List<Field> REQUIRED_FIELDS = ImmutableList.of(new LdapConfigurationField());

    public LdapUserAttributes() {
        super(ACTION_ID, DESCRIPTION, REQUIRED_FIELDS, null, new LdapAttributeListField());
    }

    @Override
    public LdapAttributeListField process(Map<String, Object> args) {
        return new LdapAttributeListField()
                .addField(new LdapAttributeField().setValue("exampleAttri"))
                .addField(new LdapAttributeField().setValue("exampleAttri2"));
    }
}
