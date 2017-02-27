package org.codice.ddf.admin.query.commons.fields.base;

import org.codice.ddf.admin.query.api.fields.Field;

public abstract class BaseField implements Field {

    private String fieldName;
    private String fieldTypeName;
    private String description;
    private FieldBaseType fieldBaseType;

    public BaseField(String fieldName, String fieldTypeName, String description, FieldBaseType fieldBaseType) {
        this.fieldName = fieldName;
        this.fieldTypeName = fieldTypeName;
        this.fieldBaseType = fieldBaseType;
        this.description = description;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    @Override
    public String fieldTypeName() {
        return fieldTypeName;
    }

    @Override
    public FieldBaseType fieldBaseType() {
        return fieldBaseType;
    }

    @Override
    public String description() {
        return description;
    }

}
