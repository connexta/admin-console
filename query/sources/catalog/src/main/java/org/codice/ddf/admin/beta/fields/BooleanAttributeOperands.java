package org.codice.ddf.admin.beta.fields;

import java.util.List;

import org.codice.ddf.admin.beta.FieldOperand;
import org.codice.ddf.admin.beta.OperandProvider;
import org.opengis.filter.Filter;
import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.common.fields.base.BaseObjectField;
import org.codice.ddf.admin.common.fields.base.scalar.BooleanField;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;

import com.google.common.collect.ImmutableList;

import ddf.catalog.filter.FilterBuilder;

public class BooleanAttributeOperands extends BaseObjectField implements OperandProvider {

    private BooleanEquals equals;

    public BooleanAttributeOperands() {
        super("boolean", "BooleanAttributeOperands", "Supported query operands for Boolean attributes");
        equals = new BooleanEquals();
        updateInnerFieldPaths();
    }

    @Override
    public List<Field> getFields() {
        return ImmutableList.of(equals);
    }

    @Override
    public List<FieldOperand> getFieldOperands() {
        return ImmutableList.of(equals);
    }

    public class BooleanEquals extends BaseObjectField implements FieldOperand {
        private StringField attribute;
        private BooleanField value;

        public BooleanEquals() {
            super("equals", "BooleanEquals", "Represents a filter that requires the specified attribute to match the given value.");
            attribute = new StringField("attribute");
            value = new BooleanField("value");
            updateInnerFieldPaths();
        }

        @Override
        public List<Field> getFields() {
            return ImmutableList.of(attribute, value);
        }

        @Override
        public Filter toFilter(FilterBuilder builder) {
            if(attribute.getValue() == null || value.getValue() == null) {
                return null;
            }

            return builder.attribute(attribute.getValue())
                    .is()
                    .equalTo()
                    .bool(value.getValue());
        }
    }
}
