package org.codice.ddf.admin.query.commons.field;

import static org.codice.ddf.admin.query.api.field.Field.FieldType.ENUM;
import static org.codice.ddf.admin.query.api.field.Field.FieldType.INTEGER;
import static org.codice.ddf.admin.query.api.field.Field.FieldType.LIST;
import static org.codice.ddf.admin.query.api.field.Field.FieldType.OBJECT;
import static org.codice.ddf.admin.query.api.field.Field.FieldType.STRING;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.codice.ddf.admin.query.api.field.ActionHandlerFields;
import org.codice.ddf.admin.query.api.field.Field;
import org.codice.ddf.admin.query.api.field.Message;
import org.codice.ddf.admin.query.api.field.Report;

import com.google.common.collect.ImmutableList;

public class BaseFields implements ActionHandlerFields {

    @Override
    public java.util.List<Field> allFields() {
        return null;
    }

    public static class IntegerField extends BaseField<Integer> {

        public IntegerField(String fieldName) {
            super(fieldName, INTEGER);
        }

        @Override
        public String description() {
            return null;
        }

        @Override
        public Integer getValue() {
            return null;
        }
    }

    public static class StringField extends BaseField<String> {

        public StringField(String fieldName) {
            super(fieldName, STRING);
        }

        @Override
        public String description() {
            return null;
        }
    }

    public abstract static class List<T extends Field> extends BaseField<T> {

        public List(String fieldName) {
            super(fieldName, LIST);
        }

        public abstract Field getListValueField();
    }

    public static abstract class ObjectField extends BaseField {

        public ObjectField(String fieldName) {
            super(fieldName, OBJECT);
        }

        public abstract java.util.List<Field> getFields();
    }

    public static class EnumValue<T> {
        private String name;
        private T value;
        private String description;

        public EnumValue(String name, T value, String description) {
            this.name = name;
            this.value = value;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public T getValue() {
            return value;
        }

        public String getDescription() {
            return description;
        }
    }

    public static abstract class EnumField<T> extends BaseField<EnumValue<T>> {

        public EnumField(String fieldName) {
            super(fieldName, ENUM);
        }

        public abstract java.util.List<EnumValue<T>> getEnumValues();
    }

    public static class MessageField extends BaseFields.ObjectField implements Message {

        public static final String MESSAGE = "message";
        public static final String CODE = "code";
        public static final String DESCRIPTION = "description";
        public static final java.util.List<Field> FIELDS = ImmutableList.of(new BaseFields.StringField(CODE), new BaseFields.StringField(DESCRIPTION));

        private String code;
        private String description;
        private MessageType messageType;

        public MessageField(String code, String description, MessageType messageType) {
            super(MESSAGE);
            this.code = code;
            this.description = description;
            this.messageType = messageType;
        }

        @Override
        public java.util.List<Field> getFields() {
            return FIELDS;
        }

        @Override
        public String getCode() {
            return code;
        }

        @Override
        public String description() {
            return description;
        }

        @Override
        public MessageType getMessageType() {
            return messageType;
        }
    }

    public static class SuccessMessage extends MessageField {
        public SuccessMessage(String code, String description) {
            super(code, description, MessageType.SUCCESS);
        }
    }

    public static class FailureMessage extends MessageField {

        public FailureMessage(String code, String description) {
            super(code, description, MessageType.FAILURE);
        }
    }

    public static class WarningMessage extends MessageField {

        public WarningMessage(String code, String description) {
            super(code, description, MessageType.WARNING);
        }
    }

    public static class MessageList extends BaseFields.List {

        public MessageList(String fieldName) {
            super(fieldName);
        }

        @Override
        public String description() {
            return "A list containing messages.";
        }

        @Override
        public Field getListValueField() {
            return new MessageField(null, null, null);
        }
    }

    public static class BaseReport extends BaseFields.ObjectField implements Report {
        private java.util.List<Message> successes;
        private java.util.List<Message> warnings;
        private java.util.List<Message> failures;

        public static final java.util.List<Field> FIELDS = ImmutableList.of(new MessageList(SUCCESSES),
                new MessageList(FAILURES),
                new MessageList(WARNINGS));

        public BaseReport() {
            super(REPORT);
            this.successes = new ArrayList<>();
            this.warnings = new ArrayList<>();
            this.failures = new ArrayList<>();
        }

        @Override
        public String description() {
            return "A report containing the results of the task requested.";
        }

        @Override
        public java.util.List<Field> getFields() {
            return FIELDS;
        }

        @Override
        public java.util.List<Message> getSuccesses() {
            return successes;
        }

        @Override
        public java.util.List<Message> getWarnings() {
            return warnings;
        }

        @Override
        public java.util.List<Message> getFailures() {
            return failures;
        }

        @Override
        public java.util.List<Message> getMessages() {
            return Stream.of(successes, failures, warnings)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
        }

        public BaseReport messages(java.util.List<Message> messages) {
            messages.stream().forEach(this::message);
            return this;
        }

        public BaseReport messages(Message... messages) {
            Arrays.asList(messages).stream().forEach(this::message);
            return this;
        }

        public BaseReport message(Message message) {
            switch (message.getMessageType()) {
            case SUCCESS:
                successes.add(message);
                break;
            case FAILURE:
                failures.add(message);
                break;
            case WARNING:
                warnings.add(message);
                break;
            }
            return this;
        }
    }

    public static class Pid extends StringField {
        public static final String PID = "pid";

        public Pid() {
            super(PID);
        }

        public Pid(String fieldName) {
            super(fieldName);
        }

        @Override
        public String description() {
            return "A unique id used for persisting.";
        }
    }
}
