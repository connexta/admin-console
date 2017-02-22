package org.codice.ddf.admin.query.commons.fields.common.message;

import java.util.List;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.api.fields.Message;
import org.codice.ddf.admin.query.commons.fields.base.ListField;

public class MessageListField extends ListField<MessageField>{

    public static final String DESCRIPTION = "A list containing messages.";
    public MessageListField(String fieldName) {
        super(fieldName, DESCRIPTION);
    }

    public List<MessageField> getMessages() {
        return values;
    }

    @Override
    public Field getListValueField() {
        return new MessageField(null, null, null);
    }
}
