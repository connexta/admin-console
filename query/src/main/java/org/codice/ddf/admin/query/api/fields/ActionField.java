package org.codice.ddf.admin.query.api.fields;

import java.util.List;
import java.util.Map;

public interface ActionField<T extends Field> extends Field {
    T process(Map<String, Object> args);
    List<Field> getArguments();
    Field getReturnField();
}
