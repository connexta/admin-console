package org.codice.ddf.admin.query.sources.common;

import org.codice.ddf.admin.query.commons.fields.base.ListField;

public class SourceConfigurationListField extends ListField {

    public static final String DEFAULT_FIELD_NAME = "sourceConfigs";
    public static final String DESCRIPTION = "A list of source configurations";

    public SourceConfigurationListField() {
        super(DEFAULT_FIELD_NAME, DESCRIPTION);
    }

    @Override
    public SourceConfigUnionField getListValueField() {
        return new SourceConfigUnionField();
    }
}
