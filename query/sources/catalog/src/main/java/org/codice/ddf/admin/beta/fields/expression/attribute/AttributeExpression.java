package org.codice.ddf.admin.beta.fields.expression.attribute;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.beta.FieldOperand;
import org.codice.ddf.admin.beta.fields.expression.attribute.IsAttributeExpression;
import org.codice.ddf.admin.beta.fields.expression.attribute.AttributeOrExpression;
import org.codice.ddf.admin.beta.types.MetacardAttributeField;
import org.codice.ddf.admin.common.fields.base.BaseObjectField;
import org.opengis.filter.Filter;

import com.google.common.collect.ImmutableList;

import ddf.catalog.data.MetacardType;
import ddf.catalog.filter.FilterBuilder;

public class AttributeExpression extends BaseObjectField implements FieldOperand {

    public static final String DEFAULT_FIELD_NAME = "attributeQuery";
    public static final String FIELD_TYPE_NAME = "AttributeExpression";
    public static final String DESCRIPTION = "An expression representing a specific query for an attribute";

    private MetacardAttributeField attribute;
    private IsAttributeExpression is;
    private AttributeOrExpression or;

    public AttributeExpression() {
        this(Collections.emptyList());
    }

    public AttributeExpression(List<MetacardType> metacardTypes) {
        super(DEFAULT_FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
        attribute = new MetacardAttributeField(metacardTypes);
        attribute.isRequired(true);
        is = new IsAttributeExpression();
        or = new AttributeOrExpression();
        updateInnerFieldPaths();
    }

    @Override
    public List<Field> getFields() {
        return ImmutableList.of(attribute, is, or);
    }

    @Override
    public Filter buildFilter(String attribute, Class attributeType, FilterBuilder builder) {
        List<Filter> filters = Arrays.asList(is.buildFilter(this.attribute.getValue(), null, builder),
                or.buildFilter(this.attribute.getValue(), null, builder));

        return FieldOperand.anyOf(builder, filters);
    }

}
