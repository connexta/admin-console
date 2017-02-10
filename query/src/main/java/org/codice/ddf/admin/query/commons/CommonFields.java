package org.codice.ddf.admin.query.commons;

import java.util.Arrays;
import java.util.List;

import org.codice.ddf.admin.query.api.ActionHandlerFields;
import org.codice.ddf.admin.query.api.ActionMessage;
import org.codice.ddf.admin.query.api.Field;

public class CommonFields implements ActionHandlerFields {

    @Override
    public List<Field> allFields() {
        return Arrays.asList(new ActionId(), new HostName(), new HostNameList());
    }

    public class HostNameList extends ListField<HostName> {

        public HostNameList() {

        }

        public HostNameList(List<HostName> value) {
            super(value);
        }

        @Override
        public String getUniqueName() {
            return "hostNames";
        }

        @Override
        public Class getValueClass() {
            return List.class;
        }
    }

    public class HostName extends StringField {

        public HostName() {

        }
        public HostName(String value) {
            super(value);
        }

        @Override
        public String getUniqueName() {
            return "hostName";
        }

        @Override
        public String getDescription() {
            return null;
        }
    }

    public static class ActionId extends StringField {
        public ActionId() {
        }

        public ActionId(String value) {
            super(value);
        }

        @Override
        public String getUniqueName() {
            return "actionId";
        }

        @Override
        public String getDescription() {
            return "Unique id of a specific actionType";
        }
    }

    public abstract static class ListField<S> implements Field<List<S>> {

        private List<S> value;

        public ListField() {

        }

        public ListField(List<S> value) {
            this.value = value;
        }

        @Override
        public String getDescription() {
            return null;
        }

        @Override
        public List<S> value() {
            return value;
        }

        @Override
        public List<ActionMessage> validate() {
            return null;
        }
    }

    public abstract static class StringField implements Field<String> {

        private String value;

        public StringField() {

        }

        public StringField(String value) {
            this.value = value;
        }

        @Override
        public Class<String> getValueClass() {
            return String.class;
        }

        @Override
        public String value() {
            return value;
        }

        @Override
        public List<ActionMessage> validate() {
            return null;
        }
    }

}
