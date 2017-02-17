package org.codice.ddf.admin.query.api.field;

public interface Message extends Field{
    String getCode();
    String getContent();
    MessageType getMessageType();

    enum MessageType {
        SUCCESS, FAILURE, WARNING
    }
}
