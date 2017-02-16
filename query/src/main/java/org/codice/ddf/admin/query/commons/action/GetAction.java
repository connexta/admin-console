package org.codice.ddf.admin.query.commons.action;

import java.util.List;
import java.util.Map;

import org.codice.ddf.admin.query.api.field.Field;

public abstract class GetAction<T extends Field> extends DefaultAction<T> {

    public GetAction(String actionId, String description, T returnType) {
        super(actionId, description, null, null, returnType);
    }

    public abstract T process();

    @Override
    public T process(Map<String, Object> args) {
        return process();
    }

    @Override
    public List<Field> getRequiredFields() {
        return null;
    }

    @Override
    public List<Field> getOptionalFields() {
        return null;
    }

}
