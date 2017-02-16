package org.codice.ddf.admin.query.commons;

import org.codice.ddf.admin.query.commons.field.BaseFields;

public class CommonFields {

    public static class HostnameField extends BaseFields.StringField {

        public static final String HOSTNAME = "hostname";

        public HostnameField(String fieldName) {
            super(fieldName);
        }
        public HostnameField() {
            super(HOSTNAME);
        }
    }

    public static class PortField extends BaseFields.IntegerField {

        public static final String PORT = "port";

        public PortField(String fieldName) {
            super(fieldName);
        }

        public PortField() {
            super(PORT);
        }

    }
}
