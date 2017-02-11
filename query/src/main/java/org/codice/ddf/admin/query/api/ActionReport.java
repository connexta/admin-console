package org.codice.ddf.admin.query.api;

import java.util.List;
import java.util.Map;

public interface ActionReport {
    String SUCCESSES = "successes";
    String FAILURES = "failures";
    String WARNINGS = "warnings";

    List<ActionMessage> getMessages();
    List<ActionMessage> getSuccessMessages();
    List<ActionMessage> getFailureMessages();
    List<ActionMessage> getWarningsMessages();
    Map<String, Object> getValues();
    Map<String, Object> toMap();
}
