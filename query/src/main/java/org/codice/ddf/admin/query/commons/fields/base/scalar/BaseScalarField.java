package org.codice.ddf.admin.query.commons.fields.base.scalar;

import java.util.ArrayList;
import java.util.List;

import org.codice.ddf.admin.query.api.action.Message;
import org.codice.ddf.admin.query.commons.fields.base.BaseField;

public abstract class BaseScalarField<T> extends BaseField<T> {

    private T value;

    public BaseScalarField(String fieldName, String fieldTypeName, String description, FieldBaseType fieldBaseType) {
        super(fieldName, fieldTypeName, description, fieldBaseType);
    }

    public T getValue() {return value; }

    @Override
    public void setValue(T value) {
        this.value = value;
    }

    @Override
    public List<Message> validate() {
        // TODO: tbatie - 3/16/17 - Validate scalar fields
        return new ArrayList<>();
    }
}
