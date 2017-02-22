package org.codice.ddf.admin.query.api.fields;

import org.codice.ddf.admin.query.commons.fields.base.ScalarBaseField;

public interface ScalarField<T> {
    T getValue();
    ScalarBaseField setValue(T value);
}
