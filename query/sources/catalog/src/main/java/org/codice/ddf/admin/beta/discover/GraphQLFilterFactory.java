package org.codice.ddf.admin.beta.discover;

import java.util.Objects;
import java.util.stream.Collectors;

import org.opengis.filter.Filter;

import ddf.catalog.filter.EqualityExpressionBuilder;
import ddf.catalog.filter.FilterBuilder;

public class GraphQLFilterFactory {

    private FilterBuilder filterBuilder;
    private QueryField query;

    public GraphQLFilterFactory(FilterBuilder filterBuilder, QueryField queryToTransform) {
        this.filterBuilder = filterBuilder;
        this.query = queryToTransform;
    }

    public Filter buildFilter() {
    return filterBuilder.allOf(query.getOperands()
                .stream()
                .filter(Objects::nonNull)
                .map(operand -> operand.toFilter(filterBuilder))
                .collect(Collectors.toList()));
    }
}
