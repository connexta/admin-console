package org.codice.ddf.admin.query.commons.fields.common;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.commons.fields.base.BaseListField;

public class ContextPathList extends BaseListField<ContextPathField> {

    public static final String DEFAULT_FIELD_NAME = "paths";
    public static final String DESCRIPTION = "A list of context paths.";

    public ContextPathList() {
        super(DEFAULT_FIELD_NAME, DESCRIPTION);
    }

    @Override
    public Field getListValueField() {
        return new ContextPathField();
    }

    @Override
    public ContextPathList addField(ContextPathField value) {
        super.addField(value);
        return this;
    }
}
