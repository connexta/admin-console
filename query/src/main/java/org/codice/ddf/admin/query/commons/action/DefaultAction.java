package org.codice.ddf.admin.query.commons.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codice.ddf.admin.query.api.Action;
import org.codice.ddf.admin.query.api.ActionReport;
import org.codice.ddf.admin.query.api.Field;
import org.codice.ddf.admin.query.commons.DefaultActionReport;

public abstract class DefaultAction implements Action {

    private String actionId;
    private String description;
    private List<Field> requiredFields;
    private List<Field> optionalFields;
    private List<Field> returnTypes;

    public DefaultAction(String actionId, String description, List<Field> requiredFields,
            List<Field> optionalFields) {
        this.actionId = actionId;
        this.description = description;
        this.requiredFields = requiredFields;
        this.optionalFields = optionalFields;
    }

    public DefaultAction(String actionId, String description, List<Field> requiredFields,
            List<Field> optionalFields, List<Field> returnTypes) {
        this.actionId = actionId;
        this.description = description;
        this.requiredFields = requiredFields;
        this.optionalFields = optionalFields;
        this.returnTypes = returnTypes;
    }

    @Override
    public String getActionId() {
        return actionId;
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public ActionReport validate(List<Field> args) {
        DefaultActionReport validationReport = new DefaultActionReport();
//        requiredFields.stream()
//                .map(field -> field.validate())
//                .forEach(report -> validationReport.messages(report.getMessages()));
        return validationReport;
    }

    @Override
    public List<Field> getReturnTypes() {
        return returnTypes;
    }

    @Override
    public List<Field> getRequiredFields() {
        return requiredFields;
    }

    @Override
    public List<Field> getOptionalFields() {
        return optionalFields;
    }
}
