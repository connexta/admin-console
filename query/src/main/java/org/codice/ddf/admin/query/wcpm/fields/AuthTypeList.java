package org.codice.ddf.admin.query.wcpm.fields;

import org.codice.ddf.admin.query.commons.fields.base.BaseListField;
import org.codice.ddf.admin.query.commons.fields.base.scalar.StringField;

public class AuthTypeList extends BaseListField<AuthType> {

    public static final String DEFAULT_FIELD_NAME = "authTypes";
    public static final String DESCRIPTION = "A list of authentication types";

    public AuthTypeList() {
        super(DEFAULT_FIELD_NAME, DESCRIPTION, new AuthType());
    }

    @Override
    public AuthTypeList add(AuthType value) {
        super.add(value);
        return this;
    }
}
