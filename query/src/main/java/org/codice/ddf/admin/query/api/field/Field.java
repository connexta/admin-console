package org.codice.ddf.admin.query.api.field;

public interface Field<T> {

    String fieldName();
    String fieldTypeName();
    FieldBaseType fieldBaseType();
    String description();
    <S extends T> S getValue();
    Field addValue(T value);

    enum FieldBaseType {
        STRING, INTEGER, LIST, OBJECT, ENUM
    }
}
