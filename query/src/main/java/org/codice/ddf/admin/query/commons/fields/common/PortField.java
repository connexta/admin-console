package org.codice.ddf.admin.query.commons.fields.common;

import org.codice.ddf.admin.query.commons.fields.base.IntegerField;

public class PortField extends IntegerField {

    public static final String PORT = "port";

    public PortField(String fieldName) {
        super(fieldName);
    }

    public PortField() {
        super(PORT);
    }

}
