package org.codice.ddf.admin.query.wcpm.fields;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.commons.fields.base.BaseListField;
import org.codice.ddf.admin.query.commons.fields.base.scalar.StringField;

public class AuthTypeList extends BaseListField<StringField> {

    public static final String DEFAULT_FIELD_NAME = "authTypes";
    public static final String DESCRIPTION = "A list of authentication types";

    public AuthTypeList() {
        super(DEFAULT_FIELD_NAME, DESCRIPTION);
    }

    @Override
    public AuthType getListValueField() {
        return new AuthType();
    }

    @Override
    public AuthTypeList addField(StringField value) {
        super.addField(value);
        return this;
    }
}
