package org.codice.ddf.admin.query.commons.action;

import java.util.List;
import java.util.Map;

import org.codice.ddf.admin.query.api.ActionReport;
import org.codice.ddf.admin.query.api.Field;
import org.codice.ddf.admin.query.commons.DefaultActionReport;

public abstract class GetAction extends DefaultAction {

    public GetAction(String actionId, String description) {
        super(actionId, description, null, null);
    }

    public abstract ActionReport process();

    @Override
    public ActionReport process(Map<String, Object> args) {
        return process();
    }

    @Override
    public List<Field> getRequiredFields() {
        return null;
    }

    @Override
    public List<Field> getOptionalFields() {
        return null;
    }

    @Override
    public ActionReport validate(List<Field> args) {
        return new DefaultActionReport();
    }
}
