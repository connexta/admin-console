package org.codice.ddf.admin.beta.fields.expression.attribute;

import java.util.List;
import java.util.stream.Collectors;

import org.codice.ddf.admin.beta.FieldOperand;
import org.codice.ddf.admin.common.fields.base.ListFieldImpl;
import org.opengis.filter.Filter;

import ddf.catalog.filter.FilterBuilder;


public class AttributeOrExpression extends ListFieldImpl<IsAttributeExpression> implements FieldOperand {

    public AttributeOrExpression() {
        super("or", new IsAttributeExpression());
    }

    @Override
    public Filter buildFilter(String attribute, Class attributeType, FilterBuilder builder) {
        List<Filter> filters = getList().stream()
                .map(field -> field.buildFilter(attribute, attributeType, builder))
                .collect(Collectors.toList());

        return FieldOperand.anyOf(builder, filters);
    }
}
