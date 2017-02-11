package org.codice.ddf.admin.query.commons;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codice.ddf.admin.query.api.ActionMessage;
import org.codice.ddf.admin.query.api.ActionReport;

public class DefaultActionReport implements ActionReport {

    private List<ActionMessage> successes;
    private List<ActionMessage> warnings;
    private List<ActionMessage> failures;
    private Map<String, Object> values;

    public DefaultActionReport(){
        successes = new ArrayList<>();
        warnings = new ArrayList<>();
        failures = new ArrayList<>();
        values = new HashMap<>();
    }

    @Override
    public List<ActionMessage> getSuccessMessages() {
        return successes;
    }

    @Override
    public List<ActionMessage> getFailureMessages() {
        return failures;
    }

    @Override
    public List<ActionMessage> getWarningsMessages() {
        return warnings;
    }

    @Override
    public Map<String, Object> getValues() {
        return values;
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> report = new HashMap<>();
        report.put(SUCCESSES, getSuccessMessages());
        report.put(FAILURES, getFailureMessages());
        report.put(WARNINGS, getWarningsMessages());
        report.putAll(getValues());
        return report;
    }

    public List<ActionMessage> getMessages() {
        List<ActionMessage> allMessages = new ArrayList<>();
        if(successes != null) {
            allMessages.addAll(successes);
        }

        if(failures != null) {
            allMessages.addAll(failures);
        }

        if(warnings != null) {
            allMessages.addAll(warnings);
        }

        return allMessages;
    }

    public DefaultActionReport messages(List<ActionMessage> messages) {
        messages.stream().forEach(this::message);
        return this;
    }

    public DefaultActionReport messages(ActionMessage... messages) {
        Arrays.asList(messages).stream().forEach(this::message);
        return this;
    }

    public DefaultActionReport message(ActionMessage message) {
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

    public DefaultActionReport addValues(Map<String, Object> values) {
        this.values.putAll(values);
        return this;
    }

    public DefaultActionReport addValue(String key, Object value) {
        this.values.put(key, value);
        return this;
    }

}
