package org.codice.ddf.admin.query.api.action;

public interface Message {
    MessageType messageType();
    String code();
    String content();
    String messagePath();
    void addMessageSubPath(String subPath);

    String MESSAGE_PATH_DELIMETER = ":";
    enum MessageType {
        SUCCESS, FAILURE, WARNING
    }
}
