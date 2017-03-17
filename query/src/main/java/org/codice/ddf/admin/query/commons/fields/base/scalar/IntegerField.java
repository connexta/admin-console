package org.codice.ddf.admin.query.commons.fields.base.scalar;

import static org.codice.ddf.admin.query.api.fields.Field.FieldBaseType.INTEGER;

public class IntegerField extends BaseScalarField<Integer> {

    public IntegerField(String fieldName) {
        super(fieldName, null, null, INTEGER);
    }

    protected IntegerField(String fieldName, String fieldTypeName, String description) {
        super(fieldName, fieldTypeName, description, INTEGER);
    }

}
