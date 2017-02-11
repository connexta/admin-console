package org.codice.ddf.admin.query.api;

public abstract class Field<T> {

    private String fieldName;
    private String fieldType;
    private T value;

    public Field(String fieldName, String fieldType) {
        this.fieldName = fieldName;
        this.fieldType = fieldType;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getFieldType() {
        return fieldType;
    }

    public<S> S getValue() {
        return (S) value;
    }

    public Field setValue(T value) {
        this.value = value;
        return this;
    }

    public abstract ActionReport validate();

    // TODO: tbatie - 2/10/17 - Replace with enum set, STRING, INT, DOUBLE, LIST
    public abstract Class getValueClass();

    public enum FieldType {
        STRING, INT, DOUBLE, LIST_STRING, LIST_FIELD,
    }
}
