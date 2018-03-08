package org.codice.ddf.admin.configuration.api;

import java.util.List;
import org.codice.ddf.admin.configuration.api.security.GuestClaimsConfiguration;
import org.codice.ddf.admin.configuration.api.security.StsConfiguration;
import org.codice.ddf.admin.configuration.api.security.TemporaryUserConfiguration;
import org.codice.ddf.admin.configuration.api.system.SystemInformationConfiguration;
import org.codice.ddf.admin.configuration.api.theme.SystemBannerConfiguration;

public interface DefaultConfigurationProfile {

  String getId();

  String getName();

  String getDescription();

  GuestClaimsConfiguration getGuestClaims();

  List<TemporaryUserConfiguration> getTemporaryUsers();

  SystemBannerConfiguration getSystemBannerConfiguration();

  SystemInformationConfiguration getSystemInformationConfiguration();

  StsConfiguration getStsConfiguration();
}
