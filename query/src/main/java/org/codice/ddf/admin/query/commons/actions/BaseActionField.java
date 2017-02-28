package org.codice.ddf.admin.query.commons.actions;

import static org.codice.ddf.admin.query.api.fields.Field.FieldBaseType.ACTION;

import java.util.Map;

import org.codice.ddf.admin.query.api.fields.ActionField;
import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.commons.fields.base.BaseField;

public abstract class BaseActionField<T extends Field> extends BaseField implements ActionField<T> {

    private Field returnFieldType;

    public BaseActionField(String fieldName, String description, Field returnFieldType) {
        super(fieldName, null, description, ACTION);
        this.returnFieldType = returnFieldType;
    }

    @Override
    public Map<String, Object> getValue() {
//        Map<String, Object> value = new HashMap<>();
//        getArguments().forEach(field -> value.put(field.fieldName(), field.getValue()));
//        return value;
        // TODO: tbatie - 2/27/17 - Need to re evaluate if getValue at the Field level makes sense, this is the second time it doesn't. In reality process should be called, getValue will not return anything useful
        return null;
    }

    @Override
    public Field getReturnField() {
        return returnFieldType;
    }
}
