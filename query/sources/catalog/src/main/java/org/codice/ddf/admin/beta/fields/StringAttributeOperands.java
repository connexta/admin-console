package org.codice.ddf.admin.beta.fields;

import java.util.List;

import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.beta.FieldOperand;
import org.codice.ddf.admin.beta.OperandProvider;
import org.codice.ddf.admin.common.fields.base.BaseObjectField;
import org.codice.ddf.admin.common.fields.base.scalar.BooleanField;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;
import org.opengis.filter.Filter;

import com.google.common.collect.ImmutableList;

import ddf.catalog.filter.ContextualExpressionBuilder;
import ddf.catalog.filter.FilterBuilder;

public class StringAttributeOperands extends BaseObjectField implements OperandProvider {

    private StringEquals equals;
    private StringLike like;

    public StringAttributeOperands() {
        super("string", "StringAttributeOperands", "Supported query operands for String attributes");
        equals = new StringEquals();
        like = new StringLike();
        updateInnerFieldPaths();
    }

    @Override
    public List<Field> getFields() {
        return ImmutableList.of(equals, like);
    }

    @Override
    public List<FieldOperand> getFieldOperands() {
        return ImmutableList.of(equals, like);
    }

    public class StringEquals extends BaseObjectField implements FieldOperand {
        private StringField attribute;
        private StringField value;

        public StringEquals() {
            super("equals", "StringEquals", "Represents a filter that requires the specified attribute to match the given value.");
            attribute = new StringField("attribute");
            value = new StringField("value");
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
                    .text(value.getValue());
        }
    }

    public class StringLike extends BaseObjectField implements FieldOperand {
        private StringField attribute;
        private StringField value;
        private BooleanField isFuzzy;
        private BooleanField isCaseSensitive;

        public StringLike() {
            super("like", "StringLike", "Represents a filter that requires the specified attribute to match the given value.");
            attribute = new StringField("attribute");
            value = new StringField("value");
            isFuzzy = new BooleanField("isFuzzySearch");
            isCaseSensitive = new BooleanField("isCaseSensitive");
            updateInnerFieldPaths();
        }

        @Override
        public List<Field> getFields() {
            return ImmutableList.of(attribute, value, isFuzzy, isCaseSensitive);
        }

        @Override
        public Filter toFilter(FilterBuilder builder) {
            if(attribute.getValue() == null || value.getValue() == null) {
                return null;
            }

            ContextualExpressionBuilder exp = builder.attribute(attribute.getValue())
                    .is()
                    .like();

            if(isFuzzy.getValue()) {
                return exp.fuzzyText(value.getValue());
            } else if(isCaseSensitive.getValue()) {
                return exp.caseSensitiveText(value.getValue());
            } else {
                return exp.text(value.getValue());
            }
        }
    }
}
