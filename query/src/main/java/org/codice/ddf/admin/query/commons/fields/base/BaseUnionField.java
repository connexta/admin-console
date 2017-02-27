package org.codice.ddf.admin.query.commons.fields.base;

import java.util.List;

import org.codice.ddf.admin.query.api.fields.ObjectField;
import org.codice.ddf.admin.query.api.fields.UnionField;

public abstract class BaseUnionField extends BaseField implements UnionField {

    private List<ObjectField> unionTypes;

    public BaseUnionField(String fieldTypeName, String description, List<ObjectField> unionTypes) {
        super(null, fieldTypeName, description, FieldBaseType.UNION);
        this.unionTypes = unionTypes;
    }

    @Override
    public List<ObjectField> getUnionTypes() {
        return unionTypes;
    }

    @Override
    public Object getValue() {
        return null;
    }
}
