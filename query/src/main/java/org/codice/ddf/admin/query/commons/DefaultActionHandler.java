package org.codice.ddf.admin.query.commons;

import java.util.Map;
import java.util.Optional;

import org.codice.ddf.admin.query.api.ActionHandler;
import org.codice.ddf.admin.query.api.Action;
import org.codice.ddf.admin.query.api.fields.ActionField;
import org.codice.ddf.admin.query.api.fields.Field;

public abstract class DefaultActionHandler implements ActionHandler {

    @Override
    public Field process(ActionField action, Map<String, Object> args) {
        Optional<ActionField> foundAction = getDiscoveryActions().stream()
                .filter(actionType -> actionType.fieldName()
                        .equals(action.fieldName()))
                .findFirst();

        if(foundAction.isPresent()) {
            return foundAction.get().process(args);
        }
        return null;
    }
}
