package org.codice.ddf.admin.query.commons.fields.base;

import static org.codice.ddf.admin.query.api.fields.Field.FieldBaseType.LIST;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.api.fields.ListField;

public abstract class BaseListField<T extends Field> extends BaseField implements ListField<T> {

    protected List<T> fields;

    public BaseListField(String fieldName, String description) {
        super(fieldName, null, description, LIST);
        fields = new ArrayList<T>();
    }

    @Override
    public List getValue() {
        return fields.stream()
                .map(field -> field.getValue())
                .collect(Collectors.toList());
    }

    public Field addField(T value) {
        fields.add(value);
        return this;
    }

    public List<T> getFields() {
        return fields;
    }
}
