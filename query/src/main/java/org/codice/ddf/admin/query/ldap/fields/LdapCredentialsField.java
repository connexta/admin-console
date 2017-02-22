package org.codice.ddf.admin.query.ldap.fields;

import java.util.List;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.commons.fields.base.ObjectField;
import org.codice.ddf.admin.query.commons.fields.base.StringField;

import com.google.common.collect.ImmutableList;

public class LdapCredentialsField extends ObjectField {
    public static final String FIELD_NAME = "credentials";
    public static final String FIELD_TYPE_NAME = "LdapCredentials";
    public static final String DESCRIPTION = "Contains the required credentials to bind a user to an LDAP connection.";

    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    private StringField username;
    private StringField password;

    public LdapCredentialsField() {
        super(FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
        this.username = new StringField(USERNAME);
        this.password = new StringField(PASSWORD);
    }

    @Override
    public List<Field> getFields() {
        return ImmutableList.of(username, password);
    }
}
