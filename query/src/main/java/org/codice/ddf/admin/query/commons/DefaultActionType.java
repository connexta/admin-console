package org.codice.ddf.admin.query.commons;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codice.ddf.admin.query.api.ActionMessage;
import org.codice.ddf.admin.query.api.ActionType;
import org.codice.ddf.admin.query.api.Field;

public abstract class DefaultActionType implements ActionType {

    private CommonFields.ActionId actionId;
    private String description;
    private List<Field> requiredFields;
    private List<Field> optionalFields;

    public DefaultActionType(String actionId, String description, List<Field> requiredFields,
            List<Field> optionalFields) {
        this.actionId = new CommonFields.ActionId(actionId);
        this.description = description;
        this.requiredFields = requiredFields;
        this.optionalFields = optionalFields;
    }

    public List<ActionMessage> validate(){
        List<ActionMessage> allMsgs = new ArrayList<>();
        List<ActionMessage> reqFieldMsgs = validateRequiredFields();
        List<ActionMessage> optionalFieldMsgs = validateOptionalFields();
        if(reqFieldMsgs != null) {
            allMsgs.addAll(reqFieldMsgs);
        }

        if(optionalFieldMsgs != null) {
            allMsgs.addAll(optionalFieldMsgs);
        }
        return allMsgs;
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public List<Field> getRequiredFields() {
        return requiredFields;
    }

    @Override
    public List<Field> getOptionalFields() {
        return optionalFields;
    }

    @Override
    public Field getActionId() {
        return actionId;
    }
    public List<ActionMessage> validateRequiredFields() {
        // TODO: tbatie - 2/6/17
        return new ArrayList<>();
    }

    public List<ActionMessage> validateOptionalFields() {
        return new ArrayList<>();
    }

    public abstract DefaultActionType setArguments(Map<String, Object> args);
}
