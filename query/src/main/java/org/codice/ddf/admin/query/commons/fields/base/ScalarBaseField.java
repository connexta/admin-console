package org.codice.ddf.admin.query.commons.fields.base;

import org.codice.ddf.admin.query.api.fields.ScalarField;

public abstract class ScalarBaseField<T> extends BaseField implements ScalarField<T> {

    private T value;

    public ScalarBaseField(String fieldName, String fieldTypeName, String description, FieldBaseType fieldBaseType) {
        super(fieldName, fieldTypeName, description, fieldBaseType);
    }

    public T getValue() {
        return value;
    }

    public ScalarBaseField setValue(T value) {
        this.value = value;
        return this;
    }
}
