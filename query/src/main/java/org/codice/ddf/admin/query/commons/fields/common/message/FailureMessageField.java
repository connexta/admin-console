package org.codice.ddf.admin.query.commons.fields.common.message;

public class FailureMessageField extends BaseMessageField {

    public FailureMessageField(String code, String content) {
        super(code, content, MessageType.FAILURE);
    }
}
