package org.codice.ddf.admin.query.commons.message;

public class FailureMessage extends BaseMessage{

    public FailureMessage(String code, String description) {
        super(code, description, MessageType.FAILURE);
    }
}
