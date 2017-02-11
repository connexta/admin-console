package org.codice.ddf.admin.query.api;

public interface ActionMessage {

    String MESSAGE = "message";
    String CODE = "code";
    String DESCRIPTION = "description";

    String getCode();
    String getDescription();
    MessageType getMessageType();

    enum MessageType {
         SUCCESS, FAILURE, WARNING
     }
}
