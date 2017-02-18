package org.codice.ddf.admin.query.commons.fields.base;

import static org.codice.ddf.admin.query.api.fields.Field.FieldBaseType.OBJECT;

import org.codice.ddf.admin.query.api.fields.Field;

public abstract  class ObjectField extends BaseField {

    public ObjectField(String fieldName, String fieldTypeName) {
        super(fieldName, fieldTypeName, OBJECT);
    }

    public abstract java.util.List<Field> getFields();
}
