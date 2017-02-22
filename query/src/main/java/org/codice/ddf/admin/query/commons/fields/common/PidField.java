package org.codice.ddf.admin.query.commons.fields.common;

import org.codice.ddf.admin.query.commons.fields.base.StringField;

public class PidField extends StringField {
    public static final String DEFAULT_FIELD_NAME = "pid";
    public static final String FIELD_TYPE_NAME = "Pid";
    public static final String DESCRIPTION = "A unique id used for persisting.";

    public PidField() {
        super(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
    }

    public PidField(String fieldName) {
        super(fieldName, FIELD_TYPE_NAME, DESCRIPTION);
    }
}
