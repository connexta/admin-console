package org.codice.ddf.admin.query.commons.message;

import org.codice.ddf.admin.query.api.ActionMessage;

public abstract class BaseMessage implements ActionMessage {
    private String code;
    private String description;
    private ActionMessage.MessageType messageType;

    public BaseMessage(String code, String description, ActionMessage.MessageType messageType) {
        this.code = code;
        this.description = description;
        this.messageType = messageType;
    }

    @Override
    public ActionMessage.MessageType getMessageType() {
        return messageType;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
