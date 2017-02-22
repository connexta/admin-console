package org.codice.ddf.admin.query.api.fields;

import java.util.List;

public interface Report extends Field {
    List<Message> getSuccesses();
    List<Message> getWarnings();
    List<Message> getFailures();
    List<Message> getMessages();
}
