package org.codice.ddf.admin.beta.fields.expression.attribute;

import java.util.List;
import java.util.stream.Collectors;

import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.beta.FieldOperand;
import org.codice.ddf.admin.common.fields.base.BaseObjectField;
import org.opengis.filter.Filter;

import com.google.common.collect.ImmutableList;

import ddf.catalog.filter.FilterBuilder;

public class IsAttributeExpression extends BaseObjectField implements FieldOperand {
    private NumericalExpression.LessThan lessThan;

    private NumericalExpression.LessThanOrEqualTo lessThanOrEqualTo;

    private NumericalExpression.GreaterThan greaterThan;

    private NumericalExpression.GreaterThanOrEqualTo greaterThanOrEqualTo;

    private EqualityExpression.EqualTo equalTo;

    private EqualityExpression.NotEqualTo notEqualTo;

    // TODO: tbatie - 6/2/17 - Add remaining expressions
    private ContextualExpression.Like like;

    public IsAttributeExpression() {
        super("is", "Is", "todo");

        lessThan = new NumericalExpression.LessThan();
        lessThanOrEqualTo = new NumericalExpression.LessThanOrEqualTo();
        greaterThan = new NumericalExpression.GreaterThan();
        greaterThanOrEqualTo = new NumericalExpression.GreaterThanOrEqualTo();

        equalTo = new EqualityExpression.EqualTo();
        notEqualTo = new EqualityExpression.NotEqualTo();

        like = new ContextualExpression.Like();
        updateInnerFieldPaths();
    }

    @Override
    public List<Field> getFields() {
        return ImmutableList.of(lessThan,
                lessThanOrEqualTo,
                greaterThan,
                greaterThanOrEqualTo,
                equalTo,
                notEqualTo,
                like);
    }

    @Override
    public Filter buildFilter(String attribute, Class attributeType, FilterBuilder builder) {
        List<Filter> filters = getFields().stream()
                .map(field -> ((FieldOperand) field).buildFilter(attribute, attributeType, builder))
                .collect(Collectors.toList());

        return FieldOperand.allOf(builder, filters);
    }
}
