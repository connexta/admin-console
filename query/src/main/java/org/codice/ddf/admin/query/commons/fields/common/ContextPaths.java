package org.codice.ddf.admin.query.commons.fields.common;

import org.codice.ddf.admin.query.commons.fields.base.BaseListField;

public class ContextPaths extends BaseListField<ContextPath> {

    public static final String DEFAULT_FIELD_NAME = "paths";
    public static final String DESCRIPTION = "A list of context paths.";

    public ContextPaths() {
        super(DEFAULT_FIELD_NAME, DESCRIPTION, new ContextPath());
    }

    @Override
    public ContextPaths add(ContextPath value) {
        super.add(value);
        return this;
    }
}
