package org.codice.ddf.admin.query.commons.fields.base;

import static org.codice.ddf.admin.query.api.fields.Field.FieldBaseType.LIST;

import org.codice.ddf.admin.query.api.fields.Field;

public abstract class ListField<T extends Field> extends BaseField<T> {

    public ListField(String fieldName) {
        super(fieldName, null, LIST);
    }

    public abstract Field getListValueField();
}
