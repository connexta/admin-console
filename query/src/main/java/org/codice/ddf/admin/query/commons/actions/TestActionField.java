package org.codice.ddf.admin.query.commons.actions;

import org.codice.ddf.admin.query.api.fields.ReportField;
import org.codice.ddf.admin.query.commons.fields.common.BaseReportField;

public abstract class TestActionField extends BaseActionField<ReportField> {

    public TestActionField(String fieldName, String description) {
        super(fieldName, description, new BaseReportField());
    }
}
