package org.codice.ddf.admin.query.commons.fields.base;

import static org.codice.ddf.admin.query.api.fields.Field.FieldBaseType.INTEGER;

public class IntegerField extends ScalarBaseField<Integer> {

    public IntegerField(String fieldName) {
        super(fieldName, null, null, INTEGER);
    }
}
