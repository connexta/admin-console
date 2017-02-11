package org.codice.ddf.admin.query.commons;

import java.util.Map;
import java.util.Optional;

import org.codice.ddf.admin.query.api.ActionHandler;
import org.codice.ddf.admin.query.api.ActionReport;
import org.codice.ddf.admin.query.api.Action;

public abstract class DefaultActionHandler implements ActionHandler {

    @Override
    public ActionReport process(Action action, Map<String, Object> args) {
        Optional<Action> foundAction = getSupportedActions().stream()
                .filter(actionType -> actionType.getActionId()
                        .equals(action.getActionId()))
                .findFirst();

        if(foundAction.isPresent()) {
            return foundAction.get().process(args);
        }
        return null;
    }
}
