package org.codice.ddf.admin.beta.fields.expression;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.codice.ddf.admin.api.report.Message;
import org.codice.ddf.admin.beta.FieldOperand;
import org.codice.ddf.admin.beta.fields.expression.attribute.AttributeExpression;
import org.codice.ddf.admin.common.fields.base.ListFieldImpl;
import org.opengis.filter.Filter;

import ddf.catalog.data.MetacardType;
import ddf.catalog.filter.FilterBuilder;

public class QueryField extends ListFieldImpl<AttributeExpression> implements FieldOperand {

    public QueryField(List<MetacardType> types) {
        super("query", new AttributeExpression(types));
    }

    @Override
    public Filter buildFilter(String attribute, Class attributeType, FilterBuilder builder) {
        List<Filter> filters = getList().stream()
                .map(field -> field.buildFilter(attribute, attributeType, builder))
                .collect(Collectors.toList());

        return FieldOperand.allOf(builder, filters);
    }

    // TODO: tbatie - 6/2/17 - Super hacky work around
    @Override
    public List<Message> validate() {
        return new ArrayList<>();
    }
}