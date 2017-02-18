package org.codice.ddf.admin.query.commons.fields.common;

import org.codice.ddf.admin.query.commons.fields.base.StringField;

public class HostnameField extends StringField {

    public static final String HOSTNAME = "hostname";

    public HostnameField(String fieldName) {
        super(fieldName);
    }
    public HostnameField() {
        super(HOSTNAME);
    }
}