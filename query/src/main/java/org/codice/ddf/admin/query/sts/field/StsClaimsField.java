package org.codice.ddf.admin.query.sts.field;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.commons.fields.base.BaseListField;

public class StsClaimsField extends BaseListField {

    public static final String DEFAULT_FIELD_NAME = "claims";
    public static final String DESCRIPTION = "All currently configured claims supported by the STS";

    public StsClaimsField() {
        super(DEFAULT_FIELD_NAME, DESCRIPTION);
    }

    @Override
    public Field getListValueField() {
        return new StsClaimField();
    }
}
