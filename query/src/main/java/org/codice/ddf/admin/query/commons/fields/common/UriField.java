package org.codice.ddf.admin.query.commons.fields.common;

import org.codice.ddf.admin.query.commons.fields.base.scalar.StringField;

public class UriField extends StringField {

    public static final String DEFAULT_FIELD_NAME = "uri";
    public static final String FIELD_TYPE_NAME = "URI";
    public static final String DESCRIPTION = "Uniform Resource Identifier (URI) is a string of characters used to identify a resource.";

    public UriField(String fieldName) {
        super(fieldName, FIELD_TYPE_NAME, DESCRIPTION);
    }

    public UriField() {
        super(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
    }
}
