package org.codice.ddf.admin.query.commons.fields.base.scalar;

import static org.codice.ddf.admin.query.api.fields.Field.FieldBaseType.BOOLEAN;

public class BooleanField extends BaseScalarField<Boolean> {
    public BooleanField(String fieldName) {
        super(fieldName, null, null, BOOLEAN);
    }

    protected BooleanField(String fieldName, String fieldTypeName, String description) {
        super(fieldName, fieldTypeName, description, BOOLEAN);
    }
}
