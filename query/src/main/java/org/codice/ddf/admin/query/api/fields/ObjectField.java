package org.codice.ddf.admin.query.api.fields;

import java.util.List;
import java.util.Map;

public interface ObjectField extends Field {

    List<InterfaceField> getInterfaces();

    List<Field> getFields();

    @Override
    Map<String, Object> getValue();
}
