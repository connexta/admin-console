package org.codice.ddf.admin.query.api.field;

import java.util.List;

public interface Report extends Field {
    String REPORT = "Report";
    String SUCCESSES = "successes";
    String FAILURES  = "failures";
    String WARNINGS = "warnings";

    List<Message> getSuccesses();

    List<Message> getWarnings();

    List<Message> getFailures();

    List<Message> getMessages();
}
