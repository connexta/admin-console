package org.codice.ddf.admin.query.commons.fields.base.scalar;

import static org.codice.ddf.admin.query.api.fields.Field.FieldBaseType.STRING;

public class StringField extends BaseScalarField<String> {

    public StringField(String fieldName) {
        super(fieldName, null, null, STRING);
    }


    public StringField(String fieldName, String fieldTypeName, String description) {
        super(fieldName, fieldTypeName, description, STRING);
    }

    @Override
    public StringField setValue(String value) {
        super.setValue(value);
        return this;
    }

    @Override
    public String getValue() {
        return super.getValue();
    }
}
