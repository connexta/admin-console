package org.codice.ddf.admin.query.api.action;

import java.util.List;

import org.codice.ddf.admin.query.api.fields.Field;

public interface ActionCreator {
    String name();
    String typeName();
    String description();
    Action createAction(String actionId);
    List<Action> getDiscoveryActions();
    List<Action> getPersistActions();
}
