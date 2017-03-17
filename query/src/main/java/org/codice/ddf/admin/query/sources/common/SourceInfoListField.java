package org.codice.ddf.admin.query.sources.common;

import org.codice.ddf.admin.query.commons.fields.base.BaseListField;

public class SourceInfoListField extends BaseListField<SourceInfoField> {

    public static final String DEFAULT_FIELD_NAME = "sourceConfigs";
    public static final String DESCRIPTION = "A list of source configurations";

    public SourceInfoListField() {
        super(DEFAULT_FIELD_NAME, DESCRIPTION, new SourceInfoField());
    }

    @Override
    public SourceInfoListField add(SourceInfoField value) {
        super.add(value);
        return this;
    }
}
