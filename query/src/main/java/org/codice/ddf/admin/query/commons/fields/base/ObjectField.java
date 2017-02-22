package org.codice.ddf.admin.query.commons.fields.base;

import static org.codice.ddf.admin.query.api.fields.Field.FieldBaseType.OBJECT;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.codice.ddf.admin.query.api.fields.Field;

public abstract class ObjectField extends BaseField {

    public ObjectField(String fieldName, String fieldTypeName, String description) {
        super(fieldName, fieldTypeName, description, OBJECT);
    }

    public abstract List<Field> getFields();

    @Override
    public Object getValue() {
        Map<String, Object> value = new HashMap<>();
        getFields().forEach(field -> value.put(field.fieldName(), field.getValue()));
        return value;
    }
}
