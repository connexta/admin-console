package org.codice.ddf.admin.query.api.field;

import org.codice.ddf.admin.query.api.ActionReport;

public abstract class Field<T> {

    private String fieldName;
    private FieldType fieldType;
    private T value;

    public Field(String fieldName, FieldType fieldType) {
        this.fieldName = fieldName;
        this.fieldType = fieldType;
    }

    public abstract ActionReport validate();
    public String getFieldName() {
        return fieldName;
    }
    public FieldType getFieldType() {
        return fieldType;
    }
    public<S> S getValue() {
        return (S) value;
    }
    public Field setValue(T value) {
        this.value = value;
        return this;
    }
    public enum FieldType {
        STRING, INTEGER, DECIMAL, LIST
    }
}
