package org.codice.ddf.admin.beta.fields;

import java.util.List;

import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.beta.FieldOperand;
import org.codice.ddf.admin.beta.OperandProvider;
import org.codice.ddf.admin.common.fields.base.BaseObjectField;
import org.codice.ddf.admin.common.fields.base.scalar.IntegerField;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;
import org.opengis.filter.Filter;

import com.google.common.collect.ImmutableList;

import ddf.catalog.filter.FilterBuilder;

public class IntegerAttributeOperands extends BaseObjectField implements OperandProvider {

    private IntegerEquals equals;
    private IntegerNotEqualTo notEqualTo;
    private IntegerLessThan lessThan;
    private IntegerLessThanOrEqualTo equalTo;
    private IntegerGreaterThan greaterThan;
    private IntegerGreaterThanOrEqualTo greaterThanOrEqualTo;
    private IntegerBetween between;

    public IntegerAttributeOperands() {
        super("integer", "IntegerAttributeOperands", "Supported query operands for integer attributes");
        equals = new IntegerEquals();
        notEqualTo = new IntegerNotEqualTo();
        lessThan = new IntegerLessThan();
        equalTo = new IntegerLessThanOrEqualTo();
        greaterThan = new IntegerGreaterThan();
        greaterThanOrEqualTo = new IntegerGreaterThanOrEqualTo();
        between = new IntegerBetween();
        updateInnerFieldPaths();
    }

    @Override
    public List<Field> getFields() {
        return ImmutableList.of(equals, notEqualTo, lessThan, equalTo, greaterThan, greaterThanOrEqualTo, between);
    }

    @Override
    public List<FieldOperand> getFieldOperands() {
        return ImmutableList.of(equals, notEqualTo, lessThan, equalTo, greaterThan, greaterThanOrEqualTo, between);
    }



    //    Integer specific

    //    public EqualityExpressionBuilder equalTo();

    //    public NumericalExpressionBuilder lessThan();
    //
    //    public NumericalExpressionBuilder lessThanOrEqualTo();
    //
    //    public NumericalExpressionBuilder greaterThan();
    //
    //    public NumericalExpressionBuilder greaterThanOrEqualTo();
    //

    public class IntegerEquals extends BaseObjectField implements FieldOperand {
        private StringField attribute;
        private IntegerField value;

        public IntegerEquals() {
            super("equals", "IntegerEquals", "Represents a filter that requires the specified attribute to match the given value.");
            attribute = new StringField("attribute");
            value = new IntegerField("value");
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
                    .number(value.getValue());
        }
    }

    public class IntegerLessThan extends BaseObjectField implements FieldOperand {
        private StringField attribute;
        private IntegerField value;

        public IntegerLessThan() {
            super("lessThan", "IntegerLessThan", "Represents a filter that requires the specified attribute to match the given value.");
            attribute = new StringField("attribute");
            value = new IntegerField("value");
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
                    .lessThan()
                    .number(value.getValue());
        }
    }

    public class IntegerLessThanOrEqualTo extends BaseObjectField implements FieldOperand {
        private StringField attribute;
        private IntegerField value;

        public IntegerLessThanOrEqualTo() {
            super("lessThanOrEqualTo", "IntegerLessThanOrEqualTo", "Represents a filter that requires the specified attribute to match the given value.");
            attribute = new StringField("attribute");
            value = new IntegerField("value");
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
                    .lessThanOrEqualTo()
                    .number(value.getValue());
        }
    }

    public class IntegerGreaterThan extends BaseObjectField implements FieldOperand {
        private StringField attribute;
        private IntegerField value;

        public IntegerGreaterThan() {
            super("greaterThan", "IntegerGreaterThan", "Represents a filter that requires the specified attribute to match the given value.");
            attribute = new StringField("attribute");
            value = new IntegerField("value");
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
                    .greaterThan()
                    .number(value.getValue());
        }
    }

    public class IntegerGreaterThanOrEqualTo extends BaseObjectField implements FieldOperand {
        private StringField attribute;
        private IntegerField value;

        public IntegerGreaterThanOrEqualTo() {
            super("greaterThanOrEqualTo", "IntegerGreaterThanOrEqualTo", "Represents a filter that requires the specified attribute to match the given value.");
            attribute = new StringField("attribute");
            value = new IntegerField("value");
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
                    .greaterThanOrEqualTo()
                    .number(value.getValue());
        }
    }

    public class IntegerBetween extends BaseObjectField implements FieldOperand {
        private StringField attribute;
        private IntegerField bottom;
        private IntegerField top;

        public IntegerBetween() {
            super("between", "IntegerBetween", "Represents a filter that requires the specified attribute to match the given value.");
            attribute = new StringField("attribute");
            bottom = new IntegerField("bottom");
            top = new IntegerField("top");
            updateInnerFieldPaths();
        }

        @Override
        public List<Field> getFields() {
            return ImmutableList.of(attribute, bottom, top);
        }

        @Override
        public Filter toFilter(FilterBuilder builder) {
            if(attribute.getValue() == null || bottom.getValue() == null || top.getValue() == null) {
                return null;
            }

            return builder.attribute(attribute.getValue())
                    .between()
                    .numbers(bottom.getValue(), top.getValue());
        }
    }

    public class IntegerNotEqualTo extends BaseObjectField implements FieldOperand {
        private StringField attribute;
        private IntegerField value;

        public IntegerNotEqualTo() {
            super("notEqualTo", "IntegerNotEqualTo", "Represents a filter that requires the specified attribute to match the given value.");
            attribute = new StringField("attribute");
            value = new IntegerField("value");
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
                    .notEqualTo()
                    .number(value.getValue());
        }
    }
}
