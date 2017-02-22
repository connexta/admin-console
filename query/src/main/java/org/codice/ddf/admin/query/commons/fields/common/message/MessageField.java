package org.codice.ddf.admin.query.commons.fields.common.message;

import java.util.List;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.api.fields.Message;
import org.codice.ddf.admin.query.commons.fields.base.ObjectField;
import org.codice.ddf.admin.query.commons.fields.base.StringField;

import com.google.common.collect.ImmutableList;

public class MessageField extends ObjectField implements Message {

    public static final String FIELD_NAME = "message";
    public static final String DESCRIPTION = "A message containing a code with a summary of the message and content will a more in depth description.";
    public static final String CONTENT = "content";

    private MessageCodeField code;
    private StringField content;
    private MessageType messageType;

    public MessageField(String code, String content, MessageType messageType) {
        super(FIELD_NAME, FIELD_NAME, DESCRIPTION);
        this.code = new MessageCodeField().setValue(code);
        this.content = new StringField(CONTENT).setValue(content);
        this.messageType = messageType;
    }

    @Override
    public List<Field> getFields() {
        return ImmutableList.of(code, content);
    }

    @Override
    public String getCode() {
        return code.getValue();
    }

    @Override
    public String getContent() {
        return content.getValue();
    }

    @Override
    public MessageType getMessageType() {
        return messageType;
    }
}
