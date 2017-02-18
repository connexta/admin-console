package org.codice.ddf.admin.query.sts.field;

import org.codice.ddf.admin.query.commons.fields.base.StringField;

public class StsClaimField extends StringField {

    public static final String FIELD_NAME = "claim";

    public StsClaimField() {
        super(FIELD_NAME);
    }
}
