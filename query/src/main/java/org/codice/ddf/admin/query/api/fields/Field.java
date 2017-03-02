package org.codice.ddf.admin.query.api.fields;

public interface Field {

    String fieldName();
    String fieldTypeName();
    FieldBaseType fieldBaseType();
    String description();
    Object getValue();

    enum FieldBaseType {
        STRING, INTEGER, FLOAT, BOOLEAN, LIST, OBJECT, ENUM, INTERFACE, UNION, ACTION, ACTION_HANDLER
    }
}
