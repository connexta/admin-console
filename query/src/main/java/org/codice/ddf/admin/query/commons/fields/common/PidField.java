package org.codice.ddf.admin.query.commons.fields.common;

import org.codice.ddf.admin.query.commons.fields.base.StringField;

public class PidField extends StringField {
    public static final String PID = "pid";

    public PidField() {
        super(PID);
    }

    public PidField(String fieldName) {
        super(fieldName);
    }

    @Override
    public String description() {
        return "A unique id used for persisting.";
    }
}
