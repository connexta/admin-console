package org.codice.ddf.admin.query.commons.fields.common;

import org.codice.ddf.admin.query.commons.fields.base.IntegerField;

public class PortField extends IntegerField {

    public static final String DEFAULT_FIELD_NAME = "port";
    public static final String FIELD_TYPE_NAME  = "Port";
    public static final String DESCRIPTION = "Port range within the bounds of 0 - 65535";

    public PortField(String fieldName) {
        super(fieldName, FIELD_TYPE_NAME, DESCRIPTION);
    }

    public PortField() {
        super(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
    }

}
