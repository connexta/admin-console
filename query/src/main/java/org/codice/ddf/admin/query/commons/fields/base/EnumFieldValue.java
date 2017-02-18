package org.codice.ddf.admin.query.commons.fields.base;

public class EnumFieldValue<T> {

    private String name;
    private T value;
    private String description;

    public EnumFieldValue(String name, T value, String description) {
        this.name = name;
        this.value = value;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public T getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

}
