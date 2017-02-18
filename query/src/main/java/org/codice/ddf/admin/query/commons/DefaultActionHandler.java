package org.codice.ddf.admin.query.commons;

import java.util.Map;
import java.util.Optional;

import org.codice.ddf.admin.query.api.ActionHandler;
import org.codice.ddf.admin.query.api.Action;
import org.codice.ddf.admin.query.api.fields.Field;

public abstract class DefaultActionHandler implements ActionHandler {

    @Override
    public Field process(Action action, Map<String, Object> args) {
        Optional<Action> foundAction = getSupportedActions().stream()
                .filter(actionType -> actionType.getActionName()
                        .equals(action.getActionName()))
                .findFirst();

        if(foundAction.isPresent()) {
            return foundAction.get().process(args);
        }
        return null;
    }
}
