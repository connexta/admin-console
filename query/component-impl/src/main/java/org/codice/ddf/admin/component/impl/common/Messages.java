package org.codice.ddf.admin.component.impl.common;

import java.util.List;
import org.codice.ddf.admin.api.report.ErrorMessage;
import org.codice.ddf.admin.common.report.message.ErrorMessageImpl;

public class Messages {

  public static final String COMPONENT_NOT_FOUND = "COMPONENT_NOT_FOUND";

  public static final String FAILED_TO_START_COMPONENT = "FAILED_TO_START_COMPONENT";

  private Messages() {}

  public static ErrorMessage componentNotFound(List<Object> path) {
    return new ErrorMessageImpl(COMPONENT_NOT_FOUND, path);
  }

  public static ErrorMessage failedToStartComponent(List<Object> path) {
    return new ErrorMessageImpl(FAILED_TO_START_COMPONENT, path);
  }
}
