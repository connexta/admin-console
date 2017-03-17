package org.codice.ddf.admin.query.api.fields;

import java.util.List;
import java.util.Map;

public interface ObjectField extends Field<Map<String, Object>> {
    List<Field> getFields();
}
