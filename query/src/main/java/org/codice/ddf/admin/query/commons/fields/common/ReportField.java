package org.codice.ddf.admin.query.commons.fields.common;


import org.codice.ddf.admin.query.commons.fields.common.message.MessageField;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.commons.fields.base.BaseObjectField;
import org.codice.ddf.admin.query.commons.fields.common.message.MessageListField;

import com.google.common.collect.ImmutableList;

public class ReportField extends BaseObjectField {

    public static final String FIELD_NAME = "report";
    public static final String FIELD_TYPE_NAME = "Report";
    public static final String DESCRIPTION = "A report containing the results of the task requested.";

    private MessageListField successes;
    private MessageListField warnings;
    private MessageListField failures;

    public ReportField() {
        super(FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
        this.successes = new MessageListField("successes");
        this.warnings = new MessageListField("warnings");
        this.failures = new MessageListField( "failures");
    }

    @Override
    public List<Field> getFields() {
        return ImmutableList.of(successes, warnings, failures);
    }

    public List<MessageField> successes() {
        return new ArrayList<>(successes.getList());
    }

    public  List<MessageField> warnings() {
        return new ArrayList<>(warnings.getList());
    }

    public  List<MessageField> failures() {
        return new ArrayList<>(failures.getList());
    }

    public  List<MessageField> messages() {
        return Stream.of(successes.getList(), failures.getList(), warnings.getList())
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public ReportField messages(List<MessageField> messages) {
        messages.stream().forEach(this::message);
        return this;
    }

    public ReportField messages(MessageField... messages) {
        Arrays.asList(messages).stream().forEach(this::message);
        return this;
    }

    public ReportField message(MessageField message) {
        switch (message.messageType()) {
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
