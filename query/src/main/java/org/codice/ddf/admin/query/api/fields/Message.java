package org.codice.ddf.admin.query.api.fields;

public interface Message extends Field{
    String getCode();
    String getContent();
    MessageType getMessageType();

    enum MessageType {
        SUCCESS, FAILURE, WARNING
    }
}
