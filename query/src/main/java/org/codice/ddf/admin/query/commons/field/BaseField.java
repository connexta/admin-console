package org.codice.ddf.admin.query.commons.field;

import org.codice.ddf.admin.query.api.field.Field;

public abstract class BaseField<T> implements Field<T> {

    private String fieldName;
    private Field.FieldType fieldType;
    private T value;

    public BaseField(String fieldName, Field.FieldType fieldType) {
        this.fieldName = fieldName;
        this.fieldType = fieldType;
    }

    public String fieldName() {
        return fieldName;
    }
    public Field.FieldType fieldType() {
        return fieldType;
    }
    public <S extends T> S getValue() {
        return (S) value;
    }
    public Field setValue(T value) {
        this.value = value;
        return this;
    }
    public enum FieldType {
        STRING, INTEGER, DECIMAL, LIST, OBJECT, ENUM
    }
}
