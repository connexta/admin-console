package org.codice.ddf.admin.query.commons.fields.common;

import org.codice.ddf.admin.query.commons.fields.base.scalar.StringField;

public class UrlField extends StringField {

    public static final String DEFAULT_FIELD_NAME = "url";
    public static final String FIELD_TYPE_NAME = "URL";
    public static final String DESCRIPTION = "An address that identifies a particular file on the Internet, usually consisting of the protocol, as http, followed by the domain name.";

    public UrlField(String fieldName) {
        super(fieldName, FIELD_TYPE_NAME, DESCRIPTION);
    }

    public UrlField() {
        super(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
    }
}
