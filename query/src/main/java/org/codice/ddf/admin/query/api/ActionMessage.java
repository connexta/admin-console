package org.codice.ddf.admin.query.api;

public interface ActionMessage {
    MessageType getMessageType();
    String getCode();
    String getDescription();

     enum MessageType {
         SUCCESS, FAILURE, WARNING
     }
}
