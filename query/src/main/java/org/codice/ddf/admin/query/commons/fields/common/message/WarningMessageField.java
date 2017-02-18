package org.codice.ddf.admin.query.commons.fields.common.message;

public class WarningMessageField extends MessageField{

    public WarningMessageField(String code, String content) {
        super(code, content, MessageType.WARNING);
    }

}
