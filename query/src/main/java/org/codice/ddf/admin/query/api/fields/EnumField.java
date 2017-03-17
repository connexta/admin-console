package org.codice.ddf.admin.query.api.fields;

import java.util.List;

public interface EnumField<S, T extends Field<S>> extends Field<S> {
    List<T> getEnumValues();
}
