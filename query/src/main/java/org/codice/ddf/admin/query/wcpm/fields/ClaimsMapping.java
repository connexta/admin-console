package org.codice.ddf.admin.query.wcpm.fields;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.commons.fields.base.BaseListField;

public class ClaimsMapping extends BaseListField<ClaimsMapEntry> {

    public static final String DEFAULT_FIELD_NAME = "claimsMapping";
    public static final String DESCRIPTION = "A collection of claims to claim values.";


    public ClaimsMapping() {
        super(DEFAULT_FIELD_NAME, DESCRIPTION);
    }

    @Override
    public Field getListValueField() {
        return new ClaimsMapEntry();
    }
}
