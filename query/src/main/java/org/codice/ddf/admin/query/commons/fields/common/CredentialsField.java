package org.codice.ddf.admin.query.commons.fields.common;

import java.util.List;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.commons.fields.base.BaseObjectField;
import org.codice.ddf.admin.query.commons.fields.base.scalar.StringField;

import com.google.common.collect.ImmutableList;

public class CredentialsField extends BaseObjectField {

    public static final String DEFAULT_FIELD_NAME = "creds";
    public static final String FIELD_TYPE_NAME  = "Credentials";
    public static final String DESCRIPTION = "Credentials required for base64 authentication.";

    private StringField hostname;
    private StringField password;

    public CredentialsField() {
        super(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
        this.hostname = new StringField("username");
        this.password = new StringField("password");
    }

    @Override
    public List<Field> getFields() {
        return ImmutableList.of(hostname, password);
    }
}
