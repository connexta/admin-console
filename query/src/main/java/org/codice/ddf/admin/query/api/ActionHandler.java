package org.codice.ddf.admin.query.api;

import java.util.List;
import java.util.Map;

public interface ActionHandler {
    String getActionHandlerId();
    String description();
    ActionReport process(Action action, Map<String, Object> args);
    List<Action> getSupportedActions();
}
