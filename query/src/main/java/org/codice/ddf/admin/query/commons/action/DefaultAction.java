package org.codice.ddf.admin.query.commons.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.codice.ddf.admin.query.api.Action;
import org.codice.ddf.admin.query.api.ActionMessage;
import org.codice.ddf.admin.query.api.ActionReport;
import org.codice.ddf.admin.query.api.field.Field;
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

    @Override
    public ActionReport validate(Map<String, Object> args) {
        DefaultActionReport validationReport = new DefaultActionReport();
        ActionReport requiredFieldsReport = validateRequiredFields(populateFields(args, getRequiredFields()));
        ActionReport optionalFieldsReport = validateOptionalFields(populateFields(args, getOptionalFields()));
        validationReport.messages(requiredFieldsReport.getMessages());
        validationReport.messages(optionalFieldsReport.getMessages());
        return validationReport;
    }

    public List<Field> populateFields(Map<String, Object> args, List<Field> fields) {
        List<Field> allFields = new ArrayList<>();
        if(fields != null) {
            allFields.addAll(getRequiredFields());
        }
        allFields.stream().forEach(field -> field.setValue(args.get(field.getFieldName())));
        return allFields;
    }

    public ActionReport validateRequiredFields(List<Field> fields){
        DefaultActionReport validationReport = new DefaultActionReport();
        List<ActionMessage> messages = fields.stream()
                .flatMap(field -> field.validate()
                        .getMessages()
                        .stream())
                .collect(Collectors.toList());
        return validationReport.messages(messages);
    }

    public ActionReport validateOptionalFields(List<Field> fields){
        return new DefaultActionReport();
    }
}
