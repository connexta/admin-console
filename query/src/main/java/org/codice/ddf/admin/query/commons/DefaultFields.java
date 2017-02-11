package org.codice.ddf.admin.query.commons;

import java.util.List;

import org.codice.ddf.admin.query.api.ActionReport;
import org.codice.ddf.admin.query.api.Field;

public class DefaultFields {

    public static class IntegerField extends Field<Integer> {

        public IntegerField(String fieldName) {
            super(fieldName, "integer");
        }

        protected IntegerField(String fieldName, String fieldType) {
            super(fieldName, fieldType);
        }

        @Override
        public ActionReport validate() {
            return null;
        }

        @Override
        public Class getValueClass() {
            return null;
        }
    }

    public static class StringField extends Field<String> {
        public StringField(String fieldName) {
            super(fieldName, "string");
        }

        protected StringField(String fieldName, String fieldType) {
            super(fieldName, fieldType);
        }

        @Override
        public ActionReport validate() {
            return null;
        }

        @Override
        public Class getValueClass() {
            return String.class;
        }
    }

    public abstract static class ListField<S> extends Field<List<S>> {

        public ListField(String fieldName) {
            super(fieldName,"list");
        }

        protected ListField(String fieldName, String fieldType) {
            super(fieldName,fieldType);
        }

        @Override
        public ActionReport validate() {
            return null;
        }

        @Override
        public Class getValueClass() {
            return List.class;
        }
    }
}
