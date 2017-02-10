package org.codice.ddf.admin.query.api;

import java.util.List;

public interface Field<T> {
    String getUniqueName();
    String getDescription();
    T value();
    Class getValueClass();
    List<ActionMessage> validate();
}
