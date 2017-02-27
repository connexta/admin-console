package org.codice.ddf.admin.query.sources.common;

import java.util.List;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.commons.fields.base.BaseObjectField;
import org.codice.ddf.admin.query.commons.fields.base.scalar.StringField;
import org.codice.ddf.admin.query.commons.fields.common.CredentialsField;
import org.codice.ddf.admin.query.commons.fields.common.PidField;
import org.codice.ddf.admin.query.commons.fields.common.UriField;

import com.google.common.collect.ImmutableList;

public class SourceConfigurationField extends BaseObjectField {

    public static final String DEFAULT_FIELD_NAME = "config";
    public static final String FIELD_TYPE_NAME = "SourceConfiguration";
    public static final String DESCRIPTION = "A configuration containing the base information of a source to be persisted";

    protected PidField id;
    protected StringField name;
    protected UriField endpointUrl;
    protected CredentialsField creds;

    public SourceConfigurationField() {
        super(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
        id = new PidField();
        name = new StringField("name");
        endpointUrl = new UriField("endpointUrl");
        creds = new CredentialsField();
    }

    protected SourceConfigurationField(String fieldName, String fieldTypeName, String description) {
        super(fieldName, fieldTypeName, description, new SourceConfigurationField());
        id = new PidField();
        name = new StringField("name");
        endpointUrl = new UriField("endpointUrl");
        creds = new CredentialsField();
    }

    @Override
    public List<Field> getFields() {
        return ImmutableList.of(id, name, endpointUrl, creds);
    }
}
