package org.codice.ddf.admin.query.commons.message;

public class SuccessMessage extends BaseMessage{
    public SuccessMessage(String code, String description) {
        super(code, description, MessageType.SUCCESS);
    }
}
