package org.codice.ddf.admin.query.commons.fields.common.message;

import java.util.List;

import org.codice.ddf.admin.query.api.action.Message;
import org.codice.ddf.admin.query.commons.fields.base.BaseListField;

public class MessageListField extends BaseListField<MessageField> {

    public static final String DESCRIPTION = "A list containing messages.";
    public MessageListField(String fieldName) {
        super(fieldName, DESCRIPTION, new MessageField(null, null, null));
    }

    @Override
    public List<Message> validate() {
        throw new UnsupportedOperationException();
    }
}
