package org.codice.ddf.admin.query.commons.fields.base;

import static org.codice.ddf.admin.query.api.fields.Field.FieldBaseType.STRING;

public class StringField extends BaseField<String>{

    public StringField(String fieldName) {
        super(fieldName, null, STRING);
    }

    @Override
    public String description() {
        return null;
    }
}
