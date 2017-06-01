package org.codice.ddf.admin.beta.discover;

import java.util.List;

import org.codice.ddf.admin.api.Field;
import org.codice.ddf.admin.common.fields.base.BaseObjectField;
import org.codice.ddf.admin.common.fields.base.scalar.BooleanField;
import org.codice.ddf.admin.common.fields.base.scalar.IntegerField;
import org.codice.ddf.admin.common.fields.base.scalar.StringField;
import org.opengis.filter.Filter;

import com.google.common.collect.ImmutableList;

import ddf.catalog.filter.FilterBuilder;

public class QueryField extends BaseObjectField {

    public static final String DEFAULT_FIELD_NAME = "query";
    public static final String DESCRIPTION = "Defines a query to be executed against the catalog framework.";

    private StringEquals stringEquals;
    private IntegerEquals integerEquals;
    private BooleanEquals booleanEquals;

    protected QueryField() {
        super(DEFAULT_FIELD_NAME, "CatalogQuery", DESCRIPTION);
        stringEquals = new StringEquals();
        integerEquals = new IntegerEquals();
        booleanEquals = new BooleanEquals();
        updateInnerFieldPaths();
    }

    public List<EqualsField> getOperands() {
        return ImmutableList.of(stringEquals, integerEquals, booleanEquals);
    }

    @Override
    public List<Field> getFields() {
        return ImmutableList.of(stringEquals, integerEquals, booleanEquals);
    }

    public interface EqualsField {
        Filter toFilter(FilterBuilder builder);
    }

    public class StringEquals extends BaseObjectField implements EqualsField {
        private StringField attribute;
        private StringField value;

        public StringEquals() {
            super("stringEquals", "StringEquals", "Represents a filter that requires the specified attribute to match the given value.");
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
            if(attribute == null || value == null) {
                return null;
            }

            return builder.attribute(attribute.getValue())
                    .is()
                    .equalTo()
                    .text(value.getValue());
        }
    }

    public class IntegerEquals extends BaseObjectField implements EqualsField {
        private StringField attribute;
        private IntegerField value;

        public IntegerEquals() {
            super("integerEquals", "IntegerEquals", "Represents a filter that requires the specified attribute to match the given value.");
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
            if(attribute == null || value == null) {
                return null;
            }

            return builder.attribute(attribute.getValue())
                    .is()
                    .equalTo()
                    .number(value.getValue());
        }
    }

    public class BooleanEquals extends BaseObjectField implements EqualsField {
        private StringField attribute;
        private BooleanField value;

        public BooleanEquals() {
            super("booleanEquals", "BooleanEquals", "Represents a filter that requires the specified attribute to match the given value.");
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
            if(attribute == null || value == null) {
                return null;
            }

            return builder.attribute(attribute.getValue())
                    .is()
                    .equalTo()
                    .bool(value.getValue());
        }
    }

    //    STRING,
    //
    //    BOOLEAN,
    //
    //    DATE,
    //
    //    SHORT,
    //
    //    INTEGER,
    //
    //    LONG,
    //
    //    FLOAT,
    //
    //    DOUBLE,
    //
    //    GEOMETRY,
    //
    //    BINARY,
    //
    //    XML,
    //
    //    OBJECT
}
