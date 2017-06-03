package org.codice.ddf.admin.beta.fields.expression.attribute;

import java.util.ArrayList;
import java.util.List;

import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.beta.FieldOperand;
import org.codice.ddf.admin.common.fields.base.BaseObjectField;
import org.codice.ddf.admin.common.fields.base.scalar.FloatField;
import org.codice.ddf.admin.common.fields.base.scalar.IntegerField;
import org.opengis.filter.Filter;

import com.google.common.collect.ImmutableList;

import ddf.catalog.filter.FilterBuilder;

public class NumericalExpression {

    public static class LessThan extends BaseObjectField implements FieldOperand {

        private IntegerField integerField;
        private FloatField floatField;

        public LessThan() {
            super("lessThan", "LessThan", "todo");
            integerField = new IntegerField();
            floatField = new FloatField();
            updateInnerFieldPaths();
        }

        @Override
        public List<Field> getFields() {
            return ImmutableList.of(integerField, floatField);
        }

        @Override
        public Filter buildFilter(String attribute, Class attributeType, FilterBuilder builder) {
            List<Filter> filters = new ArrayList<>();
            if(integerField.getValue() != null) {
                filters.add(builder.attribute(attribute)
                        .is()
                        .lessThan()
                        .number(integerField.getValue()));
            }

            if(floatField.getValue() != null) {
                filters.add(builder.attribute(attribute)
                        .is()
                        .lessThan()
                        .number(floatField.getValue()));
            }
            return FieldOperand.allOf(builder, filters);
        }
    }

    public static class LessThanOrEqualTo extends BaseObjectField implements FieldOperand {

        private IntegerField integerField;
        private FloatField floatField;

        public LessThanOrEqualTo() {
            super("lessThanOrEqualTo", "LessThanOrEqualTo", "todo");
            integerField = new IntegerField();
            floatField = new FloatField();
            updateInnerFieldPaths();
        }

        @Override
        public List<Field> getFields() {
            return ImmutableList.of(integerField, floatField);
        }

        @Override
        public Filter buildFilter(String attribute, Class attributeType, FilterBuilder builder) {
            List<Filter> filters = new ArrayList<>();
            if(integerField.getValue() != null) {
                filters.add(builder.attribute(attribute)
                        .is()
                        .lessThanOrEqualTo()
                        .number(integerField.getValue()));
            }

            if(floatField.getValue() != null) {
                filters.add(builder.attribute(attribute)
                        .is()
                        .lessThanOrEqualTo()
                        .number(floatField.getValue()));
            }
            return FieldOperand.allOf(builder, filters);
        }
    }

    public static class GreaterThan extends BaseObjectField implements FieldOperand {

        private IntegerField integerField;
        private FloatField floatField;

        public GreaterThan() {
            super("greaterThan", "GreaterThan", "todo");
            integerField = new IntegerField();
            floatField = new FloatField();
            updateInnerFieldPaths();
        }

        @Override
        public List<Field> getFields() {
            return ImmutableList.of(integerField, floatField);
        }

        @Override
        public Filter buildFilter(String attribute, Class attributeType, FilterBuilder builder) {
            List<Filter> filters = new ArrayList<>();
            if(integerField.getValue() != null) {
                filters.add(builder.attribute(attribute)
                        .is()
                        .greaterThan()
                        .number(integerField.getValue()));
            }

            if(floatField.getValue() != null) {
                filters.add(builder.attribute(attribute)
                        .is()
                        .greaterThan()
                        .number(floatField.getValue()));
            }
            return FieldOperand.allOf(builder, filters);
        }
    }

    public static class GreaterThanOrEqualTo extends BaseObjectField implements FieldOperand {

        private IntegerField integerField;
        private FloatField floatField;

        public GreaterThanOrEqualTo() {
            super("GreaterThanOrEqualTo", "GreaterThanOrEqualTo", "todo");
            integerField = new IntegerField();
            floatField = new FloatField();
            updateInnerFieldPaths();
        }

        @Override
        public List<Field> getFields() {
            return ImmutableList.of(integerField, floatField);
        }

        @Override
        public Filter buildFilter(String attribute, Class attributeType, FilterBuilder builder) {
            List<Filter> filters = new ArrayList<>();
            if(integerField.getValue() != null) {
                filters.add(builder.attribute(attribute)
                        .is()
                        .greaterThanOrEqualTo()
                        .number(integerField.getValue()));
            }

            if(floatField.getValue() != null) {
                filters.add(builder.attribute(attribute)
                        .is()
                        .greaterThanOrEqualTo()
                        .number(floatField.getValue()));
            }
            return FieldOperand.allOf(builder, filters);
        }
    }
}
