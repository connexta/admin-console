package org.codice.ddf.admin.query.commons.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codice.ddf.admin.query.api.fields.Field;

public abstract class GetAction<T extends Field> extends BaseActionField<T> {

    public GetAction(String actionId, String description, T returnType) {
        super(actionId, description, returnType);
    }

    public abstract T process();

    @Override
    public T process(Map<String, Object> args) {
        return process();
    }

    @Override
    public List<Field> getArguments() {
        return new ArrayList<>();
    }
}
