package org.codice.ddf.admin.query.commons.message;

public class WarningMessage extends BaseMessage {

    public WarningMessage(String code, String description) {
        super(code, description, MessageType.WARNING);
    }
}
