package org.codice.ddf.admin.query.api;

import java.util.List;
import java.util.Map;

import org.codice.ddf.admin.query.api.field.Field;

public interface ActionHandler {
    String getActionHandlerId();
    String description();
    Field process(Action action, Map<String, Object> args);
    List<Action> getSupportedActions();
}
