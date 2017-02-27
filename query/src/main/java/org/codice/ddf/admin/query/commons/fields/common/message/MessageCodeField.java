package org.codice.ddf.admin.query.commons.fields.common.message;

import org.codice.ddf.admin.query.commons.fields.base.scalar.StringField;

public class MessageCodeField extends StringField{
    public static final String DEFAULT_FIELD_NAME = "code";
    public static final String FIELD_TYPE_NAME = "MessageCode";
    public static final String DESCRIPTION = "An encapsulating description of what the message means.";

    public MessageCodeField() {
        super(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
    }

    @Override
    public MessageCodeField setValue(String value) {
        super.setValue(value);
        return this;
    }
}
