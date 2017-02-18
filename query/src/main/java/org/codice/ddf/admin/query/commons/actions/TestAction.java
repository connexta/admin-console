package org.codice.ddf.admin.query.commons.actions;

import java.util.List;
import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.api.fields.Report;
import org.codice.ddf.admin.query.commons.fields.common.ReportField;

public abstract class TestAction extends DefaultAction<Report>{

    public TestAction(String actionId, String description, List<Field> requiredFields,
            List<Field> optionalFields) {
        super(actionId, description, requiredFields, optionalFields, new ReportField());
    }
}
