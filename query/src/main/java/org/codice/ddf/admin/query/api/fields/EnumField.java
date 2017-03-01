package org.codice.ddf.admin.query.api.fields;

import java.util.List;

public interface EnumField<T> extends Field {

    List<T> getEnumValues();
}
