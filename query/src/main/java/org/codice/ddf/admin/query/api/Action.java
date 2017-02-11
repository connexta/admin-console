package org.codice.ddf.admin.query.api;

import java.util.List;
import java.util.Map;

public interface Action {
    String description();
    String getActionId();
    ActionReport process(Map<String, Object> args);
    ActionReport validate(List<Field> args);
    List<Field> getRequiredFields();
    List<Field> getOptionalFields();
    List<Field> getReturnTypes();
}
