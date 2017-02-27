package org.codice.ddf.admin.query.api.fields;

import java.util.List;

public interface ReportField extends Field {
    List<MessageField> getSuccesses();
    List<MessageField> getWarnings();
    List<MessageField> getFailures();
    List<MessageField> getMessages();
}
