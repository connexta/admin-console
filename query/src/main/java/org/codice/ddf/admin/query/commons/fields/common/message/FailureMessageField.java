package org.codice.ddf.admin.query.commons.fields.common.message;

public class FailureMessageField extends MessageField{

    public FailureMessageField(String code, String content) {
        super(code, content, MessageType.FAILURE);
    }
}
