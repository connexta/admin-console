package org.codice.ddf.admin.query.commons.fields.base;

import static org.codice.ddf.admin.query.api.fields.Field.FieldBaseType.ENUM;

import java.util.List;

public abstract class BaseEnumField<T> extends BaseField {

    private EnumFieldValue<T> value;

    public BaseEnumField(String fieldName, String fieldTypeName, String description) {
        super(fieldName, fieldTypeName, description, ENUM);
    }

    public T getValue() {
        return value.getValue();
    }

    public void setValue(T value) {
        this.value.setValue(value);
    }

    public abstract List<EnumFieldValue<T>> getEnumValues();
}
