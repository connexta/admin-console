package org.codice.ddf.admin.query.commons.fields.common;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.api.fields.MessageField;
import org.codice.ddf.admin.query.api.fields.ReportField;
import org.codice.ddf.admin.query.commons.fields.base.BaseObjectField;
import org.codice.ddf.admin.query.commons.fields.common.message.BaseMessageField;
import org.codice.ddf.admin.query.commons.fields.common.message.MessageListField;

import com.google.common.collect.ImmutableList;

public class BaseReportField extends BaseObjectField implements ReportField {

    public static final String FIELD_NAME = "report";
    public static final String FIELD_TYPE_NAME = "Report";
    public static final String DESCRIPTION = "A report containing the results of the task requested.";

    String SUCCESSES = "successes";
    String FAILURES  = "failures";
    String WARNINGS = "warnings";
    private MessageListField successes;
    private MessageListField warnings;
    private MessageListField failures;

    public BaseReportField() {
        super(FIELD_NAME, FIELD_TYPE_NAME, DESCRIPTION);
        this.successes = new MessageListField(SUCCESSES);
        this.warnings = new MessageListField(WARNINGS);
        this.failures = new MessageListField(FAILURES);
    }

    @Override
    public List<Field> getFields() {
        return ImmutableList.of(successes, warnings, failures);
    }

    @Override
    public List<MessageField> getSuccesses() {
        return successes.getMessages();
    }

    @Override
    public  List<MessageField> getWarnings() {
        return warnings.getMessages();
    }

    @Override
    public  List<MessageField> getFailures() {
        return failures.getMessages();
    }

    @Override
    public  List<MessageField> getMessages() {
        return Stream.of(successes.getMessages(), failures.getMessages(), warnings.getMessages())
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public BaseReportField messages(List<BaseMessageField> messages) {
        messages.stream().forEach(this::message);
        return this;
    }

    public BaseReportField messages(BaseMessageField... messages) {
        Arrays.asList(messages).stream().forEach(this::message);
        return this;
    }

    public BaseReportField message(BaseMessageField message) {
        switch (message.getMessageType()) {
        case SUCCESS:
            successes.addField(message);
            break;
        case FAILURE:
            failures.addField(message);
            break;
        case WARNING:
            warnings.addField(message);
            break;
        }
        return this;
    }
}
