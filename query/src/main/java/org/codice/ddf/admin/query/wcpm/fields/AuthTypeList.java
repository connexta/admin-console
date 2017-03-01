package org.codice.ddf.admin.query.wcpm.fields;

import org.codice.ddf.admin.query.commons.fields.base.ListField;
import org.codice.ddf.admin.query.commons.fields.base.scalar.StringField;

public class AuthTypeList extends ListField<StringField> {

    public static final String DEFAULT_FIELD_NAME = "authTypes";
    public static final String DESCRIPTION = "A list of authentication types";

    public AuthTypeList() {
        super(DEFAULT_FIELD_NAME, DESCRIPTION);
    }

    @Override
    public AuthType getListValueField() {
        return new AuthType();
    }
}
