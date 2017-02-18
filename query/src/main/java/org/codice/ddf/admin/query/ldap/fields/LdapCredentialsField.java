package org.codice.ddf.admin.query.ldap.fields;

import java.util.List;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.commons.fields.base.ObjectField;
import org.codice.ddf.admin.query.commons.fields.base.StringField;

import com.google.common.collect.ImmutableList;

public class LdapCredentialsField extends ObjectField {
    public static final String FIELD_NAME = "credentials";
    public static final String FIELD_TYPE_NAME = "LdapCredentials";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";

    public static final List<Field> FIELDS = ImmutableList.of(new StringField(USERNAME), new StringField(PASSWORD));

    public LdapCredentialsField() {
        super(FIELD_NAME, FIELD_TYPE_NAME);
    }

    @Override
    public String description() {
        return "Contains the required credentials to bind a user to an LDAP connection.";
    }

    @Override
    public List<Field> getFields() {
        return FIELDS;
    }
}
