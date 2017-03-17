package org.codice.ddf.admin.query.commons.fields.base;

import static org.codice.ddf.admin.query.api.fields.Field.FieldBaseType.LIST;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.api.fields.ListField;
import org.codice.ddf.admin.query.api.action.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseListField<T extends Field> extends BaseField<List> implements ListField<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseListField.class);

    protected List<T> fields;
    protected T listFieldType;

    public BaseListField(String fieldName, String description, T listFieldType) {
        super(fieldName, null, description, LIST);
        this.fields = new ArrayList<>();
        this.listFieldType = listFieldType;
    }

    @Override
    public T getListFieldType() {
        return listFieldType;
    }

    @Override
    public List<T> getList() {
        return fields;
    }


    @Override
    public List getValue() {
        return fields.stream()
                .map(field -> field.getValue())
                .collect(Collectors.toList());
    }

    @Override
    public void setValue(List values) {
        if(values == null) {
            return;
        }

        List<T> newFields = new ArrayList<T>();
        for(Object val : values) {
            try {
                T newField = (T) getListFieldType().getClass().newInstance();
                newField.setValue(val);
                newFields.add(newField);
            } catch (IllegalAccessException | InstantiationException e) {
                LOGGER.debug("Unable to create instance of fieldType {}", getListFieldType().fieldTypeName());
            }
        }

        fields = newFields;
    }

    @Override
    public BaseListField add(T value) {
        fields.add(value);
        return this;
    }

    @Override
    public List<Message> validate() {
        // TODO: tbatie - 3/16/17 - Validate list field
        return new ArrayList<>();
    }
}
