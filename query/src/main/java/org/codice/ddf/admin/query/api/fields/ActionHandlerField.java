package org.codice.ddf.admin.query.api.fields;

import java.util.List;
import java.util.Map;

import org.codice.ddf.admin.query.api.fields.ActionField;
import org.codice.ddf.admin.query.api.fields.Field;

public interface ActionHandlerField extends Field {
    Field process(ActionField action, Map<String, Object> args);
    List<ActionField> getDiscoveryActions();
    List<ActionField> getPersistActions();
}
