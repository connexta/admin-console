package org.codice.ddf.admin.query.commons.actions;

import java.util.List;

import org.codice.ddf.admin.query.api.Action;
import org.codice.ddf.admin.query.api.fields.Field;

public abstract class DefaultAction<T extends Field> implements Action<T> {

    private String actionId;
    private String description;
    private List<Field> requiredFields;
    private List<Field> optionalFields;
    private T returnType;

    public DefaultAction(String actionId, String description, List<Field> requiredFields,
            List<Field> optionalFields, T returnType) {
        this.actionId = actionId;
        this.description = description;
        this.requiredFields = requiredFields;
        this.optionalFields = optionalFields;
        this.returnType = returnType;
    }

    @Override
    public String getActionName() {
        return actionId;
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public T getReturnType() {
        return returnType;
    }

    @Override
    public List<Field> getRequiredFields() {
        return requiredFields;
    }

    @Override
    public List<Field> getOptionalFields() {
        return optionalFields;
    }
}
