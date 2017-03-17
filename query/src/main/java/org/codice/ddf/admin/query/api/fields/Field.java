package org.codice.ddf.admin.query.api.fields;

import java.util.List;

import org.codice.ddf.admin.query.api.action.Message;

public interface Field<T> {
    String fieldName();
    String fieldTypeName();
    FieldBaseType fieldBaseType();
    String description();
    T getValue();
    void setValue(T value);
    List<Message> validate();

    // TODO: tbatie - 3/16/17 - Should we make this strings for expandability
    enum FieldBaseType {
        STRING, INTEGER, FLOAT, BOOLEAN, LIST, OBJECT, ENUM, UNION
    }
}
