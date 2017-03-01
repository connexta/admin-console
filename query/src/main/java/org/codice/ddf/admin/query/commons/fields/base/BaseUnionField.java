package org.codice.ddf.admin.query.commons.fields.base;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codice.ddf.admin.query.api.fields.ObjectField;
import org.codice.ddf.admin.query.api.fields.UnionField;

public abstract class BaseUnionField extends BaseObjectField implements UnionField {

    public static final String FIELD_TYPE_NAME_KEY = "fieldTypeName";
    private List<ObjectField> unionTypes;

    public BaseUnionField(String fieldName, String fieldTypeName, String description, List<ObjectField> unionTypes, boolean isUnionValue) {
        super(fieldName, fieldTypeName, description, isUnionValue ? FieldBaseType.OBJECT : FieldBaseType.UNION);
        this.unionTypes = unionTypes;
    }

    @Override
    public Map<String, Object> getValue() {
        Map<String, Object> value = new HashMap<>();
        getFields().forEach(field -> value.put(field.fieldName(), field.getValue()));
        value.put(FIELD_TYPE_NAME_KEY, fieldTypeName());
        return value;
    }

    @Override
    public List<ObjectField> getUnionTypes() {
        return unionTypes;
    }
}
