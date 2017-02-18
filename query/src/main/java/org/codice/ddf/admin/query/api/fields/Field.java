package org.codice.ddf.admin.query.api.fields;

public interface Field<T> {

    // TODO: tbatie - 2/17/17 - Add setRequired field
    String fieldName();
    String fieldTypeName();
    FieldBaseType fieldBaseType();
    String description();
    <S extends T> S getValue();
    Field addValue(T value);

    enum FieldBaseType {
        STRING, INTEGER, FLOAT, LIST, OBJECT, ENUM
    }
}
