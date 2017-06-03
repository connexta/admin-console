package org.codice.ddf.admin.beta.fields.expression.query;

import java.util.List;
import java.util.stream.Collectors;

import org.codice.ddf.admin.beta.FieldOperand;
import org.codice.ddf.admin.beta.fields.expression.attribute.AttributeExpression;
import org.codice.ddf.admin.common.fields.base.ListFieldImpl;
import org.opengis.filter.Filter;

import ddf.catalog.data.MetacardType;
import ddf.catalog.filter.FilterBuilder;

public class AllOfExpression extends ListFieldImpl<AttributeExpression> implements FieldOperand {

    public AllOfExpression(List<MetacardType> types) {
        super("allOf", new AttributeExpression(types));
    }

    @Override
    public Filter buildFilter(String attribute, Class attributeType, FilterBuilder builder) {
        List<Filter> filters = getList().stream()
                .map(field -> field.buildFilter(attribute, attributeType, builder))
                .collect(Collectors.toList());

        return FieldOperand.anyOf(builder, filters);
    }
}
