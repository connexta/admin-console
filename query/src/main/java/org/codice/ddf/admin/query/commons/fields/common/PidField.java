package org.codice.ddf.admin.query.commons.fields.common;

import org.codice.ddf.admin.query.commons.fields.base.StringField;

public class PidField extends StringField {
    public static final String PID = "pid";
    public static final String DESCRIPTION = "A unique id used for persisting.";

    public PidField() {
        super(PID, DESCRIPTION);
    }

    public PidField(String fieldName) {
        super(fieldName, DESCRIPTION);
    }
}
