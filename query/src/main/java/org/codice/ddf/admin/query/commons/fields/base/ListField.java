package org.codice.ddf.admin.query.commons.fields.base;

import static org.codice.ddf.admin.query.api.fields.Field.FieldBaseType.LIST;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.codice.ddf.admin.query.api.fields.Field;

public abstract class ListField<T extends Field> extends BaseField {

    protected List<T> values;

    public ListField(String fieldName, String description) {
        super(fieldName, null, description, LIST);
        values = new ArrayList<T>();
    }

    @Override
    public List getValue() {
        return values.stream()
                .map(value ->  value.getValue())
                .collect(Collectors.toList());
    }

    public Field addField(T value) {
        values.add(value);
        return this;
    }

    public abstract Field getListValueField();
}
