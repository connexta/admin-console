package org.codice.ddf.admin.query.commons.fields.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.codice.ddf.admin.query.api.fields.Field;
import org.codice.ddf.admin.query.api.fields.Message;
import org.codice.ddf.admin.query.api.fields.Report;
import org.codice.ddf.admin.query.commons.fields.base.ObjectField;
import org.codice.ddf.admin.query.commons.fields.common.message.MessageListField;

import com.google.common.collect.ImmutableList;

public class ReportField extends ObjectField implements Report {
    private List<Message> successes;
    private List<Message> warnings;
    private List<Message> failures;

    public static final List<Field> FIELDS = ImmutableList.of(new MessageListField(SUCCESSES),
            new MessageListField(FAILURES),
            new MessageListField(WARNINGS));

    public ReportField() {
        super("report", REPORT);
        this.successes = new ArrayList<>();
        this.warnings = new ArrayList<>();
        this.failures = new ArrayList<>();
    }

    @Override
    public String description() {
        return "A report containing the results of the task requested.";
    }

    @Override
    public List<Field> getFields() {
        return FIELDS;
    }

    @Override
    public List<Message> getSuccesses() {
        return successes;
    }

    @Override
    public List<Message> getWarnings() {
        return warnings;
    }

    @Override
    public List<Message> getFailures() {
        return failures;
    }

    @Override
    public List<Message> getMessages() {
        return Stream.of(successes, failures, warnings)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public ReportField messages(List<Message> messages) {
        messages.stream().forEach(this::message);
        return this;
    }

    public ReportField messages(Message... messages) {
        Arrays.asList(messages).stream().forEach(this::message);
        return this;
    }

    public ReportField message(Message message) {
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
