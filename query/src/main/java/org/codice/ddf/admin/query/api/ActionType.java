package org.codice.ddf.admin.query.api;

import java.util.List;
import java.util.Map;

public interface ActionType {
    String description();
    ActionReport process();
    ActionType setArguments(Map<String, Object> args);
    List<Field> getRequiredFields();
    List<Field> getOptionalFields();
    List<ActionMessage> validate();
    Field getActionId();
}
