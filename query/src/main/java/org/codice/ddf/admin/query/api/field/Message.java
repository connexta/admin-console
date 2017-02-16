package org.codice.ddf.admin.query.api.field;

public interface Message extends Field{
    String MESSAGE = "Message";
    String CODE = "code";
    String DESCRIPTION = "description";

    String getCode();
    String description();
    MessageType getMessageType();

    enum MessageType {
        SUCCESS, FAILURE, WARNING
    }
}
