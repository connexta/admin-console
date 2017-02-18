package org.codice.ddf.admin.query.commons.fields.base;

import static org.codice.ddf.admin.query.api.fields.Field.FieldBaseType.INTEGER;

public class IntegerField extends BaseField<Integer> {

    public IntegerField(String fieldName) {
        super(fieldName, null, INTEGER);
    }

    @Override
    public String description() {
        return null;
    }

    @Override
    public Integer getValue() {
        return null;
    }
}
