package org.codice.ddf.admin.query.commons.fields.base;

import static org.codice.ddf.admin.query.api.fields.Field.FieldBaseType.ENUM;

import java.util.List;

public abstract class EnumField<T> extends BaseField<EnumFieldValue<T>> {

    public EnumField(String fieldName, String fieldTypeName) {
        super(fieldName, fieldTypeName, ENUM);
    }

    public abstract List<EnumFieldValue<T>> getEnumValues();
}
