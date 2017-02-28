package org.codice.ddf.admin.query.api;

import java.util.List;
import java.util.Map;

import org.codice.ddf.admin.query.api.fields.ActionField;
import org.codice.ddf.admin.query.api.fields.Field;

public interface ActionHandler {
    String getActionHandlerId();
    String description();
    Field process(ActionField action, Map<String, Object> args);
    List<ActionField> getDiscoveryActions();
    List<ActionField> getPersistActions();
}
