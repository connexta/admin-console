package org.codice.ddf.admin.query.commons.fields.base;

import static org.codice.ddf.admin.query.api.fields.Field.FieldBaseType.OBJECT;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codice.ddf.admin.query.api.fields.InterfaceField;
import org.codice.ddf.admin.query.api.fields.ObjectField;

public abstract class BaseObjectField extends BaseField implements ObjectField, InterfaceField {

    private List<InterfaceField> interfaces;

    public BaseObjectField(String fieldName, String fieldTypeName, String description, InterfaceField... interfaces) {
        super(fieldName, fieldTypeName, description, OBJECT);
        this.interfaces = new ArrayList<>();
        if(interfaces != null) {
            this.interfaces.addAll(Arrays.asList(interfaces));
        }
    }

    protected BaseObjectField(String fieldName, String fieldTypeName, String description, FieldBaseType baseType, InterfaceField... interfaces) {
        super(fieldName, fieldTypeName, description, baseType);
        this.interfaces = new ArrayList<>();
        if(interfaces != null) {
            this.interfaces.addAll(Arrays.asList(interfaces));
        }
    }

    @Override
    public Map<String, Object> getValue() {
        Map<String, Object> value = new HashMap<>();
        getFields().forEach(field -> value.put(field.fieldName(), field.getValue()));
        return value;
    }

    @Override
    public List<InterfaceField> getInterfaces() {
        return interfaces;
    }
}
