package org.codice.ddf.admin.query.commons.actions;

import java.util.List;
import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.api.fields.ReportField;
import org.codice.ddf.admin.query.commons.fields.common.BaseReportField;

public abstract class TestAction extends DefaultAction<ReportField>{

    public TestAction(String actionId, String description, List<Field> requiredFields,
            List<Field> optionalFields) {
        super(actionId, description, requiredFields, optionalFields, new BaseReportField());
    }
}
