package org.codice.ddf.admin.query.api.fields;

import java.util.List;

public interface ListField<T extends Field> extends Field {
    Field getListValueField();
    List<T> getFields();
}
