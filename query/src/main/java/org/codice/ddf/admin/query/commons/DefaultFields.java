package org.codice.ddf.admin.query.commons;

import java.util.List;

import org.codice.ddf.admin.query.api.ActionReport;
import org.codice.ddf.admin.query.api.field.Field;

public class DefaultFields {

    public static class IntegerField extends Field<Integer> {

        public IntegerField(String fieldName) {
            super(fieldName, FieldType.INTEGER);
        }

        @Override
        public ActionReport validate() {
            // TODO: tbatie - 2/11/17 - Validate
            return new DefaultActionReport();
        }

    }

    public static class StringField extends Field<String> {
        public StringField(String fieldName) {
            super(fieldName, FieldType.STRING);
        }

        @Override
        public ActionReport validate() {
            // TODO: tbatie - 2/11/17 - Validate
            return new DefaultActionReport();
        }

    }

    public abstract static class ListField<S> extends Field<List<S>> {

        public ListField(String fieldName) {
            super(fieldName, FieldType.LIST);
        }

        @Override
        public ActionReport validate() {
            // TODO: tbatie - 2/11/17 - Validate
            return new DefaultActionReport();
        }

    }
}
