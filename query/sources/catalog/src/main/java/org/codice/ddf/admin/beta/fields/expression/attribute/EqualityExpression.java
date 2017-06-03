package org.codice.ddf.admin.beta.fields.expression.attribute;

import java.util.ArrayList;
import java.util.List;

import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.beta.FieldOperand;
import org.codice.ddf.admin.common.fields.base.BaseObjectField;
import org.codice.ddf.admin.common.fields.base.scalar.BooleanField;
import org.codice.ddf.admin.common.fields.base.scalar.FloatField;
import org.codice.ddf.admin.common.fields.base.scalar.IntegerField;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;
import org.opengis.filter.Filter;

import com.google.common.collect.ImmutableList;

import ddf.catalog.filter.FilterBuilder;

public class EqualityExpression {

    public static class EqualTo extends BaseObjectField implements FieldOperand {

        private StringField stringF;
        private IntegerField integerF;
        private FloatField floatF;
        private BooleanField booleanF;

        // TODO: tbatie - 6/2/17 -
        //    wkt
        //    date
        //    dateRange
        //    bytes[]

        public EqualTo() {
            super("equalTo", "EqualTo", "todo");
            stringF = new StringField();
            integerF = new IntegerField();
            floatF = new FloatField();
            booleanF = new BooleanField();
            updateInnerFieldPaths();
        }

        @Override
        public List<Field> getFields() {
            return ImmutableList.of(stringF, integerF, floatF, booleanF);
        }

        @Override
        public Filter buildFilter(String attribute, Class attributeType, FilterBuilder builder) {
            List<Filter> filters = new ArrayList<>();
            if(stringF.getValue() != null) {
                filters.add(builder.attribute(attribute)
                        .is()
                        .equalTo()
                        .text(stringF.getValue()));
            }

            if(integerF.getValue() != null) {
                filters.add(builder.attribute(attribute)
                        .is()
                        .equalTo()
                        .number(integerF.getValue()));
            }

            if(floatF.getValue() != null) {
                filters.add(builder.attribute(attribute)
                        .is()
                        .equalTo()
                        .number(floatF.getValue()));
            }

            if(booleanF.getValue() != null) {
                filters.add(builder.attribute(attribute)
                        .is()
                        .equalTo()
                        .bool(booleanF.getValue()));
            }

            return FieldOperand.allOf(builder, filters);
        }
    }

    public static class NotEqualTo extends BaseObjectField implements FieldOperand {

        private StringField stringF;
        private IntegerField integerF;
        private FloatField floatF;
        private BooleanField booleanF;

        // TODO: tbatie - 6/2/17 -
        //    wkt
        //    date
        //    dateRange
        //    bytes[]

        public NotEqualTo() {
            super("notEqualTo", "NotEqualTo", "todo");
            stringF = new StringField();
            integerF = new IntegerField();
            floatF = new FloatField();
            booleanF = new BooleanField();
            updateInnerFieldPaths();
        }

        @Override
        public List<Field> getFields() {
            return ImmutableList.of(stringF, integerF, floatF, booleanF);
        }

        @Override
        public Filter buildFilter(String attribute, Class attributeType, FilterBuilder builder) {
            List<Filter> filters = new ArrayList<>();
            if(stringF.getValue() != null) {
                filters.add(builder.attribute(attribute)
                        .is()
                        .notEqualTo()
                        .text(stringF.getValue()));
            }

            if(integerF.getValue() != null) {
                filters.add(builder.attribute(attribute)
                        .is()
                        .notEqualTo()
                        .number(integerF.getValue()));
            }

            if(floatF.getValue() != null) {
                filters.add(builder.attribute(attribute)
                        .is()
                        .notEqualTo()
                        .number(floatF.getValue()));
            }

            if(booleanF.getValue() != null) {
                filters.add(builder.attribute(attribute)
                        .is()
                        .notEqualTo()
                        .bool(booleanF.getValue()));
            }

            return FieldOperand.allOf(builder, filters);
        }
    }
}
