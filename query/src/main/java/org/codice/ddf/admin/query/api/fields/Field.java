package org.codice.ddf.admin.query.api.fields;

public interface Field {

    // TODO: tbatie - 2/17/17 - Add setRequired field
    String fieldName();
    String fieldTypeName();
    FieldBaseType fieldBaseType();
    String description();
    Object getValue();

    enum FieldBaseType {
        STRING, INTEGER, FLOAT, LIST, OBJECT, ENUM
    }
}
