package org.codice.ddf.admin.query.commons.fields.base.scalar;

import org.codice.ddf.admin.query.api.fields.ScalarField;
import org.codice.ddf.admin.query.commons.fields.base.BaseField;

public abstract class BaseScalarField<T> extends BaseField implements ScalarField<T> {

    private T value;

    public BaseScalarField(String fieldName, String fieldTypeName, String description, FieldBaseType fieldBaseType) {
        super(fieldName, fieldTypeName, description, fieldBaseType);
    }

    public T getValue() {return value; }

    public BaseScalarField setValue(T value) {
        this.value = value;
        return this;
    }
}
