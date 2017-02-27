package org.codice.ddf.admin.query.api.fields;

public interface MessageField extends Field{
    String getCode();
    String getContent();
    MessageType getMessageType();

    enum MessageType {
        SUCCESS, FAILURE, WARNING
    }
}
