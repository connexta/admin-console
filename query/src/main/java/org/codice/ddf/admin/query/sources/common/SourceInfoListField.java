package org.codice.ddf.admin.query.sources.common;

import org.codice.ddf.admin.query.commons.fields.base.ListField;

public class SourceInfoListField extends ListField<SourceInfoField> {

    public static final String DEFAULT_FIELD_NAME = "sourceConfigs";
    public static final String DESCRIPTION = "A list of source configurations";

    public SourceInfoListField() {
        super(DEFAULT_FIELD_NAME, DESCRIPTION);
    }

    @Override
    public SourceInfoListField addField(SourceInfoField value) {
        super.addField(value);
        return this;
    }

    @Override
    public SourceInfoField getListValueField() {
        return new SourceInfoField();
    }
}
