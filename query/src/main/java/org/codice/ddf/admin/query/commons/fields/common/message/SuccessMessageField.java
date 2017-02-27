package org.codice.ddf.admin.query.commons.fields.common.message;

public class SuccessMessageField extends BaseMessageField {

    public SuccessMessageField(String code, String content) {
        super(code, content, MessageType.SUCCESS);
    }
}
