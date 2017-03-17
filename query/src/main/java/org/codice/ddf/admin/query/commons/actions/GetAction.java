package org.codice.ddf.admin.query.commons.actions;

import java.util.ArrayList;
import java.util.List;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.api.action.Message;

public abstract class GetAction<T extends Field> extends BaseAction<T> {

    public GetAction(String fieldName, String description, T returnType) {
        super(fieldName, description, returnType);
    }

    @Override
    public List<Field> getArguments() {
        return new ArrayList<>();
    }

    @Override
    public List<Message> validate() {
        return new ArrayList<>();
    }
}
