package org.codice.ddf.admin.configuration.impl;

import org.codice.ddf.admin.configuration.api.theme.SystemBannerConfiguration;

public class SystemBannerConfigurationImpl implements SystemBannerConfiguration {

  public SystemBannerConfigurationImpl() {}

  @Override
  public boolean isEnabled() {
    return false;
  }

  @Override
  public boolean showOncePerSession() {
    return false;
  }

  @Override
  public String getSystemUsageTitle() {
    return null;
  }

  @Override
  public String getSystemUsageMessage() {
    return null;
  }

  @Override
  public String getHeaderText() {
    return null;
  }

  @Override
  public String getFooterText() {
    return null;
  }

  @Override
  public String getTextColor() {
    return null;
  }

  @Override
  public String getBackgroundColor() {
    return null;
  }
}
