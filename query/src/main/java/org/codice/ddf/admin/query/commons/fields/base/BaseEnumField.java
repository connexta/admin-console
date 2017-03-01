package org.codice.ddf.admin.query.commons.fields.base;

import static org.codice.ddf.admin.query.api.fields.Field.FieldBaseType.ENUM;

import java.util.List;

import org.codice.ddf.admin.query.api.fields.EnumField;
import org.codice.ddf.admin.query.api.fields.Field;

public abstract class BaseEnumField<T extends Field> extends BaseField implements EnumField<T> {

    private Field value;

    public BaseEnumField(String fieldName, String fieldTypeName, String description) {
        super(fieldName, fieldTypeName, description, ENUM);
    }

    public Object getValue() {
        return value.getValue();
    }

    public void setValue(Field value) {
        this.value = value;
    }

    public abstract List<T> getEnumValues();
}
