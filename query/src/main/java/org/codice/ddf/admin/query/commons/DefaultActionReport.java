package org.codice.ddf.admin.query.commons;


import java.util.ArrayList;
import java.util.List;

import org.codice.ddf.admin.query.api.ActionMessage;
import org.codice.ddf.admin.query.api.ActionReport;
import org.codice.ddf.admin.query.api.Field;

public abstract class DefaultActionReport implements ActionReport {

    private ActionMessage success;
    private List<ActionMessage> warnings;
    private List<ActionMessage> failures;

    public DefaultActionReport(){
        warnings = new ArrayList<>();
        failures = new ArrayList<>();
    }
    @Override
    public ActionMessage getSuccessMessage() {
        return success;
    }

    @Override
    public List<ActionMessage> getFailureMessages() {
        return failures;
    }

    @Override
    public List<ActionMessage> getWarningsMessages() {
        return warnings;
    }

    @Override
    public List<Field> getValues() {
        return null;
    }

    @Override
    public List<Field> getReturnTypes() {
        return null;
    }
}
