package org.codice.ddf.admin.query.api.field;

public interface Field<T> {

    String fieldName();
    String description();
    FieldType fieldType();
    <S extends T> S getValue();
    Field setValue(T value);

    enum FieldType {
        STRING, INTEGER, LIST, OBJECT, ENUM
    }
}
