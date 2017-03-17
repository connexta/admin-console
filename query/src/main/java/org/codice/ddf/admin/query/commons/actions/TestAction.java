package org.codice.ddf.admin.query.commons.actions;

import org.codice.ddf.admin.query.commons.fields.common.ReportField;

public abstract class TestAction extends BaseAction<ReportField> {

    public TestAction(String fieldName, String description) {
        super(fieldName, description, new ReportField());
    }
}
