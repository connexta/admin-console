package org.codice.ddf.admin.configuration.api.theme;

public interface SystemBannerConfiguration {
  // TODO: tbatie - 3/2/18 - Is this for the modal or the classifications banners in the admin
  // console?

  boolean isEnabled();

  boolean showOncePerSession();

  String getSystemUsageTitle();

  String getSystemUsageMessage();

  String getHeaderText();

  String getFooterText();

  String getTextColor();

  String getBackgroundColor();
}
