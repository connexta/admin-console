package org.codice.ddf.admin.query.commons.fields.base;

public class EnumFieldValue<T>{

    // TODO: tbatie - 2/26/17 - Looks identical to a scalar... wink wink
    // Implement the EnumField interface, remove this class and replace with scalarType field

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

    public void setValue(T value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

}
