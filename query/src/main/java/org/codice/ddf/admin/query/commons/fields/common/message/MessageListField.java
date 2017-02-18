package org.codice.ddf.admin.query.commons.fields.common.message;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.api.fields.Message;
import org.codice.ddf.admin.query.commons.fields.base.ListField;

/**
 * Created by tbatie1 on 2/17/17.
 */
public class MessageListField extends ListField<Message>{

    public MessageListField(String fieldName) {
        super(fieldName);
    }

    @Override
    public String description() {
        return "A list containing messages.";
    }

    @Override
    public Field getListValueField() {
        return new MessageField(null, null, null);
    }
}
