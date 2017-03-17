package org.codice.ddf.admin.query.commons.fields.common.message;

import org.codice.ddf.admin.query.api.action.Message;

public class FailureMessageField extends MessageField {

    public FailureMessageField(String code, String content) {
        super(code, content, Message.MessageType.FAILURE);
    }
}
