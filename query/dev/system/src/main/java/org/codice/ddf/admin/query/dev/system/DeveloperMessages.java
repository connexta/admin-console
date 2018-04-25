package org.codice.ddf.admin.query.dev.system;

import org.codice.ddf.admin.api.report.ErrorMessage;
import org.codice.ddf.admin.common.report.message.ErrorMessageImpl;

public class DeveloperMessages {

  public static final String FAILED_FEATURE_INSTALL = "FAILED_FEATURE_INSTALL";

  public static final String FAILED_FEATURE_UNINSTALL = "FAILED_FEATURE_UNINSTALL";


  private DeveloperMessages() {}

  public static ErrorMessage failedFeatureInstall() {
    return new ErrorMessageImpl(FAILED_FEATURE_INSTALL);
  }

  public static ErrorMessage failedFeatureUninstall() {
    return new ErrorMessageImpl(FAILED_FEATURE_UNINSTALL);
  }
}
