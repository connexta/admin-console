package org.codice.ddf.admin.query.api;

import java.util.List;
import java.util.Map;

import org.codice.ddf.admin.query.api.field.Field;

public interface Action {
    String description();
    String getActionId();
    ActionReport process(Map<String, Object> args);
    ActionReport validate(Map<String, Object> args);
    List<Field> getRequiredFields();
    List<Field> getOptionalFields();
    List<Field> getReturnTypes();
}
