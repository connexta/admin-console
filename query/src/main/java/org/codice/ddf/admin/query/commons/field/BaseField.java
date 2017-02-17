package org.codice.ddf.admin.query.commons.field;

import org.codice.ddf.admin.query.api.field.Field;

public abstract class BaseField<T> implements Field<T> {

    private String fieldName;
    private String fieldTypeName;
    private FieldBaseType fieldBaseType;
    private T value;

    public BaseField(String fieldName, String fieldTypeName, FieldBaseType fieldBaseType) {
        this.fieldName = fieldName;
        this.fieldTypeName = fieldTypeName;
        this.fieldBaseType = fieldBaseType;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    @Override
    public String fieldTypeName() {
        return fieldTypeName;
    }

    @Override

    public FieldBaseType fieldBaseType() {
        return fieldBaseType;
    }

    @Override
    public <S extends T> S getValue() {
        return (S) value;
    }

    @Override
    public Field addValue(T value) {
        this.value = value;
        return this;
    }
}
