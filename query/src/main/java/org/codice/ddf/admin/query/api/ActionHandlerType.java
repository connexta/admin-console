package org.codice.ddf.admin.query.api;

import java.util.List;
import java.util.Map;

public interface ActionHandlerType {
    String description();
    ActionReport process(ActionType action, List<Field> args);
    List<ActionType> getSupportedActions();
}
