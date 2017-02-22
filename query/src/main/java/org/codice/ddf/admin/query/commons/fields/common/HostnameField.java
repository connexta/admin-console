package org.codice.ddf.admin.query.commons.fields.common;

import org.codice.ddf.admin.query.commons.fields.base.StringField;

public class HostnameField extends StringField {

    public static final String DEFAULT_FIELD_NAME = "hostname";
    public static final String FIELD_TYPE_NAME = "Hostname";

    public static final String DESCRIPTION =
            "Must be between 1 and 63 characters long, and the entire hostname (including the delimiting dots but not a trailing dot)"
            + " has a maximum of 253 ASCII characters.";

    public HostnameField(String fieldName) {
        super(fieldName, FIELD_TYPE_NAME, DESCRIPTION);
    }
    public HostnameField() {
        super(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
    }
}