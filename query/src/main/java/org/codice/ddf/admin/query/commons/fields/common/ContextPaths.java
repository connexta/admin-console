package org.codice.ddf.admin.query.commons.fields.common;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.commons.fields.base.BaseListField;

public class ContextPaths extends BaseListField<ContextPath> {

    public static final String DEFAULT_FIELD_NAME = "paths";
    public static final String DESCRIPTION = "A list of context paths.";

    public ContextPaths() {
        super(DEFAULT_FIELD_NAME, DESCRIPTION);
    }

    @Override
    public Field getListValueField() {
        return new ContextPath();
    }

    @Override
    public ContextPaths addField(ContextPath value) {
        super.addField(value);
        return this;
    }
}
