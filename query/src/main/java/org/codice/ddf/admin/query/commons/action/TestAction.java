package org.codice.ddf.admin.query.commons.action;

import java.util.List;
import org.codice.ddf.admin.query.api.field.Field;
import org.codice.ddf.admin.query.api.field.Report;
import org.codice.ddf.admin.query.commons.field.BaseFields;

public abstract class TestAction extends DefaultAction<Report>{

    public TestAction(String actionId, String description, List<Field> requiredFields,
            List<Field> optionalFields) {
        super(actionId, description, requiredFields, optionalFields, new BaseFields.BaseReport());
    }
}
