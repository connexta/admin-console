package org.codice.ddf.admin.beta;

import org.codice.ddf.admin.api.fields.ObjectField;
import org.opengis.filter.Filter;

import ddf.catalog.filter.FilterBuilder;

public interface FieldOperand extends ObjectField {
    Filter toFilter(FilterBuilder builder);
}
