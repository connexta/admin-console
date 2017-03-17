package org.codice.ddf.admin.query.commons.fields.common.message;

import java.util.List;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.api.action.Message;
import org.codice.ddf.admin.query.commons.fields.base.BaseObjectField;
import org.codice.ddf.admin.query.commons.fields.base.scalar.StringField;

import com.google.common.collect.ImmutableList;


public class MessageField extends BaseObjectField implements Message {

    public static final String FIELD_NAME = "message";
    public static final String DESCRIPTION = "A message containing a code with a summary of the message and content will a more in depth description.";

    private MessageType messageType;
    private MessageCodeField code;
    private StringField content;
    private String messagePath;

    public MessageField(MessageType messageType) {
        super(FIELD_NAME, FIELD_NAME, DESCRIPTION);
        this.messageType = messageType;
        this.code = new MessageCodeField();
        this.content = new StringField("content");
    }

    public MessageField(String code, String content, MessageType messageType) {
        this(messageType);
        this.code.setValue(code);
        this.content.setValue(content);
        this.messageType = messageType;
    }

    @Override
    public List<Field> getFields() {
        return ImmutableList.of(code, content);
    }

    @Override
    public String code() {
        return code.getValue();
    }

    @Override
    public String content() {
        return content.getValue();
    }

    @Override
    public MessageType messageType() {
        return messageType;
    }

    @Override
    public List<Message> validate() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String messagePath() {
        return messagePath;
    }

    @Override
    public void addMessageSubPath(String subPath) {
        messagePath = subPath + MESSAGE_PATH_DELIMETER + messagePath;
    }
}
