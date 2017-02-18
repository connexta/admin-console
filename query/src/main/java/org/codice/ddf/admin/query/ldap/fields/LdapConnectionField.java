package org.codice.ddf.admin.query.ldap.fields;

import java.util.List;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.commons.fields.base.ObjectField;
import org.codice.ddf.admin.query.commons.fields.common.HostnameField;
import org.codice.ddf.admin.query.commons.fields.common.PortField;

import com.google.common.collect.ImmutableList;

public class LdapConnectionField extends ObjectField {
    public static final String FIELD_NAME = "connection";
    public static final String FIELD_TYPE_NAME = "LdapConnection";
    public static final List<Field> FIELDS = ImmutableList.of(new HostnameField(), new PortField(), new LdapEncryptionMethodField());
    public LdapConnectionField() {
        super(FIELD_NAME, FIELD_TYPE_NAME);
    }

    @Override
    public String description() {
        return "Contains the required information to establish an LDAP connection.";
    }

    @Override
    public List<Field> getFields() {
        return FIELDS;
    }
}
