package org.codice.ddf.admin.query.commons.fields.base;

import static org.codice.ddf.admin.query.api.fields.Field.FieldBaseType.FLOAT;

public class FloatField extends BaseField<Float>{
    public FloatField(String fieldName) {
        super(fieldName, null, FLOAT);
    }

    @Override
    public String description() {
        return null;
    }

    @Override
    public Float getValue() {
        return null;
    }
}

