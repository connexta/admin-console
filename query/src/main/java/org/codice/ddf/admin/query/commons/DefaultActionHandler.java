package org.codice.ddf.admin.query.commons;

import java.util.Map;
import java.util.Optional;

import org.codice.ddf.admin.query.api.fields.ActionHandlerField;
import org.codice.ddf.admin.query.api.fields.ActionField;
import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.commons.fields.base.BaseField;

public abstract class DefaultActionHandler extends BaseField implements ActionHandlerField {

    public DefaultActionHandler(String fieldName, String description) {
        super(fieldName, null, description, FieldBaseType.ACTION_HANDLER);
    }

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

    @Override
    public Object getValue() {
        return null;
    }
}
