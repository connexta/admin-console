package org.codice.ddf.admin.query.api.action;

import java.util.List;
import java.util.Map;

import org.codice.ddf.admin.query.api.fields.Field;

public interface Action<T extends Field> {
    String name();
    String description();
    T returnType();
    T process();
    List<Field> getArguments();
    List<Message> validate();
    void setArguments(Map<String, Object> args);
}
