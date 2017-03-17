package org.codice.ddf.admin.query.commons.fields.common;

import org.codice.ddf.admin.query.commons.fields.base.scalar.StringField;

public class ContextPath extends StringField {

    public static final String DEFAULT_FIELD_NAME = "path";
    public static final String FIELD_TYPE_NAME = "ContextPath";
    public static final String DESCRIPTION = "The context path is the prefix of a URL path that is used to select the context(s) to which an incoming request is passed. For example, http://hostname.com/<contextPath>.";

    public ContextPath() {
        super(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
    }

    @Override
    public void setValue(String value) {
        super.setValue(value);
    }
}
