package org.codice.ddf.admin.beta;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.opengis.filter.Filter;

import ddf.catalog.filter.FilterBuilder;

public interface FieldOperand {
    Filter buildFilter(String attribute, Class attributeType, FilterBuilder builder);

    static Filter allOf(FilterBuilder builder, List<Filter> filters) {
        List<Filter> filtered = filters.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return filtered.isEmpty()? null : filtered.size() == 1? filtered.get(0) : builder.allOf(filtered);
    }

    static Filter anyOf(FilterBuilder builder, List<Filter> filters) {
        List<Filter> filtered = filters.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return filtered.isEmpty()? null : filtered.size() == 1? filtered.get(0) : builder.anyOf(filtered);
    }
}
