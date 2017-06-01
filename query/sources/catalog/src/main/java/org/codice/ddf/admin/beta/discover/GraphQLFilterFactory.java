package org.codice.ddf.admin.beta.discover;

import java.util.Objects;
import java.util.stream.Collectors;

import org.opengis.filter.Filter;

import ddf.catalog.filter.FilterBuilder;

public class GraphQLFilterFactory {

    private FilterBuilder filterBuilder;
    private QueryField query;

    public GraphQLFilterFactory(FilterBuilder filterBuilder, QueryField queryToTransform) {
        this.filterBuilder = filterBuilder;
        this.query = queryToTransform;
    }

    public Filter buildFilter() {
        return filterBuilder.allOf(query.getFieldOperands()
                .stream()
                .map(operand -> operand.toFilter(filterBuilder))
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
    }
}
