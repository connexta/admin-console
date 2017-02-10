package org.codice.ddf.admin.query.api;

import java.util.List;

public interface ActionReport {
    ActionMessage getSuccessMessage();
    List<ActionMessage> getFailureMessages();
    List<ActionMessage> getWarningsMessages();
    List<Field> getValues();
    List<Field> getReturnTypes();
}
