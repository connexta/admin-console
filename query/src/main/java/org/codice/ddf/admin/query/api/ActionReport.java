package org.codice.ddf.admin.query.api;

import java.util.List;
import java.util.Map;

public interface ActionReport {
    List<ActionMessage> getMessages();
    List<ActionMessage> getSuccessMessages();
    List<ActionMessage> getFailureMessages();
    List<ActionMessage> getWarningsMessages();
    Map<String, Object> getValues();
    Map<String, Object> toMap();
}
