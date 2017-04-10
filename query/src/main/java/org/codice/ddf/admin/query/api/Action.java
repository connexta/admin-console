package org.codice.ddf.admin.query.api;

import java.util.List;
import java.util.Map;

import org.codice.ddf.admin.query.api.fields.Field;

public interface Action<T extends Field> {
    String description();
    String getActionName();
    Field getReturnType();
    T process(Map<String, Object> args);
    List<Field> getRequiredFields();
    List<Field> getOptionalFields();
}
