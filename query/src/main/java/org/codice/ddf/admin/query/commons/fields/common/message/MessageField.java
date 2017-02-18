package org.codice.ddf.admin.query.commons.fields.common.message;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.api.fields.Message;
import org.codice.ddf.admin.query.commons.fields.base.ObjectField;
import org.codice.ddf.admin.query.commons.fields.base.StringField;

import com.google.common.collect.ImmutableList;

public class MessageField extends ObjectField implements Message {

    public static final String FIELD_NAME = "message";
    public static final String DESCRIPTION = "A message containing a code with a summary of the message and content will a more in depth description.";
    public static final String CODE = "code";
    public static final String CONTENT = "content";
    public static final java.util.List<Field> FIELDS = ImmutableList.of(new StringField(CODE), new StringField(
            CONTENT));

    private String code;
    private String content;
    private MessageType messageType;

    public MessageField(String code, String content, MessageType messageType) {
        super(FIELD_NAME, FIELD_NAME);
        this.code = code;
        this.content = content;
        this.messageType = messageType;
    }

    @Override
    public java.util.List<Field> getFields() {
        return FIELDS;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public MessageType getMessageType() {
        return messageType;
    }

    @Override
    public String description() {
        return DESCRIPTION;
    }
}
