package org.codice.ddf.admin.query.api.fields;

import org.codice.ddf.admin.query.commons.fields.base.scalar.BaseScalarField;

public interface ScalarField<T> {
    T getValue();
    BaseScalarField setValue(T value);
}
