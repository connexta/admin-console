package org.codice.ddf.admin.query.sts.field;

import org.codice.ddf.admin.query.commons.fields.base.scalar.StringField;

public class StsClaimField extends StringField {

    public static final String DEFAULT_FIELD_NAME = "claim";
    public static final String FIELD_NAME_TYPE = "StsClaim";
    public static final String DESCRIPTION = "A statement that one subject, such as a person or organization, makes about itself or another subject";

    public StsClaimField(String fieldName) {
        super(fieldName, FIELD_NAME_TYPE, DESCRIPTION);
    }

    public StsClaimField() {
        super(DEFAULT_FIELD_NAME, FIELD_NAME_TYPE, DESCRIPTION);
    }
}
