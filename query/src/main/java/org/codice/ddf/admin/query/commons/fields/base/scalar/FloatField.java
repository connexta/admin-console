package org.codice.ddf.admin.query.commons.fields.base.scalar;

import static org.codice.ddf.admin.query.api.fields.Field.FieldBaseType.FLOAT;

public class FloatField extends BaseScalarField<Float> {
    public FloatField(String fieldName) {
        super(fieldName, null, null, FLOAT);
    }

    protected FloatField(String fieldName, String fieldTypeName, String description) {
        super(fieldName, fieldTypeName, description, FLOAT);
    }

}

